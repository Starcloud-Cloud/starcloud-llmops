package com.starcloud.ops.business.app.api.limit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-29
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@Schema(name = "DictionaryLimitDTO", description = "限流配置字典DTO")
public class DictionaryLimitDTO implements Serializable {

    private static final long serialVersionUID = 7193020982911411325L;

    /**
     * 限流配置编码
     */
    @Schema(description = "限流配置编码")
    private String code;

    /**
     * 限流配置是否启用
     */
    @Schema(description = "限流配置是否启用")
    private Boolean enable;

    /**
     * 限流配置
     */
    @Schema(description = "限流配置")
    private LimitConfigDTO config;

}
