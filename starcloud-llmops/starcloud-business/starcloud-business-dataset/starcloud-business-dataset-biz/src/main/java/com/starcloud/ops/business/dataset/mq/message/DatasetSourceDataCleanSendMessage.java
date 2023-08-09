package com.starcloud.ops.business.dataset.mq.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataCleanSendMessage extends DatasetSourceSendMessage {

    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.clean.send";
    }

}
