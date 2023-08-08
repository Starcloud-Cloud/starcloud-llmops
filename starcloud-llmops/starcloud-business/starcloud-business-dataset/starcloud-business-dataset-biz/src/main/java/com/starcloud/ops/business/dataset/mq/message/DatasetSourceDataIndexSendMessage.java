package com.starcloud.ops.business.dataset.mq.message;

import cn.iocoder.yudao.framework.mq.core.stream.AbstractStreamMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 发送消息 创建索引
 *
 * @author  Alan Cusack
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasetSourceDataIndexSendMessage extends DatasetSourceSendMessage {

    /**
     * 数据集编号
     */
    @NotNull(message = " 数据集编号 不能为空")
    private String datasetId;
    /**
     * 数据源 ID
     */
    @NotNull(message = " 数据源 ID")
    private Long dataSourceId;

    /**
     * 用户 ID
     */
    @NotNull(message = " 用户 ID不能为空")
    private Long userId;


    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.index.send";
    }

}
