package com.starcloud.ops.llm.langchain.core.model.llm.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author df007df
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseLLMResult<R> {

    private Boolean success;

    private String text;

    private R llmOutput;

    private BaseLLMUsage usage;

    public static <R> BaseLLMResult<R> success(String text, R output, BaseLLMUsage usage) {
        return BaseLLMResult.<R>builder().success(true).text(text).llmOutput(output).usage(usage).build();
    }

    public static <R> BaseLLMResult<R> success(String text) {
        return BaseLLMResult.<R>builder().success(true).text(text).build();
    }
}
