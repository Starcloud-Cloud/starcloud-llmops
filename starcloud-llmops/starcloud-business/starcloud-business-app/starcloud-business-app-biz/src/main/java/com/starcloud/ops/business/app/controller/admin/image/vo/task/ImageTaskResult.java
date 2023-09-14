package com.starcloud.ops.business.app.controller.admin.image.vo.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ImageTaskResult", description = "商品图生成任务")
public class ImageTaskResult implements Serializable {

    private static final long serialVersionUID = 1993548223066927183L;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private String taskId;

    /**
     * 执行ID
     */
    @Schema(description = "执行ID")
    private String executionId;

    /**
     * 任务第几次执行
     */
    @Schema(description = "任务第几次执行")
    private Integer sequence;

    /**
     * 任务图片
     */
    @Schema(description = "任务图片")
    private String image;

    /**
     * 执行配置
     */
    @Schema(description = "执行配置")
    private ImageTaskConfig config;

    /**
     * 执行结果
     */
    @Schema(description = "执行结果")
    private List<TaskImageVO> result;

    /**
     * 任务创建时间
     */
    @Schema(description = "执行时间")
    private Date executionTime;


}
