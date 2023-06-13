package com.starcloud.ops.business.app.api.market.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用基础请求实体")
public class AppMarketRequest implements Serializable {

    private static final long serialVersionUID = 6193262241108919904L;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    @NotBlank(message = "应用名称不能为空")
    private String name;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    @NotBlank(message = "应用模型不能为空")
    private String model;

    /**
     * 应用类型
     */
    @Schema(description = "应用类型")
    @NotBlank(message = "应用类型不能为空")
    private String type;

    /**
     * 应用标识, 区分自定义应用和每一种具体的系统应用，所有的应用的具体类型都基于此标识，不同的标识，应用的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "应用标识")
    @NotBlank(message = "应用标识不能为空")
    private String logotype;

    /**
     * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "应用来源类型")
    @NotBlank(message = "应用来源类型不能为空")
    private String sourceType;

    /**
     * 应用市场应用版本号
     */
    @Schema(description = "应用市场应用版本号")
    @NotBlank(message = "应用市场应用版本号不能为空")
    private String version;

    /**
     * 应用标签
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别")
    private List<String> categories;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private List<String> scenes;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
    @NotBlank(message = "应用语言不能为空")
    private String language;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息")
    private AppConfigDTO config;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private AppChatConfigDTO chatConfig;

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
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用提示信息
     */
    private String promptInfo;

    /**
     * 应用价格
     */
    @Schema(description = "应用价格")
    private BigDecimal cost;

    /**
     * 应用是否免费
     */
    @Schema(description = "应用是否免费")
    private Boolean free;

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
     * 下载数量
     */
    @Schema(description = "下载数量")
    private Integer downloadCount;

    /**
     * 插件版本
     */
    @Schema(description = "插件版本")
    private String pluginVersion;

    /**
     * 插件级别
     */
    @Schema(description = "插件级别")
    private String pluginLevel;

    /**
     * 应用审核
     */
    @Schema(description = "应用审核")
    private Integer audit;

    /**
     * 应用状态，0：启用，1：禁用
     */
    @Schema(description = "应用状态")
    private Integer status;

}
