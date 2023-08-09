package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

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
