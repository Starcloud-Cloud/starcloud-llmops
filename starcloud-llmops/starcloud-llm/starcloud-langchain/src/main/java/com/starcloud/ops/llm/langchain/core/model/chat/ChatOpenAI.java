package com.starcloud.ops.llm.langchain.core.model.chat;

import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BatchChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
public class ChatOpenAI extends BaseChatModel<ChatCompletionRequest, ChatCompletionResult> {

    private ChatCompletionRequest chatCompletionRequest;

    public ChatOpenAI(ChatCompletionRequest chatCompletionRequest) {
        this.chatCompletionRequest = chatCompletionRequest;
    }

    public ChatOpenAI() {

        this.chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .maxTokens(500)
                .temperature(0.5)
                .n(1)
                .build();
    }


    @Override
    public BaseLLMResult<ChatCompletionResult> call(ChatCompletionRequest completionRequest) {

        OpenAiService openAiService = new OpenAiService(OpenAIConfig.apiKey, Duration.ofSeconds(OpenAIConfig.timeOut));

        ChatCompletionResult chatCompletionResult = openAiService.createChatCompletion(completionRequest);

        ChatMessage chatMessage = chatCompletionResult.getChoices().get(0).getMessage();
        Usage usage = chatCompletionResult.getUsage();

        BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder()
                .promptTokens(usage.getPromptTokens())
                .completionTokens(usage.getCompletionTokens())
                .totalTokens(usage.getTotalTokens())
                .build();

        return BaseLLMResult.success(chatMessage.getContent(), chatCompletionResult, baseLLMUsage);
    }


    @Override
    protected BaseLLMResult<ChatCompletionResult> generate(BatchChatMessage batchChatMessage) {

        List<ChatMessage> messages = Optional.ofNullable(batchChatMessage.getMessages()).orElse(new ArrayList<>()).stream().map(baseChatMessage -> {
            return new ChatMessage(baseChatMessage.getRole(), baseChatMessage.getContent());
        }).collect(Collectors.toList());

        this.chatCompletionRequest.setMessages(messages);
        return this.call(this.chatCompletionRequest);
    }


}
