package com.starcloud.ops.business.app.api.limit.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Schema(name = "AppLimitRuleReqVO", description = "限流配置基础请求")
public class AppLimitRuleReqVO implements Serializable {

    private static final long serialVersionUID = 5429362672093413179L;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    @NotNull(message = "enable不能为空")
    private Boolean enable;

    /**
     * 限流数量
     */
    @Schema(description = "限流阈值")
    @NotNull(message = "限流阈值不能为空")
    @Min(value = 1, message = "限流阈值不能小于1")
    private Integer threshold;

    /**
     * 限流时间
     */
    @Schema(description = "限流时间间隔")
    @NotNull(message = "时间间隔不能为空")
    @Min(value = 1, message = "时间间隔不能小于1")
    private Long timeInterval;

    /**
     * 限流时间单位
     */
    @Schema(description = "限流时间单位")
    @NotNull(message = "时间单位不能为空")
    private String timeUnit;

    /**
     * 超出数量之后的消息
     */
    @Schema(description = "超出数量之后的消息")
    @NotBlank(message = "提示消息不能为空")
    private String message;

}
