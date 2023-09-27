package com.starcloud.ops.business.log.api.conversation.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

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
     * app 配置
     */
    @Schema(description = "app 配置")
    private String appConfig;

    /**
     * app 场景
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
     * 创建时间
     */
    @Schema(description = "模版创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}