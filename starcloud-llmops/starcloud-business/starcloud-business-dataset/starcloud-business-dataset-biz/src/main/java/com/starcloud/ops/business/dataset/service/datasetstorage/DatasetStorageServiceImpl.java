package com.starcloud.ops.business.dataset.service.datasetstorage;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.convert.datasetstorage.DatasetStorageConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasetstorage.DatasetStorageMapper;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.DATASET_STORAGE_NOT_EXISTS;

/**
 * 数据集源数据存储 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class DatasetStorageServiceImpl implements DatasetStorageService {

    @Resource
    private FileConfigService fileConfigService;
    @Resource
    private FileMapper fileMapper;

    @Resource
    private DatasetStorageMapper datasetStorageMapper;


    /**
     * @param  createReqVO 源数据上传
     * @return UID
     */
    @Override
    public String addSourceData(DatasetStorageCreateReqVO createReqVO) {
        String uid = DatasetUID.getDatasetUID();
        createReqVO.setUid(uid);
        DatasetStorageDO convert = DatasetStorageConvert.convert(createReqVO);
        // 数据入库
        datasetStorageMapper.insert(convert);
        return uid;
    }


    /**
     * 根据文件编号获取文件存储信息
     *
     * @param UID
     * @return
     */
    @Override
    public DatasetStorageUpLoadRespVO getDatasetStorageByUID(String UID) {
        // 封装查询条件
        LambdaQueryWrapper<DatasetStorageDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetStorageDO::getUid, UID);
        wrapper.eq(DatasetStorageDO::getTenantId, getLoginUser().getId());

        DatasetStorageDO datasetStorageDO = datasetStorageMapper.selectOne(wrapper);
        // 文件不存在，则报错
        if (ObjectUtil.isEmpty(datasetStorageDO)) {
            log.error("[getDatasetStorageInfo][获取源数据失败，文件不存在：文件UID({})|用户ID({})|租户ID({})", UID, getLoginUserId(), getLoginUser().getTenantId());
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
        // 根据ID获取文件信息
        FileDO fileDO = fileMapper.selectById(datasetStorageDO.getStorageKey());
        if (ObjectUtil.isEmpty(fileDO)) {
            log.error("[getDatasetStorageInfo][获取源数据失败，文件不存在：文件UID({})|用户ID({})|租户ID({})", UID, getLoginUserId(), getLoginUser().getTenantId());
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
        DatasetStorageUpLoadRespVO datasetStorageUpLoadRespVO = DatasetStorageConvert.convert2LoadRespVO(datasetStorageDO);
        datasetStorageUpLoadRespVO.setStorageKey(fileDO.getUrl());
        // 数据转换
        return datasetStorageUpLoadRespVO;
    }

    /**
     * 文件预览
     *
     * @param UID
     * @return
     */
    @Override
    public String previewUpLoadFile(String UID) {

        return null;
    }

    private void validateDatasetStorageExists(Long id) {
        if (datasetStorageMapper.selectById(id) == null) {
            throw exception(DATASET_STORAGE_NOT_EXISTS);
        }
    }

}