package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@Schema(description = "聊天模型参数配置")
@NoArgsConstructor
@AllArgsConstructor
public class PrePromptConfigVO implements Serializable {

    /**
     * 用户输入 prompt
     */
    @Schema(description = "用户prompt")
    private String prePrompt;

    /**
     * 回复语气
     */
    @Schema(description = "回复语气")
    private String tone;

    /**
     * 回复的语言
     */
    @Schema(description = "回复的语言")
    private String replyLang;

    /**
     * 最大回复多少个字
     */
    @Schema(description = "最大回复多少个字")
    private Integer maxReturn;

}
