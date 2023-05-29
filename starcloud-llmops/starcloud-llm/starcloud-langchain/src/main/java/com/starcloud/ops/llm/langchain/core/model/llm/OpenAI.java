package com.starcloud.ops.llm.langchain.core.model.llm;

import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseOpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;

/**
 * @author df007df
 */
public class OpenAI extends BaseOpenAI<CompletionRequest, CompletionResult> {

    private CompletionRequest completionRequest;

    public OpenAI() {
        this.completionRequest = new CompletionRequest();
        this.completionRequest.setMaxTokens(500);
        this.completionRequest.setTemperature(0.5);
        this.completionRequest.setN(1);
        this.completionRequest.setModel("text-davinci-003");
    }


    @Override
    public BaseLLMResult<CompletionResult> call(CompletionRequest completionRequest) {

        OpenAiService openAiService = new OpenAiService(OpenAIConfig.apiKey, Duration.ofSeconds(OpenAIConfig.timeOut));

        CompletionResult completionResult = openAiService.createCompletion(completionRequest);
        String text = completionResult.getChoices().get(0).getText();

        BaseLLMUsage usage = BaseLLMUsage.builder()
                .promptTokens(completionResult.getUsage().getPromptTokens())
                .completionTokens(completionResult.getUsage().getCompletionTokens())
                .totalTokens(completionResult.getUsage().getTotalTokens())
                .build();

        return BaseLLMResult.success(text, completionResult, usage);
    }


    @Override
    public BaseLLMResult<CompletionResult> generatePrompt(PromptValue promptValue) {

        this.completionRequest.setPrompt(promptValue.getStr());
        return this.call(this.completionRequest);
    }


}
