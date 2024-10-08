package com.starcloud.ops.business.dataset.service.datasets;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import com.starcloud.ops.business.dataset.pojo.dto.UserBaseDTO;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 数据集 Service 接口
 *
 * @author Alan Cusack
 */
public interface DatasetsService {

    // ============================新增数据=================================

    /**
     * 创建数据集
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    String createDatasets(@Validated DatasetsCreateReqVO createReqVO);

    /**
     * 根据用户会话创建数据集
     *
     * @param appId 应用 ID
     * @param sessionId  会话 ID
     * @return Boolean
     */
    DatasetsDO createDatasetsBySession(String appId, String sessionId, UserBaseDTO baseDBHandleDTO);

    /**
     * 根据用户应用创建数据集
     *
     * @param appId 应用 ID
     * @return Boolean
     */
    DatasetsDO createDatasetsByApp(String appId);


    // ============================数据校验=================================
    /***
     * 验证应用下是否存在数据集
     * @param id 主键 ID
     */
    void validateDatasetsExists(Long id);

    /**
     * 根据数据编号判断数据是否存在
     * @param UID 数据编号
     */
    void validateDatasetsExists(String UID);

    /***
     * 验证应用下是否存在数据集
     * @param appId 应用 ID
     */
    Boolean validateAppDatasetsExists(String appId);


    /***
     * 验证会话下是否存在数据集
     * @param appId 应用 ID
     * @param sessionId  会话 ID
     */
    Boolean validateSessionDatasetsExists(String appId, String sessionId);

    // ============================数据查询=================================

    /**
     * 获得数据集
     *
     * @param uid 数据集编号
     * @return 数据集
     */
    DatasetsDO getDataByUid(String uid);

    /**
     * 根据主键 ID 获取数据
     * @param id 主键 ID
     * @return DatasetsDO
     */
    DatasetsDO getDataById(Long id);

    /**
     * 获得数据集分页
     *
     * @param pageReqVO 分页查询
     * @return 数据集分页
     */
    PageResult<DatasetsDO> getDatasetsPage(DatasetsPageReqVO pageReqVO);


    /**
     * 根据应用 ID 获取数据集详情
     *
     * @param appId 应用 ID
     * @return 数据集
     */
    DatasetsDO getDatasetInfoByAppId(String appId);

    /**
     * 根据应用ID 获取应用下所有的获取数据集详情 包括应用会话下的数据集
     *
     * @param appId 应用 ID
     * @return 数据集
     */
    List<DatasetsDO> getAllDatasetInfoByAppId(String appId);


    /**
     * 查询会话下数据集详情
     *
     * @param appId 应用 ID
     * @param sessionId  会话 ID
     * @return 数据集
     */
    DatasetsDO getDatasetInfoBySession(String appId, String sessionId);


    // ============================数据更新=================================

    /**
     * 更新数据集
     *
     * @param updateReqVO 更新信息
     */
    void updateDatasets(@Validated DatasetsUpdateReqVO updateReqVO);
    /**
     * 启用数据集
     *
     * @param uid 数据集编号
     */
    void enableDatasets(String uid);

    /**
     * 停用数据集
     *
     * @param uid 数据集编号
     */
    void offDatasets(String uid);

    /**
     * 删除数据集
     *
     * @param uid 数据集编号
     */
    void deleteDatasets(String uid);
}