package com.starcloud.ops.business.app.api.app.vo.request.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.action.LLMFunctionReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 函数请求对象")
public class ChatConfigReqVO extends ConfigReqVO {

    private static final long serialVersionUID = -1232191599886098743L;

    /**
     * code
     */
    @Schema(description = "code")
    private String code;

    /**
     * 挂载的 functions 列表
     */
    @Valid
    @Schema(description = "挂载的 functions 列表")
    private List<LLMFunctionReqVO> functions;

}
