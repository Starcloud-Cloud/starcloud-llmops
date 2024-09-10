package com.starcloud.ops.business.mq.message;

import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LibraryDeleteMessage extends AbstractRedisStreamMessage {

    private String libraryUid;

    @Override
    public String getStreamKey() {
        return "library.delete.send";
    }
}
