package com.starcloud.ops.business.app.api.limit.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AppLimitRuleDTO", description = "限流详细配置DTO")
public class AppLimitRuleDTO implements Serializable {

    private static final long serialVersionUID = 4978714304963662944L;

    /**
     * 限流配置 code
     */
    @Schema(description = "限流类型")
    private String code;

    /**
     * 限流依据: APP: 应用级别限流，USER： 用户级别限流，TEAM： 团队级别限流
     */
    @Schema(description = "限流依据: APP: 应用级别限流，USER： 用户级别限流，TEAM： 团队级别限流")
    private String limitBy;

    /**
     * 匹配的应用，如果配置，则改配置只针对配置的应用生效，不配置对所有的应用生效
     */
    @Schema(description = "匹配的应用，如果配置，则改配置只针对配置的应用生效，不配置对所有的应用生效")
    private List<String> matchApps;

    /**
     * 忽略的应用集合，如果配置，则配置了的应用，不会进行限流。未配置，则对所有的应用生效 <br>
     * 如果 matchApps 和 ignoreApps 配置了相同的应用 UID，以 ignoreApps 的规则为准，不会进行限流
     */
    @Schema(description = "忽略的应用集合，如果配置，则配置了的应用，不会进行限流。未配置，则对所有的应用生效")
    private List<String> ignoreApps;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 限流阈值
     */
    @Schema(description = "限流阈值")
    private Integer threshold;

    /**
     * 限流时间间隔
     */
    @Schema(description = "限流时间间隔")
    private Long timeInterval;

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

}
