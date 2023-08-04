package com.starcloud.ops.business.app.api.channel.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
@Schema(name = "BaseChannelConfigDTO", description = "应用发布基础渠道 DTO")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ShareChannelConfigDTO.class, name = "2"),
        @JsonSubTypes.Type(value = JsIframeChannelConfigDTO.class, name = "3"),
        @JsonSubTypes.Type(value = OpenApiChannelConfigDTO.class, name = "4"),

})
public class BaseChannelConfigDTO implements Serializable {

    private static final long serialVersionUID = -8877854685518185874L;


}
