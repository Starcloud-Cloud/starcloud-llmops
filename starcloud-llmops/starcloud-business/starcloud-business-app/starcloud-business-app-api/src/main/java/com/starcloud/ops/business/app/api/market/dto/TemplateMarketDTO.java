package com.starcloud.ops.business.app.api.market.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.template.dto.TemplateConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版市场对象实体")
public class TemplateMarketDTO implements Serializable {

    private static final long serialVersionUID = 1475037816778901152L;

    /**
     * 模板市场ID
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 模版市场 key，我的模版上传到模版市场时候，会生成一个模版市场 key，下载模版的时候，会将该 key 存到此处。
     */
    @Schema(description = "模版市场 Key")
    private String key;

    /**
     * 模版名称
     */
    @Schema(description = "模版名称")
    private String name;

    /**
     * 模版类型, SYSTEM：系统推荐模版，MY_TEMPLATE：我的模版，DOWNLOAD_TEMPLATE：下载模版
     */
    @Schema(description = "模版类型")
    private String type;

    /**
     * 模版标识, 区分自定义模版和每一种具体的系统模版，所有的模版的具体类型都基于此标识，不同的标识，模版的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "模版标识")
    private String logotype;

    /**
     * 模版来源类型，表示模版的是从那个平台创建，或者下载的。比如 WrdPress ， Chrome插件等
     */
    @Schema(description = "模版来源类型")
    private String sourceType;

    /**
     * 模版版本，默认版本 1.0.0
     */
    @Schema(description = "模版版本")
    private String version;

    /**
     * 模版标签，多个以逗号分割
     */
    @Schema(description = "模版标签")
    private List<String> tags;

    /**
     * 模版类别，多个以逗号分割
     */
    @Schema(description = "模版类别")
    private List<String> categories;

    /**
     * 模版场景，多个以逗号分割
     */
    @Schema(description = "模版场景")
    private List<String> scenes;

    /**
     * 模版语言
     */
    @Schema(description = "模版语言")
    private String language;

    /**
     * 模版详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "模版详细配置信息")
    private TemplateConfigDTO config;

    /**
     * 模版图片，多个以逗号分割
     */
    @Schema(description = "模版图片")
    private List<String> images;

    /**
     * 模版图标
     */
    @Schema(description = "模版图标")
    private String icon;

    /**
     * 模版步骤图标、多个以逗号分割
     */
    @Schema(description = "模版步骤图标")
    private List<String> stepIcons;

    /**
     * 步骤数量
     */
    @Schema(description = "步骤数量")
    private Integer stepCount;

    /**
     * 模版描述
     */
    @Schema(description = "模版描述")
    private String description;

    /**
     * 模版 Prompt详情
     */
    @Schema(description = "模版 Prompt详情")
    private String promptInfo;

    /**
     * 模版收费数
     */
    @Schema(description = "模版收费数")
    private BigDecimal cost;

    /**
     * 模版 word
     */
    @Schema(description = "模版 word")
    private Integer word;

    /**
     * 模版是否是免费的
     */
    @Schema(description = "模版是否是免费的")
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
    @Schema(description = "Schema")
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

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String creator;

    /**
     * 更新者，
     */
    @Schema(description = "更新者")
    private String updater;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 多租户编号
     */
    @Schema(description = "多租户编号")
    private Long tenantId;
}
