package com.starcloud.ops.business.dataset.core.init;


import com.starcloud.ops.business.dataset.enums.DataSetStatusEnum;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;

/**
 *
 *  数据集，统一数据处理、数据集合的调用等功能
 *
 * @author Alan Cusack
 */
public interface DatasetHandle {
    /**
     * 数据集数据处理
     * @apiNote 这一步发生在数据集创建后 针对不同数据的处理
     * @return 根据类型返回结果
     */
    Boolean dataProcessing();

    /**
     * 获取当前数据状态
     * @return 数据集状态 {@link DataSetStatusEnum}
     */
    DataSetStatusEnum getDatasetStatus();

    /**
     * 获取源数据状态
     * @return 数据集状态 {@link DataSetStatusEnum}
     */
    DataSetStatusEnum getSourceDataStatus();
}
