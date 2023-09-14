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
@Schema(name = "ImageTask", description = "商品图生成任务")
public class ImageTask implements Serializable {

    private static final long serialVersionUID = 8653221584190223941L;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private String id;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String name;

    /**
     * 任务类型
     */
    @Schema(description = "任务类型")
    private Integer type;

    /**
     * 任务图片
     */
    @Schema(description = "任务图片")
    private String image;

    /**
     * 任务第几次执行
     */
    @Schema(description = "任务第几次执行")
    private Integer sequence;

    /**
     * 任务创建人
     */
    @Schema(description = "任务创建人")
    private String creator;

    /**
     * 任务创建时间
     */
    @Schema(description = "任务创建时间")
    private Date createTime;

    /**
     * 任务最后一次执行人
     */
    @Schema(description = "最后一次执行人")
    private String lastExecuteUser;

    /**
     * 任务最后一次执行时间
     */
    @Schema(description = "最后一次执行时间")
    private Date lastExecuteTime;

    /**
     * 最后一次执行的状态
     */
    @Schema(description = "最后一次执行的状态")
    private String status;

    /**
     * 任务配置
     */
    @Schema(description = "任务配置")
    private ImageTaskConfig config;

    /**
     * 任务执行结果
     */
    @Schema(description = "任务执行结果")
    private List<ImageTaskResult> result;

}
