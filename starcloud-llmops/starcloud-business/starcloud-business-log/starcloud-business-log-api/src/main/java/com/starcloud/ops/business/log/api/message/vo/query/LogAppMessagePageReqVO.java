package com.starcloud.ops.business.log.api.message.vo.query;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

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
     * app 配置
     */
    @Schema(description = "app 配置")
    private String appConfig;

    /**
     * app 场景
     */
    @Schema(description = "执行的 app step")
    private String appStep;

    /**
     * app 场景
     */
    @Schema(description = "执行状态，error：失败，success：成功")
    private String status;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private String errorCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * app 配置
     */
    @Schema(description = "app 配置")
    private String variables;

    /**
     * 请求内容
     */
    @Schema(description = "请求内容")
    private String message;

    /**
     * 消耗token数
     */
    @Schema(description = "消耗token数")
    private Integer messageTokens;

    /**
     * 消耗token单位价格
     */
    @Schema(description = "消耗token单位价格")
    private Long messageUnitPrice;

    /**
     * 返回内容
     */
    @Schema(description = "返回内容")
    private String answer;

    /**
     * 消耗token数
     */
    @Schema(description = "消耗token数")
    private Integer answerTokens;

    /**
     * 消耗token单位价格
     */
    @Schema(description = "消耗token单位价格")
    private Long answerUnitPrice;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 总消耗价格
     */
    @Schema(description = "总消耗价格")
    private Long totalPrice;

    /**
     * 价格单位
     */
    @Schema(description = "价格单位")
    private String currency;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    private String fromScene;

    /**
     * 用户ID
     */
    @Schema(description = "临时用户ID")
    private String endUser;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}