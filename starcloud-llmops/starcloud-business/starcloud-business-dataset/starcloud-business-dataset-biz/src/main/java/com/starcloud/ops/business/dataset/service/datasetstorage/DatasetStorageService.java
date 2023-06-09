package com.starcloud.ops.business.dataset.service.datasetstorage;

import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;

/**
 * 数据集源数据存储 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetStorageService {
    /**
     * 上传数据集源数据
     *
     * @param createReqVO 源数据上传
     * @return 数据集源数据UID
     */
    String addSourceData(DatasetStorageCreateReqVO createReqVO);

    /**
     * 根据文件编号获取文件存储信息
     * @param UID
     * @return
     */
    DatasetStorageUpLoadRespVO getDatasetStorageByUID(String UID);


    /**
     * 文件预览
     * @param UID
     * @return
     */
    String previewUpLoadFile(String UID);
}