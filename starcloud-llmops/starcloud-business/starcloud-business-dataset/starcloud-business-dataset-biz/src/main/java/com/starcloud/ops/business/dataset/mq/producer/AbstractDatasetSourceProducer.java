package com.starcloud.ops.business.dataset.mq.producer;

import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据集 队列消息发送
 *
 * @author Alan Cusack
 */
@Slf4j
public abstract class AbstractDatasetSourceProducer {


    /**
     * 异步发送消息
     * @param sendMessage
     */
    public abstract void asyncSendMessage(DatasetSourceSendMessage sendMessage);

    /**
     * 同步发送消息
     * @param sendMessage
     */

    public abstract void sendMessage(DatasetSourceSendMessage sendMessage);

}
