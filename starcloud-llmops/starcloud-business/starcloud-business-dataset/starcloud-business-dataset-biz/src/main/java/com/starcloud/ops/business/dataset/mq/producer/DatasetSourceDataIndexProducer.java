package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.consumer.DataSetSourceDataIndexSendConsumer;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataIndexSendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 数据集 队列消息发送
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
    public void sendIndexDatasetsSendMessage(String dataSetId, Long dataSourceId, List<String> splitText) {
        DatasetSourceDataIndexSendMessage message = new DatasetSourceDataIndexSendMessage()
                .setDatasetId(dataSetId)
                .setDataSourceId(dataSourceId)
                .setSplitText(splitText);
        redisMQTemplate.send(message);
    }


    @Override
    public void asyncSendMessage(Long dataSourceId) {

    }

    @Override
    public void sendMessage(Long dataSourceId) {

        DatasetSourceDataIndexSendMessage indexSendMessage = new DatasetSourceDataIndexSendMessage();
        indexSendMessage.setDataSourceId(dataSourceId);

        indexSendConsumer.onMessage(indexSendMessage);

    }
}
