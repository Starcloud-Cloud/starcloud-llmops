package com.starcloud.ops.business.app.feign.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "PosterResponse", description = "海报请求响应")
public class PosterResponse<T> implements java.io.Serializable {

    private static final long serialVersionUID = -3606626262542939665L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 状态码
     */
    @Schema(description = "状态码")
    private Integer code;

    /**
     * 返回信息
     */
    @Schema(description = "返回信息")
    private String message;

    /**
     * 数据
     */
    @Schema(description = "数据")
    private T data;


}
