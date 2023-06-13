package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用配置DTO")
public class AppChatConfigDTO {
    
}
