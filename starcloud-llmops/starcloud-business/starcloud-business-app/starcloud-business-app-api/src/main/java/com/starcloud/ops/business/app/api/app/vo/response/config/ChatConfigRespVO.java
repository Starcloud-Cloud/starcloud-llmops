package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.action.LLMFunctionRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 聊天应用配置实体
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
@Schema(description = "应用聊天配置响应对象")
public class ChatConfigRespVO extends BaseConfigRespVO {

    private static final long serialVersionUID = 7849659423813192733L;

    /**
     * code
     */
    @Schema(description = "code")
    private String code;

    /**
     * 挂载的 functions 列表
     */
    @Schema(description = "挂载的 functions 列表")
    private List<LLMFunctionRespVO> functions;

}
