package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.config.ChatConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.config.ImageConfigDTO;
import com.starcloud.ops.business.app.api.app.dto.config.WorkflowConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用对象实体")
public class AppDTO implements Serializable {

    private static final long serialVersionUID = 1578944445567574534L;

    /**
     * 我的应用 ID
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    @Schema(description = "应用 UID, 每个应用的唯一标识")
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
     * 应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用
     */
    @Schema(description = "应用类型：MYSELF：我的应用，DOWNLOAD：已下载应用")
    private String type;

    /**
     * 应用来源类型：表示应用的是从那个平台创建，或者下载的。
     */
    @Schema(description = "应用来源类型：表示应用的是从那个平台创建，或者下载的。")
    private String source;

    /**
     * 应用排序，越小越靠前
     */
    @Schema(description = "应用排序，越小越靠前")
    private Long sort;

    /**
     * 应用类别
     */
    @Schema(description = "应用类别")
    private String category;

    /**
     * 应用标签，多个以逗号分割
     */
    @Schema(description = "应用标签，多个以逗号分割")
    private List<String> tags;

    /**
     * 应用场景，多个以逗号分割
     */
    @Schema(description = "应用类别，多个以逗号分割")
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
     * 应用详细配置信息, 步骤，变量，场景等
     */
    @Schema(description = "应用详细配置信息, 步骤，变量，场景等")
    private WorkflowConfigDTO workflowConfig;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private ChatConfigDTO chatConfig;

    /**
     * 应用图片配置
     */
    @Schema(description = "应用图片配置")
    private ImageConfigDTO imageConfig;

    /**
     * 应用步骤图标、多个以逗号分割
     */
    @Schema(description = "应用步骤图标、多个以逗号分割")
    private List<String> stepIcons;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用发布成功后，应用市场 uid-version
     */
    @Schema(description = "应用上传成功后，应用市场 UID")
    private String publishUid;

    /**
     * 应用安装成功后，应用市场 uid-version
     */
    @Schema(description = "应用下载成功后，应用市场 UID")
    private String installUid;

    /**
     * 应用状态，0：启用，1：禁用
     */
    @Schema(description = "应用状态，0：启用，1：禁用")
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
    private LocalDateTime lastPublish;

    /**
     * 租户编号
     */
    @Schema(description = "租户编号")
    private Long tenantId;

}
