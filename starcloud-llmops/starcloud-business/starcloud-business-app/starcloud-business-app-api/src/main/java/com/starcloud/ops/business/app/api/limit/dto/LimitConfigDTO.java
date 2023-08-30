package com.starcloud.ops.business.app.api.limit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name = "LimitConfigDTO", description = "限流详细配置DTO")
public class LimitConfigDTO extends BaseLimitConfigDTO {

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

}
