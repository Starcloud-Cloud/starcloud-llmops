package com.starcloud.ops.business.app.feign.response;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
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
public class VectorSearchResponse<R> implements Serializable {

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

    /**
     * 成功响应
     *
     * @param result 响应结果
     * @return 成功响应
     */
    public static <R> VectorSearchResponse<R> success(R result) {
        VectorSearchResponse<R> response = new VectorSearchResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setCode(200);
        response.setMessage(null);
        response.setResult(result);
        return response;
    }

    /**
     * 失败响应
     *
     * @param code    错误吗
     * @param message 错误信息
     * @return 响应
     */
    public static <R> VectorSearchResponse<R> failure(Integer code, String message) {
        VectorSearchResponse<R> response = new VectorSearchResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setCode(code);
        response.setMessage(message);
        response.setResult(null);
        return response;
    }

    /**
     * 失败响应
     *
     * @param errorCode 错误吗
     * @return 响应
     */
    public static <R> VectorSearchResponse<R> failure(ErrorCode errorCode) {
        VectorSearchResponse<R> response = new VectorSearchResponse<>();
        response.setSuccess(Boolean.FALSE);
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMsg());
        response.setResult(null);
        return response;
    }
}
