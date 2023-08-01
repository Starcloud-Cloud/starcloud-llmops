package com.starcloud.ops.business.dataset.service.datasets;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasets.DatasetsConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasets.DatasetsMapper;
import com.starcloud.ops.business.dataset.enums.DatasetPermissionEnum;
import com.starcloud.ops.business.dataset.enums.DatasetProviderEnum;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.dataset.enums.ErrorCodeConstants.*;

/**
 * 数据集 Service 实现类
 *
 * @author AlanCusack
 */
@Service
@Validated
public class DatasetsServiceImpl implements DatasetsService {

    @Resource
    private DatasetsMapper datasetsMapper;

    @Override
    public String createDatasets(DatasetsCreateReqVO createReqVO) {
        // TODO 校验当前用户权限 是否可以创建数据集
        // 数据非空校验
        if (ObjectUtil.isEmpty(createReqVO)) {
            throw exception(DATASETS_PARAM_NULL);
        }
        // 数据转换
        DatasetsDO datasets = DatasetsConvert.convert(createReqVO, DatasetUID.createDatasetUID());
        // 数据插入
        datasetsMapper.insert(datasets);
        // 返回
        return datasets.getUid();
    }

    /**
     * 根据用户应用创建数据集
     *
     * @param appId 应用 ID
     * @param appName 应用 名称
     * @return Boolean
     */
    @Override
    public Boolean createDatasetsByApplication(String appId, String appName) {
        DatasetsDO datasetsDO = new DatasetsDO();
        datasetsDO.setUid(appId);
        datasetsDO.setName(appName);
        datasetsDO.setDescription(appName);
        datasetsDO.setProvider(DatasetProviderEnum.SYSTEM.getName());
        datasetsDO.setPermission(DatasetPermissionEnum.TEAM_OWNED.getStatus());
        datasetsDO.setEnabled(true);

        // 数据插入
        int result = datasetsMapper.insert(datasetsDO);

        return BooleanUtil.isTrue(1 == result);
    }

    @Override
    public String createWechatDatasets() {

        DatasetsDO datasetsDO = new DatasetsDO();

        datasetsDO.setUid(IdUtil.getSnowflakeNextIdStr());
        datasetsDO.setName("微信数据集" + IdUtil.fastSimpleUUID().substring(0, 6));
        datasetsDO.setDescription("微信数据集" + IdUtil.fastSimpleUUID().substring(0, 6));
        datasetsDO.setPermission(0);
        // 数据插入
        datasetsMapper.insert(datasetsDO);
        // 返回
        return datasetsDO.getUid();

    }


    @Override
    public void updateDatasets(DatasetsUpdateReqVO updateReqVO) {
        // 校验存在
        validateDatasetsExists(updateReqVO.getUid());
        // 更新
        DatasetsDO updateObj = DatasetsConvert.convert(updateReqVO);
        datasetsMapper.update(updateObj, Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getUid, updateReqVO.getUid()));
    }

    /**
     * 启用数据集
     *
     * @param uid 数据集编号
     */
    @Override
    public void enableDatasets(String uid) {
        // 校验存在
        validateDatasetsExists(uid);
        LambdaUpdateWrapper<DatasetsDO> wrapper = Wrappers.lambdaUpdate(DatasetsDO.class);
        wrapper.eq(DatasetsDO::getUid, uid);
        wrapper.set(DatasetsDO::getEnabled, true);
        datasetsMapper.update(null, wrapper);
    }

    /**
     * 停用数据集
     *
     * @param uid 数据集编号
     */
    @Override
    public void offDatasets(String uid) {
        LambdaUpdateWrapper<DatasetsDO> wrapper = Wrappers.lambdaUpdate(DatasetsDO.class);
        wrapper.eq(DatasetsDO::getUid, uid);
        wrapper.set(DatasetsDO::getEnabled, false);
        datasetsMapper.update(null, wrapper);
    }

    @Override
    public void deleteDatasets(String uid) {
        // 校验存在
        validateDatasetsExists(uid);
        // 删除
        datasetsMapper.delete(Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getUid, uid));
    }


    @Override
    public DatasetsDO getDatasets(String uid) {
        LambdaQueryWrapper<DatasetsDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DatasetsDO::getUid, uid);
        DatasetsDO datasetsDO;
        try {
            datasetsDO = datasetsMapper.selectOne(wrapper);
        } catch (RuntimeException e) {
            throw exception(DATASETS_ERROR_REPEAT, uid);
        }
        return datasetsDO;
    }


    @Override
    public PageResult<DatasetsDO> getDatasetsPage(DatasetsPageReqVO pageReqVO) {
        return datasetsMapper.selectPage(pageReqVO);
    }


    /**
     * 数据存在校验
     *
     * @param uid
     */
    @Override
    public void validateDatasetsExists(String uid) {

        if (datasetsMapper.selectOne(Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getUid, uid)) == null) {
            throw exception(DATASETS_NOT_EXISTS);
        }
    }


    /**
     * @param UID
     * @return
     */
    @Override
    public DatasetsDO getDataSetBaseDo(String UID) {
        this.validateDatasetsExists(UID);
        return datasetsMapper.selectOne(Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getUid, UID));
    }
}
