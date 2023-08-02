package com.starcloud.ops.business.app.api.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "OpenApiChannelConfigDTO", description = "API 调用发布渠道 DTO")
public class OpenApiChannelConfigDTO extends BaseChannelConfigDTO {

    private static final long serialVersionUID = -4319517734165182676L;

    @Schema(description = "API Key")
    private String apiKey;

}
