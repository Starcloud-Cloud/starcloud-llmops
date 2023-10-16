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
     * 失败数
     */
    private Integer errorCount;

    /**
     * 用户满意数
     */
    private Integer feedbackLikeCount;

    /**
     * 完成/聊天平均耗时
     */
    private BigDecimal completionAvgElapsed;

    /**
     * 图片平均耗时
     */
    private BigDecimal imageAvgElapsed;

    /**
     * 完成/聊天总花费积分
     */
    private Integer completionCostPoints;

    /**
     * 图片总花费积分
     */
    private Integer imageCostPoints;

    /**
     * 创建时间
     */
    private String createDate;

}
