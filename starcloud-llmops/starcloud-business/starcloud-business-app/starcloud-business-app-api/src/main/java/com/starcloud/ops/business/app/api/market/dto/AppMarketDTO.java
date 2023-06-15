package com.starcloud.ops.business.app.api.market.dto;

import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.AppChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.AppConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用市场对象实体")
public class AppMarketDTO implements Serializable {

    private static final long serialVersionUID = 1475037816778901152L;

    /**
     * 模板市场 ID
     */
    @Schema(description = "模板市场 ID")
    private Long id;

    /**
     * 市场应用 UID
     */
    @Schema(description = "市场应用 UID")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 应用模型：CHAT：聊天式应用，COMPLETION：生成式应用
     */
    @Schema(description = "应用模型：CHAT：聊天式应用，COMPLETION：生成式应用")
    private String model;

    /**
     * 应用版本，默认版本 1
     */
    @Schema(description = "应用版本，默认版本 1")
    private Integer version;

    /**
     * 应用语言
     */
    @Schema(description = "应用语言")
    private String language;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签，多个以逗号分割")
    private List<String> tags;

    /**
     * 应用类别，多个以逗号分割
     */
    @Schema(description = "应用类别，多个以逗号分割")
    private List<String> categories;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用场景，多个以逗号分割")
    private List<String> scenes;

    /**
     * 应用图片，多个以逗号分割
     */
    @Schema(description = "应用图片，多个以逗号分割")
    private List<String> images;

    /**
     * 应用图标
     */
    @Schema(description = "应用图标")
    private String icon;

    /**
     * 应用是否是免费的
     */
    @Schema(description = "应用是否是免费的")
    private Integer free;

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
     * 应用点赞数量
     */
    @Schema(description = "应用点赞数量")
    private Integer likeCount;

    /**
     * 应用查看数量
     */
    @Schema(description = "应用查看数量")
    private Integer viewCount;

    /**
     * 应用下载数量
     */
    @Schema(description = "应用下载数量")
    private Integer downloadCount;

    /**
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息, 步骤，变量，场景等")
    private AppConfigDTO config;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private AppChatConfigDTO chatConfig;

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
     * 应用审核
     */
    @Schema(description = "应用审核")
    private Integer audit;

    /**
     * 应用状态，0：启用，1：禁用
     */
    @Schema(description = "应用状态，0：启用，1：禁用")
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
