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
@Schema(name = "BaseLimitConfigDTO", description = "限流配置基础DTO")
public class BaseLimitConfigDTO implements Serializable {

    private static final long serialVersionUID = 5429362672093413179L;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

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

}
