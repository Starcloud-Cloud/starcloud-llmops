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

        if (message.getRetryCount() <= 3) {
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
        }else {
            log.info("任务重复执行失败，超过重试次数;任务 message 是({})",message);
        }

    }

    protected abstract void setDataState(DatasetSourceSendMessage message);

    protected abstract void processBusinessLogic(DatasetSourceSendMessage message);

    protected void updateDataState(DatasetSourceSendMessage message) {
        datasetSourceDataService.updateStatusById(message.getDataSourceId(), message.getStatus(),message.getErrCode(), message.getErrMsg());
    }

    protected abstract void sendMessage(DatasetSourceSendMessage message);

}
