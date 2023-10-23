package com.starcloud.ops.business.log.api.conversation.vo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 */
@Schema(description = "管理后台 - 应用执行日志会话集合，参数和 LogAppConversationPageReqVO 是一致的")
@Data
public class LogAppConversationListReqVO implements Serializable {

    private static final long serialVersionUID = 1493039303691726663L;

    /**
     * 会话 uid
     */
    @Schema(description = "会话uid")
    private String uid;

    /**
     * app uid
     */
    @Schema(description = "app uid")
    private String appUid;

    /**
     * app name
     */
    @Schema(description = "app name")
    private String appName;

    /**
     * app 模式
     */
    @Schema(description = "app 模式")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 使用的 ai 模型
     */
    @Schema(description = "使用的 ai 模型")
    private String aiModel;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private List<String> fromSceneList;

    /**
     * 执行状态
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    private String status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;
}