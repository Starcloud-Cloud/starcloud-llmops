package com.starcloud.ops.business.app.api.market.dto;

import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
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
@Schema(description = "应用市场对象实体")
public class AppMarketDTO implements Serializable {

    private static final long serialVersionUID = 1475037816778901152L;

    /**
     * 模板市场ID
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 应用市场 key，我的应用上传到应用市场时候，会生成一个应用市场 key，下载应用的时候，会将该 key 存到此处。
     */
    @Schema(description = "应用市场 uid")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    private String model;

    /**
     * 应用类型, SYSTEM：系统推荐应用，MY_TEMPLATE：我的应用，DOWNLOAD_TEMPLATE：下载应用
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 应用标识, 区分自定义应用和每一种具体的系统应用，所有的应用的具体类型都基于此标识，不同的标识，应用的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "应用标识")
    private String logotype;

    /**
     * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress ， Chrome插件等
     */
    @Schema(description = "应用来源类型")
    private String sourceType;

    /**
     * 应用版本，默认版本 1.0.0
     */
    @Schema(description = "应用版本")
    private String version;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签")
    private List<String> tags;

    /**
     * 应用类别，多个以逗号分割
     */
    @Schema(description = "应用类别")
    private List<String> categories;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用场景")
    private List<String> scenes;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
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
     * 应用图片，多个以逗号分割
     */
    @Schema(description = "应用图片")
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用步骤图标、多个以逗号分割
     */
    @Schema(description = "应用步骤图标")
    private List<String> stepIcons;

    /**
     * 步骤数量
     */
    @Schema(description = "步骤数量")
    private Integer stepCount;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用 Prompt详情
     */
    @Schema(description = "应用 Prompt详情")
    private String promptInfo;

    /**
     * 应用收费数
     */
    @Schema(description = "应用收费数")
    private BigDecimal cost;

    /**
     * 应用 word
     */
    @Schema(description = "应用 word")
    private Integer word;

    /**
     * 应用是否是免费的
     */
    @Schema(description = "应用是否是免费的")
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
     * 应用审核
     */
    @Schema(description = "应用审核")
    private Integer audit;

    /**
     * 应用状态，0：启用，1：禁用
     */
    @Schema(description = "应用状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, timezone = "GMT+8")
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
