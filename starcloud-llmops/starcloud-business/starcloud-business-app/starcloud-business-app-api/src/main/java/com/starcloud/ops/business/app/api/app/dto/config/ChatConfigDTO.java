package com.starcloud.ops.business.app.api.app.dto.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.action.LLMFunctionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 聊天应用配置DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用配置DTO")
public class ChatConfigDTO extends BaseConfigDTO {

    private static final long serialVersionUID = -3558039190933774947L;

    /**
     * code
     */
    @Schema(description = "code")
    private String code;

    /**
     * 挂载的 functions 列表
     */
    @Schema(description = "挂载的 functions 列表")
    private List<LLMFunctionDTO> functions;
}
