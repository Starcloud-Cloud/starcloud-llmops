package com.starcloud.ops.business.dataset.service.datasets;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.AppApi;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.convert.datasets.DatasetsConvert;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.dal.mysql.datasets.DatasetsMapper;
import com.starcloud.ops.business.dataset.enums.DatasetPermissionEnum;
import com.starcloud.ops.business.dataset.enums.DatasetProviderEnum;
import com.starcloud.ops.business.dataset.pojo.dto.BaseDBHandleDTO;
import com.starcloud.ops.business.dataset.util.dataset.DatasetUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    @Lazy
    private AppApi appApi;

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

        datasetsDO = datasetsMapper.selectOne(wrapper);

        if (datasetsDO == null) {
            throw exception(DATASETS_ERROR_REPEAT, uid);
        }
        return datasetsDO;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public DatasetsDO getDataById(Long id) {
        return datasetsMapper.selectById(id);
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


    @Override
    public void validateDatasetsExists(Long id) {
        if (datasetsMapper.selectOne(Wrappers.lambdaQuery(DatasetsDO.class).eq(DatasetsDO::getId, id)) == null) {
            throw exception(DATASETS_NOT_EXISTS);
        }
    }

    /**
     * 根据应用 ID 获取数据集详情
     *
     * @param appId 应用 ID
     * @return 数据集
     */
    @Override
    public DatasetsDO getDatasetInfoByAppId(String appId) {

        List<DatasetsDO> datasetsDOS = datasetsMapper.selectList(Wrappers.lambdaQuery(DatasetsDO.class)
                .eq(DatasetsDO::getAppId, appId)
                .isNull(DatasetsDO::getSessionId));

        if (CollUtil.isEmpty(datasetsDOS)) {
            throw exception(DATASETS_APPID_NOT_EXISTS);
        }
        if (datasetsDOS.size() > 1) {
            throw exception(DATASETS_APPID_REPEAT_BIND);
        }
        return datasetsDOS.get(0);
    }


    @Override
    public Boolean validateAppDatasetsExists(String appId) {
        DatasetsDO datasetsDO = datasetsMapper.selectOne(
                Wrappers.lambdaQuery(DatasetsDO.class)
                        .eq(DatasetsDO::getAppId, appId)
                        .isNull(DatasetsDO::getSessionId));
        return datasetsDO != null;
    }

    /**
     * 根据用户应用创建数据集
     *
     * @param appId 应用 ID
     * @return Boolean
     */
    @Override
    public DatasetsDO createDatasetsByApp(String appId) {
        DatasetsDO datasetsDO = new DatasetsDO();
        datasetsDO.setUid(DatasetUID.createDatasetUID());
        datasetsDO.setName(String.format("应用%s的数据集", appId));
        datasetsDO.setDescription(String.format("应用%s的数据集", appId));
        datasetsDO.setAppId(appId);
        datasetsDO.setProvider(DatasetProviderEnum.SYSTEM.getName());
        datasetsDO.setPermission(DatasetPermissionEnum.TEAM_OWNED.getStatus());
        datasetsDO.setEnabled(true);
        // 数据插入
        datasetsMapper.insert(datasetsDO);

        return datasetsDO;
    }


    @TenantIgnore
    @Override
    public DatasetsDO getDatasetInfoBySession(String appId, String sessionId) {
        try {
            TenantContextHolder.getRequiredTenantId();
        } catch (Exception e) {
            AppRespVO appRespVO = appApi.get(appId);
            TenantContextHolder.setTenantId(appRespVO.getTenantId());
            TenantContextHolder.setIgnore(false);
            UserContextHolder.setUserId(Long.valueOf(appRespVO.getCreator()));
        }

        List<DatasetsDO> datasetsDOS = datasetsMapper.selectList(
                Wrappers.lambdaQuery(DatasetsDO.class)
                        .eq(DatasetsDO::getAppId, appId)
                        .eq(DatasetsDO::getSessionId, sessionId));
        if (CollUtil.isEmpty(datasetsDOS)) {
            throw exception(DATASETS_CONVERSATION_NOT_EXISTS);
        }
        if (datasetsDOS.size() > 1) {
            throw exception(DATASETS_CONVERSATION_REPEAT_BIND);
        }
        return datasetsDOS.get(0);

    }


    /***
     * 验证会话下是否存在数据集
     * @param appId 应用 ID
     * @param sessionId  会话 ID
     */
    @Override
    public Boolean validateSessionDatasetsExists(String appId, String sessionId) {
        DatasetsDO datasetsDO = datasetsMapper.selectOne(
                Wrappers.lambdaQuery(DatasetsDO.class)
                        .eq(DatasetsDO::getAppId, appId)
                        .eq(DatasetsDO::getSessionId, sessionId));
        return datasetsDO != null;
    }

    /**
     * 根据用户会话创建数据集
     *
     * @param appId     应用 ID
     * @param sessionId 会话 ID
     * @return Boolean
     */
    public DatasetsDO createDatasetsBySession(String appId, String sessionId, BaseDBHandleDTO baseDBHandleDTO) {
        DatasetsDO datasetsDO = new DatasetsDO();
        datasetsDO.setUid(DatasetUID.createDatasetUID());
        datasetsDO.setName(String.format("会话%s的数据集", sessionId));
        datasetsDO.setDescription(String.format("会话%s的数据集", sessionId));
        datasetsDO.setAppId(appId);
        datasetsDO.setSessionId(sessionId);
        datasetsDO.setProvider(DatasetProviderEnum.SYSTEM.getName());
        datasetsDO.setPermission(DatasetPermissionEnum.PRIVATELY_OWNED.getStatus());
        datasetsDO.setEnabled(true);


        datasetsDO.setCreator(String.valueOf(baseDBHandleDTO.getCreator()));
        datasetsDO.setCreator(String.valueOf(baseDBHandleDTO.getUpdater()));
        datasetsDO.setTenantId(baseDBHandleDTO.getTenantId());
        datasetsDO.setEndUser(baseDBHandleDTO.getEndUser());
        // 数据插入
        datasetsMapper.insert(datasetsDO);

        return datasetsDO;
    }
}
