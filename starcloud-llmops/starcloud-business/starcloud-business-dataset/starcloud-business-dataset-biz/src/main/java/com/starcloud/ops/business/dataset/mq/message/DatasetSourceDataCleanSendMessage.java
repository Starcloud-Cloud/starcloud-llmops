package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataCleanSendMessage extends AbstractStreamMessage {

    private Boolean sync;

    /**
     * 数据集编号
     */
    @NotNull(message = " 数据集编号 不能为空")
    private String datasetId;
    /**
     * 数据源 ID
     */
    @NotNull(message = " 数据源 ID不能为空")
    private Long dataSourceId;
    /**
     * 分段规则
     */
    @NotNull(message = "分段规则不能为空")
    private SplitRule splitRule;

    /**
     * 用户 ID
     */
    @NotNull(message = " 用户 ID不能为空")
    private Long userId;


    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.clean.send";
    }

}
