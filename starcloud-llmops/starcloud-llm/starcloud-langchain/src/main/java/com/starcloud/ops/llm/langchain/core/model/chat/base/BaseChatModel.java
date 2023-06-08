package com.starcloud.ops.llm.langchain.core.model.chat.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.LLMUtils;
import com.starcloud.ops.llm.langchain.core.model.llm.base.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.BaseCallbackHandler;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public abstract class BaseChatModel<R> extends BaseLanguageModel<R> {


    private static final Logger logger = LoggerFactory.getLogger(BaseChatModel.class);

    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    @Override
    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(BaseChatModel.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }


    private Boolean cache;

    private LLMCallbackManager callbackManager = new LLMCallbackManager();

    public void addCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.callbackManager.addCallbackHandler(callbackHandler);
    }

    public LLMCallbackManager getCallbackManager() {
        return callbackManager;
    }


    protected abstract ChatResult<R> _generate(List<BaseChatMessage> chatMessages);

    protected ChatResult<R> _agenerate(List<BaseChatMessage> chatMessages) {
        return null;
    }


    public ChatResult<R> generate(List<List<BaseChatMessage>> chatMessages) {

        this.getCallbackManager().onLLMStart("BaseChatModel.generate", chatMessages);

        try {

            log.debug("BaseChatModel.generate: {}", chatMessages);

            List<ChatResult<R>> chatResults = Optional.ofNullable(chatMessages).orElse(new ArrayList<>()).stream().map((chatMessageList -> {

                return this._generate(chatMessageList);
            })).collect(Collectors.toList());

            log.debug("BaseChatModel.generate result: {}", chatResults);

            this.getCallbackManager().onLLMEnd("BaseChatModel.generate", chatResults);

            return this.combineLLMOutputs(chatResults);

        } catch (Exception e) {

            this.getCallbackManager().onLLMError(e.getMessage(), e);

            throw e;
        }
    }

    @Override
    public BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues) {

        List<List<BaseChatMessage>> baseChatMessages = Optional.ofNullable(promptValues).orElse(new ArrayList<>()).stream().map(PromptValue::toMessage).collect(Collectors.toList());
        ChatResult<R> chatResult = this.generate(baseChatMessages);

        return BaseLLMResult.data(chatResult.getChatGenerations(), chatResult.getUsage());
    }


//    public ChatResult<R> generatePrompt(List<PromptValue> promptValues) {
//
//        return this.generate(Optional.ofNullable(promptValues).orElse(new ArrayList<>()).stream().map(PromptValue::toMessage).collect(Collectors.toList()));
//    }


    public String call(List<BaseChatMessage> chatMessages) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatMessages));
        return chatResult.getChatGenerations().get(0).getText();
    }


    protected ChatResult combineLLMOutputs(List<ChatResult<R>> chatResults) {

        List<BaseLLMUsage> baseLLMUsageList = Optional.ofNullable(chatResults).orElse(new ArrayList<>()).stream().map(ChatResult::getUsage).collect(Collectors.toList());
        BaseLLMUsage baseLLMUsage = LLMUtils.combineBaseLLMUsage(baseLLMUsageList);

        List<ChatGeneration<R>> generations = Optional.ofNullable(chatResults).orElse(new ArrayList<>()).stream().flatMap((baseLLMResult) -> baseLLMResult.getChatGenerations().stream()).collect(Collectors.toList());

        return ChatResult.data(generations, baseLLMUsage);

    }

    public abstract Long getNumTokens(String text);

    public abstract Long getNumTokensFromMessages(List<BaseChatMessage> messages);


}
