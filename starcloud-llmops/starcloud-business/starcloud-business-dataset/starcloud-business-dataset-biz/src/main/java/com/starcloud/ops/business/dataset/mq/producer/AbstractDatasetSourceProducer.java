package com.starcloud.ops.business.dataset.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.dataset.mq.message.DatasetSourceDataCleanSendMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 数据集 队列消息发送
 *
 * @author Alan Cusack
 */
@Slf4j
public abstract class AbstractDatasetSourceProducer {


    public abstract void asyncSendMessage(Long dataSourceId);

    public abstract void sendMessage(Long dataSourceId);

}
