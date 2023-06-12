package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 模版步骤响应 DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模版步骤响应对象")
public class StepResponse {

    /**
     * 响应状态
     */
    private Boolean success;

    /**
     * 响应错误码
     */
    private String errorCode;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 相应类型
     */
    private String type;

    /**
     * 响应数据
     */
    private String style;

    /**
     * 是否显示
     */
    private Boolean isShow;

    /**
     * 响应数据
     */
    private Object data;

}
