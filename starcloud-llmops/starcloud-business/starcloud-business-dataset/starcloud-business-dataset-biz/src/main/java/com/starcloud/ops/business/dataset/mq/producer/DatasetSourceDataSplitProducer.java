package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataSplitSendMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 数据集 队列消息发送
 * @author Alan Cusack
 */

@Slf4j
@Component
public class DatasetSourceDataSplitProducer {

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
     *
     */
    public void sendSplitDatasetsSendMessage(String dataSetId, Long dataSourceId,
                                             SplitRule splitRule, Long userId) {
        DatasetSourceDataSplitSendMessage message = new DatasetSourceDataSplitSendMessage()
                .setDatasetId(dataSetId)
                .setDataSourceId(dataSourceId)
                .setSplitRule(splitRule)
                .setUserId(userId);
        redisMQTemplate.send(message);
    }


}
