package com.starcloud.ops.business.dataset.service.datasetstorage;

import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageBaseVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpLoadRespVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;

/**
 * 数据集源数据存储 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetStorageService {

    /**
     * 根据 ID 查询数据
     *
     * @param id 主键 ID
     * @return DatasetStorageDO
     */
    DatasetStorageDO selectDataById(Long id);

    /**
     * 根据 ID 获取基础数据
     *
     * @param id 主键 ID
     * @return DatasetStorageBaseVO
     */
    DatasetStorageBaseVO selectBaseDataById(Long id);

    /**
     * 根据文件编号获取文件存储信息
     *
     * @param UID
     * @return
     */
    DatasetStorageUpLoadRespVO getDatasetStorageByUID(String UID);


}