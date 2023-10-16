package com.starcloud.ops.business.log.api.conversation.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-30
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Schema(name = "LogAppMessageStatisticsListVO", description = "应用消息统计列表响应 VO")
public class LogAppMessageStatisticsListVO implements Serializable {

    private static final long serialVersionUID = -1741170241906440742L;

    /**
     * 消息总数
     */
    @Schema(description = "消息总数")
    private Integer messageCount;

    /**
     * 消息成功总数
     */
    @Schema(description = "消息成功总数")
    private Integer successCount;

    /**
     * 消息失败总数
     */
    @Schema(description = "消息失败总数")
    private Integer errorCount;

    /**
     * 用户满意数
     */
    @Schema(description = "用户满意数")
    private Integer feedbackLikeCount;

    /**
     * 完成/聊天平均耗时
     */
    @Schema(description = "完成/聊天平均耗时")
    private BigDecimal completionAvgElapsed;

    /**
     * 图片平均耗时
     */
    @Schema(description = "图片平均耗时")
    private BigDecimal imageAvgElapsed;

    /**
     * 完成/聊天总花费积分
     */
    @Schema(description = "消耗积分")
    private Integer completionCostPoints;

    /**
     * 图片总花费积分
     */
    @Schema(description = "图片总花费积分")
    private Integer imageCostPoints;

    /**
     * 生成时间
     */
    @Schema(description = "生成时间")
    private String createDate;

}
