package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataSplitSendMessage extends DatasetSourceSendMessage {

    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.split.send";
    }

}
