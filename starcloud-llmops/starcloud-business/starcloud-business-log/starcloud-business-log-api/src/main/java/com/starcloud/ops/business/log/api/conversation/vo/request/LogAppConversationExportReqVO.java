package com.starcloud.ops.business.log.api.conversation.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @author nacoyer
 */
@Schema(description = "管理后台 - 应用执行日志会话 Excel 导出 Request VO，参数和 LogAppConversationPageReqVO 是一致的")
@Data
public class LogAppConversationExportReqVO implements Serializable {

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
     * app 配置
     */
    @Schema(description = "app 配置")
    private String appConfig;

    /**
     * 执行状态
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    private String status;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 终端用户ID
     */
    @Schema(description = "终端用户ID")
    private String endUser;

    /**
     * 模版创建时间
     */
    @Schema(description = "模版创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}