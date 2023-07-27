package com.starcloud.ops.business.app.api.publish.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-27
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "BaseMediumDTO", description = "应用发布媒介基础 DTO")
public class BasePublishChannelConfigDTO implements Serializable {

    private static final long serialVersionUID = -8877854685518185874L;
}
