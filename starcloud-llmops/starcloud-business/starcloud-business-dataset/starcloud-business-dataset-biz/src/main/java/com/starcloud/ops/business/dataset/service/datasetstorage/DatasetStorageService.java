package com.starcloud.ops.business.dataset.service.datasetstorage;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStoragePageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageRespVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetstorage.vo.DatasetStorageUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetstorage.DatasetStorageDO;

/**
 * 数据集源数据存储 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetStorageService {

    /**
     * 创建数据集源数据存储
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDatasetStorage(@Valid DatasetStorageCreateReqVO createReqVO);

    /**
     * 更新数据集源数据存储
     *
     * @param updateReqVO 更新信息
     */
    void updateDatasetStorage(@Valid DatasetStorageUpdateReqVO updateReqVO);

    /**
     * 删除数据集源数据存储
     *
     * @param id 编号
     */
    void deleteDatasetStorage(Long id);

    /**
     * 获得数据集源数据存储
     *
     * @param id 编号
     * @return 数据集源数据存储
     */
    DatasetStorageDO getDatasetStorage(Long id);

    /**
     * 获得数据集源数据存储列表
     *
     * @param ids 编号
     * @return 数据集源数据存储列表
     */
    List<DatasetStorageDO> getDatasetStorageList(Collection<Long> ids);

    /**
     * 获得数据集源数据存储分页
     *
     * @param pageReqVO 分页查询
     * @return 数据集源数据存储分页
     */
    PageResult<DatasetStorageDO> getDatasetStoragePage(DatasetStoragePageReqVO pageReqVO);

    /**
     * 上传数据集源数据
     *
     * @param respVO 源数据上传
     * @return 数据集源数据存储分页
     */
    Boolean uploadSourceData(@Valid DatasetStorageRespVO respVO);


}