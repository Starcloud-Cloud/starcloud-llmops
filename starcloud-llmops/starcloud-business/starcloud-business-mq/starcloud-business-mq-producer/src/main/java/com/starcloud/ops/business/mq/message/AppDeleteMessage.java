package com.starcloud.ops.business.mq.message;

import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppDeleteMessage extends AbstractRedisStreamMessage {

    private String appUid;

    @Override
    public String getStreamKey() {
        return "app.delete.send";
    }
}
