package com.starcloud.ops.business.app.api.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author starcloud
 */
@Data
@Schema(description = "自动编排")
public class RuleGenerateRequest {

    @Schema(description = "目标用户")
    @NotBlank(message = "目标用户必填")
    private String audiences;

    @Schema(description = "解决问题")
    @NotBlank(message = "解决问题必填")
    private String hopingToSolve;

}

