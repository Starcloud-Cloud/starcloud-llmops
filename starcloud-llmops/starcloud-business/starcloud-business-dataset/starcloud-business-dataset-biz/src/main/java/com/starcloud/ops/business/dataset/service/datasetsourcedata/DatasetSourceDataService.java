package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 数据集源数据 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetSourceDataService {


    /**
     * 根据主键 ID 获取数据
     *
     * @param id 数据集源数据ID
     */
    DatasetSourceDataDO selectDataById(Long id);

    /**
     * 上传文件-支持批量上传
     *
     * @return 编号
     */
    SourceDataUploadDTO uploadFilesSourceData(UploadFileReqVO reqVO);

    /**
     * 上传URL-支持批量上传
     *
     * @return 编号
     */
    List<SourceDataUploadDTO> uploadUrlsSourceData(UploadUrlReqVO reqVO);

    /**
     * 上传字符-支持批量上传
     *
     * @return 编号
     */
    List<SourceDataUploadDTO> uploadCharactersSourceData(UploadCharacterReqVO reqVOS);

    /**
     * 更新数据集源数据
     *
     * @param updateReqVO 更新信息
     */
    void updateDatasetSourceData(@Validated DatasetSourceDataUpdateReqVO updateReqVO);

    /**
     * 删除数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    void deleteDatasetSourceData(String uid);

    /**
     * 禁用源数据
     *
     * @param uid 数据集源数据编号
     */
    void disable(String uid);

    /**
     * 启用源数据
     *
     * @param uid 数据集源数据编号
     */
    void enable(String uid);

    /**
     * 获得数据集源数据分页
     *
     * @param pageReqVO 分页查询
     * @return 数据集源数据分页
     */
    PageResult<DatasetSourceDataDO> getDatasetSourceDataPage(DatasetSourceDataPageReqVO pageReqVO);

    /**
     * 获得数据集源数据列表
     *
     * @param datasetId 数据集 ID
     * @return 数据集源数据列表
     */
    List<ListDatasetSourceDataRespVO> getDatasetSourceDataList(Long datasetId, Integer dataModel);


    /**
     * 获取分块内容
     *
     * @param reqVO
     * @return
     */
    PageResult<DatasetSourceDataSplitPageRespVO> getSplitDetails(DatasetSourceDataSplitPageReqVO reqVO);

    /**
     * 归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    void archivedDatasetSourceData(String uid);

    /**
     * 取消归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    void unArchivedDatasetSourceData(String uid);

    /**
     * 更新数据集状态
     *
     * @param uid 数据集源数据编号
     */
    void updateStatusById(Long uid, Integer status, Integer errorCode, String message);

    /**
     * 更新数据集状态
     *
     * @param dataDO 数据集源数据DO
     */
    void updateDatasourceById(DatasetSourceDataDO dataDO);


    /**
     * 通过编号获取数据源详情
     *
     * @param uid    数据集源数据编号
     * @param enable 如果 enable 为 true 则详情内容默认为清洗后的数据
     */
    DatasetSourceDataDetailsInfoVO getSourceDataByUid(String uid, Boolean enable);

    /**
     * 获取主键 ID数据源详情
     *
     * @param id    数据集源数据编号
     * @param enable 如果 enable 为 true 则详情内容默认为清洗后的数据
     */
    DatasetSourceDataDetailsInfoVO getSourceDataById(Long id, Boolean enable);

    /**
     * 获取数据源基础信息 集合
     *
     * @param sourceDataIds 数据集源数据ID 集合
     */
    List<DatasetSourceDataBasicInfoVO> getSourceDataListData(List<Long> sourceDataIds);


    /**
     * 根据数据 UID 获取数据详情
     *
     * @param UID             源数据 UID
     * @param getCleanContent 是否详细内容数据
     * @return 数据集源数据列表
     */
    DatasetSourceDataDetailRespVO getSourceDataDetailByUID(String UID, Boolean getCleanContent);

    /**
     * ====应用的数据========
     * 获得应用下 数据集源数据列表
     *
     * @param appId           应用 ID
     * @param dataModel       数据模型
     * @param getCleanContent 是否详细内容数据
     * @return 数据集源数据列表
     */
    List<DatasetSourceDataDetailRespVO> getApplicationSourceDataList(String appId, Integer dataModel, Boolean getCleanContent);


    /**
     * ====应用下会话的数据========
     * 获得会话下 数据集源数据列表
     *
     * @param appId           应用 ID
     * @param conversationId  会话 ID
     * @param dataModel       数据模型
     * @param getCleanContent 是否详细内容数据
     * @return 数据集源数据列表
     */
    List<DatasetSourceDataDetailRespVO> getSessionSourceDataList(String appId, String conversationId, Integer dataModel, Boolean getCleanContent);

    /**
     * 通过会话上传文件
     *
     * @return 上传结果
     */
    SourceDataUploadDTO uploadFilesSourceDataBySession(UploadFileReqVO reqVO);

    /**
     * 通过会话上传URL
     *
     * @return 上传结果
     */
    List<SourceDataUploadDTO> uploadUrlsSourceDataBySession(UploadUrlReqVO reqVO);

    /**
     * 通过会话上传自定义文本
     *
     * @return 上传结果
     */
    List<SourceDataUploadDTO> uploadCharactersSourceDataBySession(UploadCharacterReqVO reqVOS);
}