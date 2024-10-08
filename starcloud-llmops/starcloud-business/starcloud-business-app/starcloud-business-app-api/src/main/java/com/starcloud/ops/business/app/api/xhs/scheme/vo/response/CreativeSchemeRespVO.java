package com.starcloud.ops.business.app.api.xhs.scheme.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创作方案DO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "创作方案详情")
public class CreativeSchemeRespVO implements java.io.Serializable {

    private static final long serialVersionUID = 1664393955030446928L;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String uid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 创作方案名称
     */
    @Schema(description = "创作方案名称")
    private String name;

    /**
     * 创作方案类型
     */
    @Schema(description = "创作方案类型")
    private String type;

    /**
     * 创作方案类目
     */
    @Schema(description = "创作方案类目")
    private String category;

    /**
     * 创作方案标签
     */
    @Schema(description = "创作方案标签")
    private List<String> tags;

    /**
     * 创作方案描述
     */
    @Schema(description = "创作方案描述")
    private String description;

    /**
     * 创作方案配置信息
     */
    @Schema(description = "创作方案配置信息")
    private CreativeSchemeConfigurationDTO configuration;

    /**
     * 创作方案图片
     */
    @Schema(description = "创作方案图片")
    private List<String> useImages;

    /**
     * 物料
     */
    @Schema(description = "物料")
    private String materiel;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updater;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
