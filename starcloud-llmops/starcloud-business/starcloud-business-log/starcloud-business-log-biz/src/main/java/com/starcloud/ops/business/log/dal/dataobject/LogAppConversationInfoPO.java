package com.starcloud.ops.business.log.dal.dataobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author admin
 * @version 1.0.0
 * @since 2023-07-24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogAppConversationInfoPO {

    /**
     * 会话 uID
     */
    private String uid;

    /**
     * 会话 uid
     */
    private String appUid;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用模型
     */
    private String appMode;

    /**
     * 场景
     */
    private String fromScene;

    /**
     * 使用的 ai 模型
     */
    private String aiModel;

    /**
     * 总消耗token数
     */
    private Integer totalMessageTokens = 0;

    /**
     * 总消耗token数
     */
    private Integer totalAnswerTokens = 0;

    /**
     * 总消耗token数
     */
    private Integer tokens = 0;

    /**
     * 消息数-当前回话下的消息条数
     */
    private Integer messageCount = 0;

    /**
     * 回复数
     */
    private Integer feedbacksCount = 0;

    /**
     * 总耗时
     */
    private BigDecimal totalElapsed = BigDecimal.ZERO;

    /**
     * 总价格
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * 消耗积分
     */
    private Integer costPoints;

    /**
     * 会话状态
     */
    private String status;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 游客编号
     */
    private String endUser;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
