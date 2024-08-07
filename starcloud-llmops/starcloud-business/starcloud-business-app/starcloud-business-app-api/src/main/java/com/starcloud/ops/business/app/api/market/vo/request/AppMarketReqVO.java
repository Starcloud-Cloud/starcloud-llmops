package com.starcloud.ops.business.app.api.market.vo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.config.ChatConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.ImageConfigReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.WorkflowConfigReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 应用基础请求实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用基础请求实体")
public class AppMarketReqVO implements Serializable {

    private static final long serialVersionUID = 6193262241108919904L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用名称不能为空")
    private String name;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 资料类型
     */
    @Schema(description = "资料类型")
    private String materialType;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用模型不能为空")
    private String model;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应用语言不能为空")
    private String language;

    /**
     * 应用排序，越小越靠前
     */
    @Schema(description = "应用排序")
    private Long sort;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private List<String> scenes;

    /**
     * 应用图片
     */
    @Schema(description = "应用图片")
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用是否免费
     */
    @Schema(description = "应用是否免费")
    private Boolean free;

    /**
     * 应用价格
     */
    @Schema(description = "应用价格")
    private BigDecimal cost;

    /**
     * 使用数量
     */
    @Schema(description = "使用数量")
    private Integer usageCount;

    /**
     * 点赞数量
     */
    @Schema(description = "点赞数量")
    private Integer likeCount;

    /**
     * 查看数量
     */
    @Schema(description = "查看数量")
    private Integer viewCount;

    /**
     * 安装数量
     */
    @Schema(description = "安装数量")
    private Integer installCount;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息")
    private WorkflowConfigReqVO workflowConfig;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private ChatConfigReqVO chatConfig;

    /**
     * 应用图片配置
     */
    @Schema(description = "应用图片配置")
    private ImageConfigReqVO imageConfig;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用example
     */
    @Schema(description = "应用example")
    private String example;

    /**
     * 是否校验
     */
    @Schema(description = "是否校验")
    private Boolean validate;
}
