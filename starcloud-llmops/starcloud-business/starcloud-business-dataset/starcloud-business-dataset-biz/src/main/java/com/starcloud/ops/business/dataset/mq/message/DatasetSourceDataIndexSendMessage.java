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
public class DatasetSourceDataIndexSendMessage extends AbstractStreamMessage {

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
     * 分割后的数据集合
     */
    @NotNull(message = "分割后的数据结集合")
    private List<String> splitText;

    @Override
    public String getStreamKey() {
        return "system.dataset.sourcedata.index.send";
    }

}
