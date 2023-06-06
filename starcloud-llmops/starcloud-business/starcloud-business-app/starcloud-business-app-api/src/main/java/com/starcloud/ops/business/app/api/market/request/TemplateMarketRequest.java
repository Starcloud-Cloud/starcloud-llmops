package com.starcloud.ops.business.app.api.market.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.template.dto.TemplateConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 模版基础请求实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版基础请求实体")
public class TemplateMarketRequest implements Serializable {

    private static final long serialVersionUID = 6193262241108919904L;

    /**
     * 模版名称
     */
    @Schema(description = "模版名称")
    @NotBlank(message = "模版名称不能为空")
    private String name;

    /**
     * 模版类型
     */
    @Schema(description = "模版类型")
    @NotBlank(message = "模版类型不能为空")
    private String type;

    /**
     * 模版标识, 区分自定义模版和每一种具体的系统模版，所有的模版的具体类型都基于此标识，不同的标识，模版的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "模版标识")
    @NotBlank(message = "模版标识不能为空")
    private String logotype;

    /**
     * 模版来源类型，表示模版的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "模版来源类型")
    @NotBlank(message = "模版来源类型不能为空")
    private String sourceType;

    /**
     * 模版市场模版版本号
     */
    @Schema(description = "模版市场模版版本号")
    private String version;

    /**
     * 模版标签
     */
    @Schema(description = "模版标签")
    private List<String> tags;

    /**
     * 模版类别
     */
    @Schema(description = "模版类别")
    private List<String> categories;

    /**
     * 模版场景
     */
    @Schema(description = "模版场景")
    private List<String> scenes;

    /**
     * 模版语言
     */
    @Schema(description = "模版语言")
    @NotBlank(message = "模版语言不能为空")
    private String language;

    /**
     * 模版详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "模版详细配置信息")
    private TemplateConfigDTO config;

    /**
     * 模版图片
     */
    @Schema(description = "模版图片")
    private List<String> images;

    /**
     * 模版图标
     */
    @Schema(description = "模版图标")
    private String icon;

    /**
     * 模版描述
     */
    @Schema(description = "模版描述")
    private String description;

    /**
     * 模版提示信息
     */
    private String promptInfo;

    /**
     * 模版价格
     */
    @Schema(description = "模版价格")
    private BigDecimal cost;

    /**
     * 模版是否免费
     */
    @Schema(description = "模版是否免费")
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
     * 模版审核
     */
    @Schema(description = "模版审核")
    private Integer audit;

    /**
     * 模版状态，0：启用，1：禁用
     */
    @Schema(description = "模版状态")
    private Integer status;

}
