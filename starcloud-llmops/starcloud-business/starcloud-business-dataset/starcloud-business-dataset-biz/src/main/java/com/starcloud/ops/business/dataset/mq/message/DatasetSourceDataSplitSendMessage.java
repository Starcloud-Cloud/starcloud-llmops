package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataSplitSendMessage extends AbstractStreamMessage {

    /**
     * 数据集编号
     */
    @NotNull(message = " 数据集编号 不能为空")
    private String datasetId;
    /**
     * 数据源 ID
     */
    @NotNull(message = " 数据源 ID")
    private String dataSourceId;
    /**
     * 分段规则
     */
    @NotNull(message = "分段规则")
    private SplitRule splitRule;

    /**
     * 清洗后的数据
     */
    @NotNull(message = "清洗后的数据")
    private String cleanText;

    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.split.send";
    }

}
