package com.starcloud.ops.business.app.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 模版数据传输对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版对象实体")
public class TemplateDTO implements Serializable {

    private static final long serialVersionUID = 1578944445567574534L;

    /**
     * 模板ID
     */
    @Schema(description = "模板ID")
    private Long id;

    /**
     * 模版名称
     */
    @Schema(description = "模版名称")
    private String name;

    /**
     * 模版类型
     */
    @Schema(description = "模版类型")
    private String type;

    /**
     * 模版标识, 区分自定义模版和每一种具体的系统模版，所有的模版的具体类型都基于此标识，不同的标识，模版的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "模版标识")
    private String logotype;

    /**
     * 模版来源类型，表示模版的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "模版来源类型")
    private String sourceType;

    /**
     * 模版市场 key，我的模版上传到模版市场时候，会生成一个模版市场 key，下载模版的时候，会将该 key 存到此处。
     */
    @Schema(description = "模版市场 key")
    private String marketKey;

    /**
     * 模版版本，默认版本 1.0.0
     */
    @Schema(description = "模版版本")
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
     * 模版步骤的图标
     */
    @Schema(description = "模版步骤的图标")
    private List<String> stepIcons;

    /**
     * 模版描述
     */
    @Schema(description = "模版描述")
    private String description;

    /**
     * 模版状态，0：启用，1：禁用
     */
    @Schema(description = "模版状态")
    private Integer status;

    /**
     * 是否删除： 1 表示删除，0 表示未删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 模版创建者
     */
    @Schema(description = "模版创建者")
    private String creator;

    /**
     * 模版修改者
     */
    @Schema(description = "模版修改者")
    private String updater;

    /**
     * 模版创建时间
     */
    @Schema(description = "模版创建时间")
    private LocalDateTime createTime;

    /**
     * 模版更新时间
     */
    @Schema(description = "模版更新时间")
    private LocalDateTime updateTime;

    /**
     * 最后一次上传到模版市场时间
     */
    @Schema(description = "最后一次上传到模版市场时间")
    private LocalDateTime lastUpload;

    /**
     * 租户编号
     */
    @Schema(description = "租户编号")
    private Long tenantId;

}
