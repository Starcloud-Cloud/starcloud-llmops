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
@Schema(name = "ShareMediumDTO", description = "应用分享发布渠道 DTO")
public class ShareChannelConfigDTO extends BaseChannelConfigDTO {

    private static final long serialVersionUID = -9067857365145672779L;

    /**
     * 分享链接
     */
    @Schema(description = "分享链接")
    private String slug;
}
