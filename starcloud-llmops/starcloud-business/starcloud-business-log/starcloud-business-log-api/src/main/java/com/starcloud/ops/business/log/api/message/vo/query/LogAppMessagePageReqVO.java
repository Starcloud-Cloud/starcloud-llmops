package com.starcloud.ops.business.log.api.message.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author nacoyer
 */
@Schema(description = "管理后台 - 应用执行日志结果分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppMessagePageReqVO extends PageParam {

    private static final long serialVersionUID = 8007580402983890156L;

    /**
     * 消息 uid
     */
    @Schema(description = "消息uid")
    private String uid;

    /**
     * 会话 uid
     */
    @Schema(description = "会话ID")
    private String appConversationUid;

    /**
     * app uid
     */
    @Schema(description = "app uid")
    private String appUid;

    /**
     * app name
     */
    @Schema(description = "app 模式")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 开始时间
     */
    @Schema(description = "AI 模型")
    private String aiModel;

    /**
     * app 场景
     */
    @Schema(description = "执行状态，ERROR：失败，SUCCESS：成功")
    private String status;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

}