package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.consumer.DataSetSourceDataCleanSendConsumer;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 数据集 队列消息发送
 *
 * @author Alan Cusack
 */

@Slf4j
@Component
public class DatasetSourceDataCleanProducer extends AbstractDatasetSourceProducer {

    @Autowired
    private DataSetSourceDataCleanSendConsumer dataSetSourceDataCleanSendConsumer;

    @Resource
    private RedisMQTemplate redisMQTemplate;

    // /**
    //  * 发送 {@link SmsChannelRefreshMessage} 消息
    //  */
    // public void sendSmsChannelRefreshMessage() {
    //     SmsChannelRefreshMessage message = new SmsChannelRefreshMessage();
    //     redisMQTemplate.send(message);
    // }
    //
    // /**
    //  * 发送 {@link SmsTemplateRefreshMessage} 消息
    //  */
    // public void sendSmsTemplateRefreshMessage() {
    //     SmsTemplateRefreshMessage message = new SmsTemplateRefreshMessage();
    //     redisMQTemplate.send(message);
    // }

    @Override
    public void asyncSendMessage(DatasetSourceSendMessage sendMessage) {
        DatasetSourceSendMessage message = new DatasetSourceDataCleanSendMessage()
                .setDatasetId(sendMessage.getDatasetId())
                .setDataSourceId(sendMessage.getDataSourceId())
                .setSplitRule(sendMessage.getSplitRule())
                .setUserId(sendMessage.getUserId())
                .setRetryCount(0)
                .setCleanSync(sendMessage.getCleanSync())
                .setSplitSync(sendMessage.getSplitSync())
                .setIndexSync(sendMessage.getIndexSync());
        log.info("发送数据清洗信息");
        redisMQTemplate.send(message);
    }

    @Override
    public void sendMessage(DatasetSourceSendMessage sendMessage) {

        DatasetSourceDataCleanSendMessage message = new DatasetSourceDataCleanSendMessage();

        message.setRetryCount(0);
        message.setDatasetId(sendMessage.getDatasetId());
        message.setDataSourceId(sendMessage.getDataSourceId());
        message.setSplitRule(sendMessage.getSplitRule());
        message.setUserId(sendMessage.getUserId());
        message.setCleanSync(sendMessage.getCleanSync());
        message.setSplitSync(sendMessage.getSplitSync());
        message.setIndexSync(sendMessage.getIndexSync());

        dataSetSourceDataCleanSendConsumer.onMessage(message);
    }
}
