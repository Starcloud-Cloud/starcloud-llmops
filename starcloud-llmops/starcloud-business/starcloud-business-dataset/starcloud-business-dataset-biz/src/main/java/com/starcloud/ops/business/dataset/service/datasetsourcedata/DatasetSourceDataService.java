package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.SourceDataBatchCreateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 数据集源数据 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetSourceDataService {

    /**
     * 创建数据集源数据
     *
     * @param datasetId 数据集 ID
     * @param storageId 存储 ID
     * @param sourceName 资源名称
     * @param wordCount 字符数
     * @return 编号
     */
    Long createDatasetSourceData(String datasetId, Long storageId, String sourceName, Long wordCount);

    /**
     * 创建数据集源数据
     *
     * @param batchCreateReqVOS 创建信息
     * @return 编号
     */
    List<Long> batchCreateDatasetSourceData(String datasetId, List<SourceDataBatchCreateReqVO> batchCreateReqVOS);


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
     * 归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    void archivedDatasetSourceData( String uid);

    /**
     * 取消归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    void unArchivedDatasetSourceData( String uid);

    /**
     * 取消归档数据集源数据
     *
     * @param uid 数据集源数据编号
     */
    void updateDatasourceStatus( String uid,Integer status);


}