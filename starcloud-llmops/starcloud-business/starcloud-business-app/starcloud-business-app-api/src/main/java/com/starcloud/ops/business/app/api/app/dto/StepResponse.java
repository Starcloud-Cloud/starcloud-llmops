package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 应用步骤响应 DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用步骤响应对象")
public class StepResponse {

    /**
     * 响应状态
     */
    @Schema(description = "响应状态")
    private Boolean success;

    /**
     * 响应错误码
     */
    @Schema(description = "响应错误码")
    private String errorCode;

    /**
     * 响应信息
     */
    @Schema(description = "响应信息")
    private String message;

    /**
     * 响应类型
     */
    @Schema(description = "响应类型")
    private String type;

    /**
     * 响应样式
     */
    @Schema(description = "响应样式")
    private String style;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示")
    private Boolean isShow;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private Object data;

    /**
     * 流程请求参数
     */
    @Schema(description = "流程请求参数")
    private ProcessRequest processRequest;

}
