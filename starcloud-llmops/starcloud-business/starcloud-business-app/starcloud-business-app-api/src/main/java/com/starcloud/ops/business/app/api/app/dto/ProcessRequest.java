package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 流程请求 DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "流程请求对象")
public class ProcessRequest {

    /**
     * 响应状态
     */
    @Schema(description = "响应状态")
    private Boolean success;

    /**
     * 标识
     */
    @Schema(description = "标识")
    private String mark;

    /**
     * 请求数据
     */
    @Schema(description = "请求数据")
    private Object request;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private Object response;

    /**
     * 模型价格
     */
    @Schema(description = "模型价格")
    private ModelPrice modelPrice;

}
