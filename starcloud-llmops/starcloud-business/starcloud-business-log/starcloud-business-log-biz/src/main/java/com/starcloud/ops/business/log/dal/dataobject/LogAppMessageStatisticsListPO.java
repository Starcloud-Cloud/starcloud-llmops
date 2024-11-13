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
     * 消息数
     */
    private Integer messageCount;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 生成/聊天成功数
     */
    private Integer completionSuccessCount;

    /**
     * 图片成功数
     */
    private Integer imageSuccessCount;

    /**
     * 失败数
     */
    private Integer errorCount;

    /**
     * 生成/聊天失败数
     */
    private Integer completionErrorCount;

    /**
     * 图片失败数
     */
    private Integer imageErrorCount;

    /**
     * 用户满意数
     */
    private Integer feedbackLikeCount;

    /**
     * 生成/聊天平均耗时
     */
    private BigDecimal completionAvgElapsed;

    /**
     * 图片平均耗时
     */
    private BigDecimal imageAvgElapsed;

    /**
     * 生成/聊天总花费积分
     */
    private Integer completionCostPoints;

    /**
     * 图片总花费积分
     */
    private Integer imageCostPoints;

    private Integer matrixCostPoints;

    /**
     * 应用执行消耗总tokens
     */
    private Integer completionTokens;

    /**
     * 聊天执行消耗总tokens
     */
    private Integer chatTokens;

    /**
     * 生成/聊天执行消耗总tokens
     */
    private Integer tokens;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 创建时间
     */
    private String updateDate;

}
