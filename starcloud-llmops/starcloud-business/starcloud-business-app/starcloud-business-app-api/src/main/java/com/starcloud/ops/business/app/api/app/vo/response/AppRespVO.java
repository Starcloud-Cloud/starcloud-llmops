package com.starcloud.ops.business.app.api.app.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.config.ChatConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.ImageConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用返回 VO 对象")
public class AppRespVO implements Serializable {

    private static final long serialVersionUID = 4803486484547919894L;

    /**
     * 应用 UID, 每个应用的唯一标识
     */
    @Schema(description = "应用 UID, 每个应用的唯一标识")
    private String uid;

    /**
     * 推荐应用的唯一标识，只有推荐应用存在该标识
     */
    @Schema(description = "推荐应用的唯一标识，只有推荐应用存在该标识")
    private String recommend;

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
    private WorkflowConfigRespVO workflowConfig;

    /**
     * 应用聊天配置
     */
    @Schema(description = "应用聊天配置")
    private ChatConfigRespVO chatConfig;

    /**
     * 应用生成图片配置
     */
    @Schema(description = "生成图片配置")
    private ImageConfigRespVO imageConfig;

    /**
     * 应用步骤图标、多个以逗号分割
     */
    @Schema(description = "应用步骤图标、多个以逗号分割")
    private List<String> actionIcons;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 应用发布成功后，应用市场 uid-version
     */
    @Schema(description = "应用发布成功后，应用市场 uid-version")
    private String publishUid;

    /**
     * 应用安装成功后，应用市场 uid-version
     */
    @Schema(description = "应用安装成功后，应用市场 uid-version")
    private String installUid;

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
     * 获取应用的简介信息
     *
     * @return 应用简介信息
     */
    public AppRespVO intro() {
        this.setPublishUid(null);
        this.setInstallUid(null);
        this.setUpdater(null);
        this.setCreator(null);
        this.setCreateTime(null);
        this.setUpdateTime(null);
        this.setUid(null);
        this.setWorkflowConfig(null);
        this.setChatConfig(null);
        return this;
    }

}
