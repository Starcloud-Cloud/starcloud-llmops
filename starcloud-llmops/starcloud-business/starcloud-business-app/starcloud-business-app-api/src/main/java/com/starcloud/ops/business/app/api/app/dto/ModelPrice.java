package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 模型价格信息 DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "模型价格信息对象")
public class ModelPrice {

    /**
     * 模型
     */
    @Schema(description = "模型")
    private String model;

    /**
     * 价格
     */
    @Schema(description = "价格")
    private BigDecimal price;

    /**
     * 总价
     */
    @Schema(description = "总价")
    private BigDecimal totalPrice;

    /**
     * token 使用量
     */
    @Schema(description = "token 使用量")
    private Integer tokenUsage;

    /**
     * 用户标识
     */
    @Schema(description = "用户标识")
    private Long userId;

    /**
     * 用户名称
     */
    @Schema(description = "用户名称")
    private String userName;

    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private String userType;

}
