package com.starcloud.ops.business.app.api.limit.dto;

import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Schema(name = "LimitConfigDTO", description = "限流配置基础DTO")
public class LimitConfigDTO implements Serializable {

    private static final long serialVersionUID = 4978714304963662944L;

    /**
     * 限流数量
     */
    @Schema(description = "限流数量")
    private Integer number;

    /**
     * 限流时间
     */
    @Schema(description = "限流时间")
    private Integer time;

    /**
     * 限流时间单位
     */
    @Schema(description = "限流时间单位")
    private ChronoUnit timeUnit;

    /**
     * 超出数量之后的消息
     */
    @Schema(description = "超出数量之后的消息")
    private String message;

    /**
     * 创建限流配置
     *
     * @param number   限流数量
     * @param time     限流时间
     * @param timeUnit 限流时间单位
     * @param message  超出数量之后的消息
     * @return 限流配置
     */
    public static LimitConfigDTO of(Integer number, Integer time, ChronoUnit timeUnit, String message) {
        LimitConfigDTO limitConfig = new LimitConfigDTO();
        limitConfig.setNumber(number);
        limitConfig.setTime(time);
        limitConfig.setTimeUnit(timeUnit);
        limitConfig.setMessage(message);
        return limitConfig;
    }

}
