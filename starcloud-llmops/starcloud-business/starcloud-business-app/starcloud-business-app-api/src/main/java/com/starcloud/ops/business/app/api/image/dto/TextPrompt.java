package com.starcloud.ops.business.app.api.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-19
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Schema(name = "TextPrompt", description = "用于生成的文本提示集合")
public class TextPrompt implements Serializable {

    private static final long serialVersionUID = 4953954645872107968L;

    /**
     * 用于生成的文本提示
     */
    @Schema(description = "用于生成的文本提示")
    private String text;

    /**
     * 用于生成的文本提示的权重
     */
    @Schema(description = "用于生成的文本提示的权重")
    private Double weight;

    /**
     * 用于生成的默认文本提示的类型
     *
     * @param text 文本
     * @return TextPrompt
     */
    public static TextPrompt ofDefault(String text) {
        TextPrompt textPrompt = new TextPrompt();
        textPrompt.setText(text);
        textPrompt.setWeight(1.0);
        return textPrompt;
    }

    /**
     * 用于生成的默认文本提示的类型
     *
     * @param text 文本
     * @return TextPrompt
     */
    public static TextPrompt ofHalfWeight(String text) {
        TextPrompt textPrompt = new TextPrompt();
        textPrompt.setText(text);
        textPrompt.setWeight(1.0);
        return textPrompt;
    }
}
