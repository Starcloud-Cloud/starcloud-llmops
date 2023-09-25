package com.starcloud.ops.business.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppDeleteMessage extends AbstractStreamMessage {

    private String appUid;

    @Override
    public String getStreamKey() {
        return "app.delete.send";
    }
}
