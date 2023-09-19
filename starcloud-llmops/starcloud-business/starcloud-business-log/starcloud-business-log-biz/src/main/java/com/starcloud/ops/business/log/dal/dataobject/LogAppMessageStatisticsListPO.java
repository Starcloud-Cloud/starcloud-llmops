package com.starcloud.ops.business.log.dal.dataobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author admin
 * @version 1.0.0
 * @since 2023-07-30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LogAppMessageStatisticsListPO {

    /**
     * 应用 ID
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
     * 应用场景
     */
    private String fromScene;

    /**
     * AI 模型
     */
    private String aiModel;

    /**
     * 消息数
     */
    private Integer messageCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer errorCount;

    /**
     * 用户数
     */
    private Integer userCount;

    /**
     * 用户满意数
     */
    private Integer feedbackLikeCount;

    /**
     * 总耗时
     */
    private BigDecimal elapsedTotal;

    /**
     * 平均耗时
     */
    private BigDecimal elapsedAvg;

    /**
     * 请求消耗 token
     */
    private Integer messageTokens;

    /**
     * 回答消耗 token
     */
    private Integer answerTokens;

    /**
     * 总消耗 token
     */
    private Integer tokens;

    /**
     * 创建时间
     */
    private String createDate;

}
