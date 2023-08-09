package com.starcloud.ops.business.dataset.mq.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 发送消息 创建索引
 *
 * @author Alan Cusack
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataIndexSendMessage extends DatasetSourceSendMessage {
    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.index.send";
    }

}
