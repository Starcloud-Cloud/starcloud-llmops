package com.starcloud.ops.business.dataset.service.datasets;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsCreateReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsPageReqVO;
import com.starcloud.ops.business.dataset.controller.admin.datasets.vo.DatasetsUpdateReqVO;
import com.starcloud.ops.business.dataset.dal.dataobject.datasets.DatasetsDO;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 数据集 Service 接口
 *
 * @author Alan Cusack
 */
public interface DatasetsService {

    /**
     * 创建数据集
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    String createDatasets(@Validated DatasetsCreateReqVO createReqVO);

    /**
     * 创建数据集
     *
     * @return 编号
     *
     */
    String createWechatDatasets();

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


    void validateDatasetsExists(String UID);


    DatasetsDO getDataSetBaseDo(String UID);


}