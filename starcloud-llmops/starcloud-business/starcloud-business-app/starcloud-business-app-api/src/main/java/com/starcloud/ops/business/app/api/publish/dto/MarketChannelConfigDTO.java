package com.starcloud.ops.business.app.api.publish.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "MarketMediumDTO", description = "应用市场发布媒介 DTO")
public class MarketChannelConfigDTO extends BasePublishChannelConfigDTO {

    private static final long serialVersionUID = 6605755834660242225L;

    /**
     * 应用语言
     */
    private String language;
}
