package com.starcloud.ops.business.dataset.mq.consumer;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessageListener;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 通用处理
 */

@Slf4j
public abstract class AbstractDataProcessor<T extends DatasetSourceSendMessage> extends AbstractStreamMessageListener<T> {


    @Resource
    private DatasetSourceDataService datasetSourceDataService;
    /**
     * 处理消息
     *
     * @param message 消息
     */
    @Override
    public void onMessage(DatasetSourceSendMessage message) {
        log.info("开始清洗数据，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());
        // 1初始任务状态状态
        setDataState(message);

        // 2.设置状态
        updateDataState(message);

        // 3.业务处理
        processBusinessLogic(message);

        // 4.设置状态
        updateDataState(message);

        // 5.发送消息
        sendMessage(message);
        log.info("清洗数据完毕，数据集 ID 为({}),源数据 ID 为({})", message.getDatasetId(), message.getDataSourceId());
    }

    protected abstract void setDataState(DatasetSourceSendMessage message);

    protected abstract void processBusinessLogic(DatasetSourceSendMessage message);

    protected void updateDataState(DatasetSourceSendMessage message){
        datasetSourceDataService.updateStatusById(message.getDataSourceId(), message.getStatus(),message.getErrMsg());
    }

    protected abstract void sendMessage(DatasetSourceSendMessage message);

}
