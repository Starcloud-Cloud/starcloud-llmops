package com.starcloud.ops.business.log.api.conversation.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 应用执行日志会话分页
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Schema(name = "LogAppConversationPageReqVO", description = "管理后台 - 应用执行日志会话分页 Request VO")
public class LogAppConversationPageReqVO extends PageParam {

    private static final long serialVersionUID = -4439458722495490535L;

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
    private List<String> scenes;

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