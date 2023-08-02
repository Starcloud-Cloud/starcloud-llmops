package com.starcloud.ops.business.app.api.channel.dto;

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
@Schema(name = "JsIframeChannelConfigDTO", description = "JS 嵌入分享渠道 DTO")
public class JsIframeChannelConfigDTO extends BaseChannelConfigDTO {

    private static final long serialVersionUID = 6605755834660242225L;

    /**
     * slug
     */
    private String slug;
}
