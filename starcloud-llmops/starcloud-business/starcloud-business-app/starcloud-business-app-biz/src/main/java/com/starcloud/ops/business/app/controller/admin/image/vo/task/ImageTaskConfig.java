package com.starcloud.ops.business.app.controller.admin.image.vo.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

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
@Schema(name = "ImageTaskConfig", description = "商品图生成任务")
public class ImageTaskConfig implements Serializable {

    private static final long serialVersionUID = -3193170612727572869L;

    /**
     * 任务ID
     */
    @Schema(description = "配置ID")
    private String id;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private String taskId;

    /**
     * 任务图片
     */
    @Schema(description = "任务图片")
    private String image;

    /**
     * 遮罩图片
     */
    @Schema(description = "遮罩图片")
    private String mask;

    /**
     * 生成图片数量
     */
    @Schema(description = "生成图片数量")
    private Integer samples;

    /**
     * 生成图片的类型
     */
    @Schema(description = "生成图片的类型")
    private Integer type;

    /**
     * 使用的快捷模板类型
     */
    @Schema(description = "使用的快捷模板类型")
    private Integer template;

    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String prompt;

    /**
     * 反向提示词
     */
    @Schema(description = "反向提示词")
    private String negativePrompt;

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


}
