package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.consumer.DataSetSourceDataSplitSendConsumer;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataSplitSendMessage;
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
public class DatasetSourceDataSplitProducer extends AbstractDatasetSourceProducer {

    @Autowired
    private DataSetSourceDataSplitSendConsumer splitSendConsumer;

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
        DatasetSourceSendMessage message = new DatasetSourceDataSplitSendMessage()
                .setDatasetId(sendMessage.getDatasetId())
                .setDataSourceId(sendMessage.getDataSourceId())
                .setSplitRule(sendMessage.getSplitRule())
                .setRetryCount(0)
                .setUserId(sendMessage.getUserId())
                .setTenantId(sendMessage.getTenantId())
                .setCleanSync(sendMessage.getCleanSync())
                .setSplitSync(sendMessage.getSplitSync())
                .setIndexSync(sendMessage.getIndexSync());

        redisMQTemplate.send(message);
    }

    @Override
    public void sendMessage(DatasetSourceSendMessage sendMessage) {

        DatasetSourceDataSplitSendMessage message = new DatasetSourceDataSplitSendMessage();

        message.setRetryCount(0);
        message.setDatasetId(sendMessage.getDatasetId());
        message.setDataSourceId(sendMessage.getDataSourceId());
        message.setSplitRule(sendMessage.getSplitRule());
        message.setUserId(sendMessage.getUserId());
        message.setTenantId(sendMessage.getTenantId());
        message.setCleanSync(sendMessage.getCleanSync());
        message.setSplitSync(sendMessage.getSplitSync());
        message.setIndexSync(sendMessage.getIndexSync());

        splitSendConsumer.onMessage(message);
    }
}
