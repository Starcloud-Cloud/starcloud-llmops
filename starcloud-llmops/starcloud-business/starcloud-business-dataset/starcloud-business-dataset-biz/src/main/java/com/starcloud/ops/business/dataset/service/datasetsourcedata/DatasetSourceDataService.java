package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.*;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import com.starcloud.ops.business.dataset.service.dto.SourceDataUploadDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 数据集源数据 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetSourceDataService {


    /**
     * 更新数据集状态
     *
     * @param id 数据集源数据ID
     */
    DatasetSourceDataDO selectDataById(Long id);

    /**
     * 上传文件-支持批量上传
     *
     * @return 编号
     */
    SourceDataUploadDTO uploadFilesSourceData(MultipartFile file, UploadFileReqVO reqVO);

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
    List<SourceDataUploadDTO> uploadCharactersSourceData(List<UploadCharacterReqVO> reqVOS);

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
    void updateStatusById(Long uid, Integer status, String message);

    /**
     * 更新数据集状态
     *
     * @param dataDO 数据集源数据DO
     */
    void updateDatasourceById(DatasetSourceDataDO dataDO);


    /**
     * 获取数据源详情
     *
     * @param uid    数据集源数据编号
     * @param enable 如果 enable 为 true 则详情内容默认为清洗后的数据
     */
    DatasetSourceDataDetailsInfoVO getSourceDataListData(String uid, Boolean enable);

    /**
     * 获取数据源基础信息 集合
     *
     * @param sourceDataIds    数据集源数据ID 集合
     */
    List<DatasetSourceDataBasicInfoVO> getSourceDataListData(List<Long> sourceDataIds);



}