package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.consumer.DataSetSourceDataCleanSendConsumer;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
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

    /**
     * 发送 {@link DatasetSourceDataCleanSendMessage} 消息
     */
    public void sendCleanDatasetsSendMessage(String dataSetId, Long dataSourceId,
                                             SplitRule splitRule, Long userId) {
        DatasetSourceDataCleanSendMessage message = new DatasetSourceDataCleanSendMessage()
                .setDatasetId(dataSetId)
                .setDataSourceId(dataSourceId)
                .setSplitRule(splitRule)
                .setUserId(userId);
        log.info("发送数据清洗信息");
        redisMQTemplate.send(message);
    }


    @Override
    public void asyncSendMessage(Long dataSourceId) {

        this.sendCleanDatasetsSendMessage("",1l,null, 1l);
    }

    @Override
    public void sendMessage(Long dataSourceId) {

        DatasetSourceDataCleanSendMessage datasetSourceDataCleanSendMessage = new DatasetSourceDataCleanSendMessage();

        //补齐参数
        datasetSourceDataCleanSendMessage.setDataSourceId(dataSourceId);
        datasetSourceDataCleanSendMessage.setSync(true);

        dataSetSourceDataCleanSendConsumer.onMessage(datasetSourceDataCleanSendMessage);

    }
}
