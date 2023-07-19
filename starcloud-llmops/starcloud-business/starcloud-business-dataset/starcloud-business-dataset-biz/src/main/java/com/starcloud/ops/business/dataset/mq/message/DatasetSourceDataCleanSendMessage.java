package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataCleanSendMessage extends AbstractStreamMessage {

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
     * 文件地址
     */
    @NotNull(message = "文件地址")
    private String storageKey;


    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.clean.send";
    }

}
