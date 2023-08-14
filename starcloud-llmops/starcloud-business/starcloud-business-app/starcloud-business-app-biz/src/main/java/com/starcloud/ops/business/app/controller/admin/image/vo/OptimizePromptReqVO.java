package com.starcloud.ops.business.app.controller.admin.image.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-10
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Schema(description = "优化提示请求对象")
public class OptimizePromptReqVO implements Serializable {

    private static final long serialVersionUID = -8438713918272194439L;

    /**
     * sse对象
     */
    @Schema(description = "sse对象")
    private SseEmitter sseEmitter;

    /**
     * 应用市场UID
     */
    @Schema(description = "应用市场UID")
    @NotBlank(message = "应用市场UID不能为空")
    private String appUid;

    /**
     * 需要优化的文本
     */
    @Schema(description = "需要优化的文本")
    private String content;

    /**
     * 目标语言
     */
    @Schema(description = "目标语言")
    private String language;
}
