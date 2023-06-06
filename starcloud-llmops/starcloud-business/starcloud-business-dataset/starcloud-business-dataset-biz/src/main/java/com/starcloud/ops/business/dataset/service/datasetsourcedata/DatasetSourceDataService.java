package com.starcloud.ops.business.dataset.service.datasetsourcedata;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo.DatasetSourceDataUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasetsourcedata.DatasetSourceDataDO;

/**
 * 数据集源数据 Service 接口
 *
 * @author 芋道源码
 */
public interface DatasetSourceDataService {

    /**
     * 创建数据集源数据
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDatasetSourceData(@Valid DatasetSourceDataCreateReqVO createReqVO);

    /**
     * 更新数据集源数据
     *
     * @param updateReqVO 更新信息
     */
    void updateDatasetSourceData(@Valid DatasetSourceDataUpdateReqVO updateReqVO);

    /**
     * 删除数据集源数据
     *
     * @param id 编号
     */
    void deleteDatasetSourceData(Long id);

    /**
     * 获得数据集源数据
     *
     * @param id 编号
     * @return 数据集源数据
     */
    DatasetSourceDataDO getDatasetSourceData(Long id);

    /**
     * 获得数据集源数据列表
     *
     * @param ids 编号
     * @return 数据集源数据列表
     */
    List<DatasetSourceDataDO> getDatasetSourceDataList(Collection<Long> ids);

    /**
     * 获得数据集源数据分页
     *
     * @param pageReqVO 分页查询
     * @return 数据集源数据分页
     */
    PageResult<DatasetSourceDataDO> getDatasetSourceDataPage(DatasetSourceDataPageReqVO pageReqVO);



}