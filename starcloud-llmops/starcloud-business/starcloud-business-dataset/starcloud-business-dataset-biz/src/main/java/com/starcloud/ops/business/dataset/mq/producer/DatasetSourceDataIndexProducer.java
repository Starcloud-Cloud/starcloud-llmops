package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.consumer.DataSetSourceDataIndexSendConsumer;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataIndexSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据集 队列消息发送
 *
 * @author Alan Cusack
 */

@Slf4j
@Component
public class DatasetSourceDataIndexProducer extends AbstractDatasetSourceProducer {

    @Autowired
    private DataSetSourceDataIndexSendConsumer indexSendConsumer;

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

    /**
     * 发送 {@link DatasetSourceDataCleanSendMessage} 消息
     */
    public void sendIndexDatasetsSendMessage(String dataSetId, Long dataSourceId) {
        DatasetSourceDataIndexSendMessage message = new DatasetSourceDataIndexSendMessage()
                .setDatasetId(dataSetId)
                .setDataSourceId(dataSourceId);
        redisMQTemplate.send(message);
    }


    @Override
    public void asyncSendMessage(DatasetSourceSendMessage sendMessage) {

        this.sendIndexDatasetsSendMessage(sendMessage.getDatasetId(), sendMessage.getDataSourceId());
    }

    @Override
    public void sendMessage(DatasetSourceSendMessage sendMessage) {

        DatasetSourceDataIndexSendMessage message = new DatasetSourceDataIndexSendMessage();

        message.setSync(true);
        message.setDataSourceId(sendMessage.getDataSourceId());
        message.setDataSourceId(sendMessage.getDataSourceId());
        message.setSplitRule(sendMessage.getSplitRule());
        message.setUserId(sendMessage.getUserId());

        indexSendConsumer.onMessage(message);
    }

}
