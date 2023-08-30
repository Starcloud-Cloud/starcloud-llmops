package com.starcloud.ops.business.app.api.limit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

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
     * 限流配置 code
     */
    @Schema(description = "限流类型")
    private String code;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 限流依据: APP: 应用级别限流，USER： 用户级别限流，TEAM： 团队级别限流
     */
    @Schema(description = "限流依据: APP: 应用级别限流，USER： 用户级别限流，TEAM： 团队级别限流")
    private String limitBy;

    /**
     * 限流数量
     */
    @Schema(description = "限流数量")
    private Integer limit;

    /**
     * 限流时间
     */
    @Schema(description = "限流超时时间")
    private Long timeout;

    /**
     * 限流时间单位
     */
    @Schema(description = "限流时间单位")
    private String timeUnit;

    /**
     * 超出数量之后的消息
     */
    @Schema(description = "超出数量之后的消息")
    private String message;

    /**
     * 创建限流配置
     *
     * @param limit    限流数量
     * @param timeout  限流时间
     * @param timeUnit 限流时间单位
     * @param message  超出数量之后的消息
     * @return 限流配置
     */
    public static LimitConfigDTO of(Boolean enable, Integer limit, Long timeout, String timeUnit, String message) {
        LimitConfigDTO limitConfig = new LimitConfigDTO();
        limitConfig.setEnable(enable);
        limitConfig.setLimit(limit);
        limitConfig.setTimeout(timeout);
        limitConfig.setTimeUnit(timeUnit);
        limitConfig.setMessage(message);
        return limitConfig;
    }

}
