package com.starcloud.ops.llm.langchain.core.model.chat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import io.reactivex.functions.Action;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Slf4j
@Data
public class ChatOpenAI extends BaseChatModel<ChatCompletionResult> {

    private String model = "gpt-3.5-turbo";

    private List<ChatMessage> messages;

    private Double temperature = 0.7d;

    private Double topP = 1d;

    private Integer n = 1;

    private Boolean stream = false;

    private List<String> stop;

    private Integer maxTokens = 500;

    private Double presencePenalty = 0d;

    private Double frequencyPenalty = 0d;


    @Override
    public ChatResult<ChatCompletionResult> _generate(List<BaseChatMessage> messages) {

        OpenAIConfig openAIConfig = SpringUtil.getBean(OpenAIConfig.class);

        OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(openAIConfig.getTimeOut()));

        ChatCompletionRequest chatCompletionRequest = BeanUtil.toBean(this, ChatCompletionRequest.class);

        List<ChatMessage> chatMessages = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(message -> {
            return new ChatMessage(message.getRole(), message.getContent());
        }).collect(Collectors.toList());

        chatCompletionRequest.setMessages(chatMessages);

        ChatResult chatResult = new ChatResult();

        if (chatCompletionRequest.getStream()) {

            StringBuffer sb = new StringBuffer();

            this.getCallbackManager().onLLMStart(this.getClass().getSimpleName(), chatCompletionRequest);

            openAiService.streamChatCompletion(chatCompletionRequest)
                    .doOnError(e -> {
                        if (e.getMessage() != null && e.getMessage().contains("timeout")) {

                            this.getCallbackManager().onLLMError("[timeout]" + e.getCause().getMessage(), e);

                        } else {

                            this.getCallbackManager().onLLMError(e.getCause().getMessage(), e);
                        }

                        log.error("chat stream error:", e);
                        String error = "&error&" + e.getMessage();

                        this.getCallbackManager().onLLMNewToken(error);

                    })
                    .doOnComplete(() -> {
                        String text = sb.toString();

                        //todo usage
                        BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder().build();

                        chatResult.setChatGenerations(Arrays.asList(ChatGeneration.builder().chatMessage(AIMessage.builder().content(text).build()).usage(baseLLMUsage).build()));
                        chatResult.setUsage(baseLLMUsage);

                        this.getCallbackManager().onLLMEnd("complete");
                    })
                    .doFinally(() -> {

                        this.getCallbackManager().onLLMEnd();
                    })
                    .blockingForEach(t -> {
                        String msg = t.getChoices().get(0).getMessage().getContent();
                        if (msg != null) {
                            sb.append(msg);
                            this.getCallbackManager().onLLMNewToken(msg);
                        }
                        if ("stop".equals(t.getChoices().get(0).getFinishReason())) {

                            String endString = "&end&";

                            this.getCallbackManager().onLLMNewToken(endString);

//                            this.getCallbackManager().onLLMEnd("stop");

                        }
                    });


            return chatResult;

        } else {

            ChatCompletionResult chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

            ChatMessage chatMessage = chatCompletionResult.getChoices().get(0).getMessage();
            Usage usage = chatCompletionResult.getUsage();

            BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder()
                    .promptTokens(usage.getPromptTokens())
                    .completionTokens(usage.getCompletionTokens())
                    .totalTokens(usage.getTotalTokens())
                    .build();

            BaseChatMessage baseChatMessage = BaseChatMessage.ofRole(chatMessage.getRole()).setContent(chatMessage.getContent());
            ChatGeneration chatGeneration = ChatGeneration.builder().generationInfo(chatCompletionResult).usage(baseLLMUsage).chatMessage(baseChatMessage).text(baseChatMessage.getContent()).build();

            return ChatResult.data(Arrays.asList(chatGeneration), chatCompletionResult, baseLLMUsage);

        }


    }

    @Override
    public Long getNumTokens(String text) {
        return null;
    }


    @Override
    public Long getNumTokensFromMessages(List<BaseChatMessage> messages) {

        Long sum = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map((message) -> {
            message.setTokens(this.getNumTokens(message.getContent()));
            return message.getTokens();
        }).reduce(0L, Long::sum);

        return sum;
    }


}
