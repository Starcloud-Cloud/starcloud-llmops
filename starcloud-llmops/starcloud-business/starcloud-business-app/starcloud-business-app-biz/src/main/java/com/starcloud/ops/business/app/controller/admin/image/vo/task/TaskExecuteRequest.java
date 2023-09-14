package com.starcloud.ops.business.app.controller.admin.image.vo.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "TaskExecuteRequest", description = "商品图生成任务")
public class TaskExecuteRequest implements Serializable {

    private static final long serialVersionUID = 8942440715321449767L;

    @Schema(description = "是否是检查任务状态，true：检查任务状态，false：执行任务")
    private Boolean isCheck;

    @Schema(description = "任务ID")
    private String taskId;
}
