package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用数据传输对象
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用对象实体")
public class AppDTO implements Serializable {

    private static final long serialVersionUID = 1578944445567574534L;

    /**
     * 模板ID
     */
    @Schema(description = "模板ID")
    private Long id;

    /**
     * 应用唯一标识
     */
    @Schema(description = "应用唯一标识")
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
     * 应用类型
     */
    @Schema(description = "应用类型")
    private String type;

    /**
     * 应用标识, 区分自定义应用和每一种具体的系统应用，所有的应用的具体类型都基于此标识，不同的标识，应用的具体配置（步骤，变量，场景等）会有所不同。
     */
    @Schema(description = "应用标识")
    private String logotype;

    /**
     * 应用来源类型，表示应用的是从那个平台创建，或者下载的。比如 WrdPress，Chrome插件等
     */
    @Schema(description = "应用来源类型")
    private String sourceType;

    /**
     * 上传成功后，应用市场 UID
     */
    @Schema(description = "上传成功后，应用市场 UID")
    private String uploadUid;

    /**
     * 下载成功后，应用市场 UID
     */
    @Schema(description = "下载成功后，应用市场 UID")
    private String downloadUid;

    /**
     * 应用版本，默认版本 1.0.0
     */
    @Schema(description = "应用版本")
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
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息")
    private AppConfigDTO config;

    /**
     * 应用聊天配置信息
     */
    @Schema(description = "应用聊天配置信息")
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
     * 应用步骤的图标
     */
    @Schema(description = "应用步骤的图标")
    private List<String> stepIcons;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用状态，0：启用，1：禁用
     */
    @Schema(description = "应用状态")
    private Integer status;

    /**
     * 是否删除： 1 表示删除，0 表示未删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 应用创建者
     */
    @Schema(description = "应用创建者")
    private String creator;

    /**
     * 应用修改者
     */
    @Schema(description = "应用修改者")
    private String updater;

    /**
     * 应用创建时间
     */
    @Schema(description = "应用创建时间")
    private LocalDateTime createTime;

    /**
     * 应用更新时间
     */
    @Schema(description = "应用更新时间")
    private LocalDateTime updateTime;

    /**
     * 最后一次上传到应用市场时间
     */
    @Schema(description = "最后一次上传到应用市场时间")
    private LocalDateTime lastUpload;

    /**
     * 租户编号
     */
    @Schema(description = "租户编号")
    private Long tenantId;

}
