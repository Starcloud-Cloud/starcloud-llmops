package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DatasetSourceSendMessage extends AbstractStreamMessage {

    private Boolean sync;

    private SplitRule splitRule;

    /**
     * 数据集编号
     */
    @NotNull(message = " 数据集编号 不能为空")
    private Long datasetId;
    /**
     * 数据源 ID
     */
    @NotNull(message = " 数据源 ID不能为空")
    private Long dataSourceId;
    /**
     * 用户 ID
     */
    @NotNull(message = " 用户 ID不能为空")
    private Long userId;

    @NotNull(message = "数据状态")
    private Integer status;

    @NotNull(message = "错误信息")
    private String errMsg;

    @NotNull(message = "重试次数")
    private int retryCount;
}
