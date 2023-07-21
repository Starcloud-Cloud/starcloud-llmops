package com.starcloud.ops.business.dataset.core.init.impl.document;

import com.starcloud.ops.business.dataset.core.init.AbstractDataset;
import com.starcloud.ops.business.dataset.enums.DataSetStatusEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * 支付宝抽象类， 实现支付宝统一的接口。如退款
 *
 * @author  jason
 */
@Slf4j
public abstract class AbstractDocumentDataset extends AbstractDataset<DatasetDocumentConfig> {


    public AbstractDocumentDataset(String datasetId, DatasetDocumentConfig config) {
        super(datasetId, config);
    }

    @Override
    @SneakyThrows
    protected void doInit() {

    }

    /**
     * 数据集数据处理
     * @apiNote 这一步发生在数据集创建后 针对不同数据的处理
     * @return 根据类型返回结果
     */
    @Override
    public Boolean dataProcessing(){

        return true;
    }

    /**
     * 获取当前数据状态
     * @return 数据集状态 {@link DataSetStatusEnum}
     */
    @Override
    @SneakyThrows
    public DataSetStatusEnum getDatasetStatus(){
        return null;
    }

    /**
     * 获取源数据状态
     * @return 数据集状态 {@link DataSetStatusEnum}
     */
    @Override
    @SneakyThrows
    public DataSetStatusEnum getSourceDataStatus(){
        return null;
    }

}
