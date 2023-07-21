package com.starcloud.ops.business.dataset.core.init;

import com.starcloud.ops.business.dataset.core.DatasetConfig;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据集的抽象类，提供模板方法，减少子类的冗余代码
 *
 * @author Alan Cusack
 */
@Slf4j
public abstract class AbstractDataset<Config extends DatasetConfig>  implements DatasetHandle {

    /**
     * 数据集 ID
     */
    private final String datasetId;

    /**
     * 数据集配置
     */
    protected Config config;

    protected AbstractDataset(String datasetId,Config config) {
        this.datasetId = datasetId;
        this.config = config;
    }

    /**
     * 初始化
     */
    public final void init() {
        doInit();
        log.info("[init][数据集配置({}) 处理完成]", config);
    }

    /**
     * 自定义初始化
     */
    protected abstract void doInit();

}
