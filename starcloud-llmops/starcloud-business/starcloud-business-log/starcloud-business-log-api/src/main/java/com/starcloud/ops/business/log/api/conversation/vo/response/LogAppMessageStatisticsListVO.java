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
     * 用户总数
     */
    @Schema(description = "用户总数")
    private Integer userCount;

    /**
     * 用户满意数
     */
    @Schema(description = "用户满意数")
    private Integer feedbackLikeCount;

    /**
     * 总耗时
     */
    @Schema(description = "总耗时")
    private BigDecimal elapsedTotal;

    /**
     * 平均耗时
     */
    @Schema(description = "平均耗时")
    private BigDecimal elapsedAvg;

    /**
     * 请求tokens总数
     */
    @Schema(description = "请求tokens总数")
    private Integer messageTokens;

    /**
     * 回答tokens总数
     */
    @Schema(description = "回答tokens总数")
    private Integer answerTokens;

    /**
     * tokens总数
     */
    @Schema(description = "tokens总数")
    private Integer tokens;

    /**
     * 生成时间
     */
    @Schema(description = "生成时间")
    private String createDate;

}
