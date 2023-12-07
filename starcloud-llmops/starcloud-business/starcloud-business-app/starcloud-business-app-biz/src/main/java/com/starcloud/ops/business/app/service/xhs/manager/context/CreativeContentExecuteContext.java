package com.starcloud.ops.business.app.service.xhs.manager.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 创作内容执行上下文
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeContentExecuteContext implements java.io.Serializable {

    private static final long serialVersionUID = -8610400867753196954L;

    /**
     * 执行计划UID
     */
    private String planUid;

    /**
     * 创作内容ID列表
     */
    private List<Long> contentIdList;

    /**
     * 执行类型
     */
    private String type;

    /**
     * 是否只执行重试任务
     */
    private Boolean retryProcess;

    /**
     * 最大重试次数
     */
    private Integer maxRetry;

}
