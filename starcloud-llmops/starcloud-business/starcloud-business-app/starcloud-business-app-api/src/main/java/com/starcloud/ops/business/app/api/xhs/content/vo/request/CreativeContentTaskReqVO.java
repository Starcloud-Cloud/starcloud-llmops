package com.starcloud.ops.business.app.api.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作任务列表查询条件")
public class CreativeContentTaskReqVO implements java.io.Serializable {

    private static final long serialVersionUID = 4311772732499234715L;

    /**
     * 计划UID
     */
    @Schema(description = "计划UID")
    private String planUid;

    /**
     * 批次UID
     */
    @Schema(description = "批次UID")
    private String batchUid;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态")
    private String status;

    /**
     * 重试次数
     */
    @Schema(description = "重试次数")
    private Integer maxRetry;

    /**
     * 批量执行一次的数据量
     */
    @Schema(description = "批量执行一次的数据量")
    private Integer bathCount;

}
