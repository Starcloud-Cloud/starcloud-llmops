package com.starcloud.ops.business.dataset.service.datasets;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsExportReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;

/**
 * 数据集 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetsService {

    /**
     * 创建数据集
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    String createDatasets(@Valid DatasetsCreateReqVO createReqVO);

    /**
     * 更新数据集
     *
     * @param updateReqVO 更新信息
     */
    void updateDatasets(@Valid DatasetsUpdateReqVO updateReqVO);

    /**
     * 启用数据集
     *
     * @param uid 数据集编号
     */
    void enableDatasets( String uid);

    /**
     * 停用数据集
     *
     * @param uid 数据集编号
     */
    void offDatasets( String uid);

    /**
     * 删除数据集
     *
     * @param uid 数据集编号
     */
    void deleteDatasets(String uid);

    /**
     * 获得数据集
     *
     * @param uid 数据集编号
     * @return 数据集
     */
    DatasetsDO getDatasets(String uid);


    /**
     * 获得数据集分页
     *
     * @param pageReqVO 分页查询
     * @return 数据集分页
     */
    PageResult<DatasetsDO> getDatasetsPage(DatasetsPageReqVO pageReqVO);






}