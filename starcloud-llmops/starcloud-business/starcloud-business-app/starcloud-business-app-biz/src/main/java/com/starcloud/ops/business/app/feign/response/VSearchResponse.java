package com.starcloud.ops.business.app.feign.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Schema(name = "StabilityResponse", description = "Stability Ai 基础响应")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VSearchResponse<R> implements Serializable {

    private static final long serialVersionUID = 4052376340382017013L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private Integer code;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String message;

    /**
     * 响应结果
     */
    @Schema(description = "响应结果")
    private R result;


}
