package com.starcloud.ops.llm.langchain.core.model.chat.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.callbacks.*;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.LLMUtils;
import com.starcloud.ops.llm.langchain.core.model.llm.base.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
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

    @Override
    public String predict(String text, List<String> stops) {

        HumanMessage message = new HumanMessage(text);
        return this._call(Arrays.asList(message), stops);
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops) {
        return null;
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops, BaseCallbackManager callbackManager) {
        return null;
    }

    private Boolean cache;

    private BaseCallbackManager callbackManager = new CallbackManager();

    public void addCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.callbackManager.addCallbackHandler(callbackHandler);
    }

    public BaseCallbackManager getCallbackManager() {
        return callbackManager;
    }


    protected abstract ChatResult<R> _generate(List<BaseMessage> chatMessages, CallbackManagerForLLMRun callbackManager);


    protected ChatResult<R> _agenerate(List<BaseChatMessage> chatMessages) {
        return null;
    }


    public ChatResult<R> generate(List<List<BaseMessage>> chatMessages, List<String> stops) {

        return this.generate(chatMessages);
    }

    public ChatResult<R> generate(List<List<BaseMessage>> chatMessages) {


        List<CallbackManagerForLLMRun> llmRuns = this.getCallbackManager().onChatModelStart(this.getClass(), chatMessages);

        log.debug("BaseChatModel.generate: {}", chatMessages);

        List<ChatResult<R>> chatResults = new ArrayList<>();

        for (int i = 0; i < CollectionUtil.size(chatMessages); i++) {

            CallbackManagerForLLMRun llmRun = llmRuns.get(i);

            try {

                chatResults.add(this._generate(chatMessages.get(i), llmRun));

                //llmRun.onLLMEnd();

            } catch (Exception e) {

                llmRun.onLLMError(e.getMessage(), e);

            }
        }

        log.debug("BaseChatModel.generate result: {}", chatResults);

        //this.getCallbackManager().onChatModelEnd(this.getClass(), chatResults);

        return this.combineLLMOutputs(chatResults);
    }


//    @Deprecated
//    public ChatResult<R> generate(List<List<BaseChatMessage>> chatMessages) {
//
//        this.getCallbackManager().onLLMStart("BaseChatModel.generate.start", chatMessages);
//
//        try {
//
//            log.debug("BaseChatModel.generate: {}", chatMessages);
//
//            List<ChatResult<R>> chatResults = Optional.ofNullable(chatMessages).orElse(new ArrayList<>()).stream().map((chatMessageList -> {
//
//                return this._generate(chatMessageList);
//            })).collect(Collectors.toList());
//
//            log.debug("BaseChatModel.generate result: {}", chatResults);
//
//            this.getCallbackManager().onLLMEnd("BaseChatModel.generate.end", chatResults);
//
//            return this.combineLLMOutputs(chatResults);
//
//        } catch (Exception e) {
//
//            this.getCallbackManager().onLLMError(e.getMessage(), e);
//
//            throw e;
//        }
//    }

    @Override
    public BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues) {

        //@todo 结构不对多余的转换
        List<List<BaseMessage>> baseMessages = Optional.ofNullable(promptValues).orElse(new ArrayList<>()).stream().map((PromptValue::toMessage)).collect(Collectors.toList());

        ChatResult<R> chatResult = this.generate(baseMessages, null);

        return BaseLLMResult.data(chatResult.getChatGenerations(), chatResult.getUsage());
    }

    public String _call(List<BaseMessage> chatMessages) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatMessages), null);
        return chatResult.getChatGenerations().get(0).getText();
    }

    public String _call(List<BaseMessage> chatMessages, List<String> stops) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatMessages), stops);
        return chatResult.getChatGenerations().get(0).getText();
    }


    protected ChatResult combineLLMOutputs(List<ChatResult<R>> chatResults) {

        List<BaseLLMUsage> baseLLMUsageList = Optional.ofNullable(chatResults).orElse(new ArrayList<>()).stream().map(ChatResult::getUsage).collect(Collectors.toList());
        BaseLLMUsage baseLLMUsage = LLMUtils.combineBaseLLMUsage(baseLLMUsageList);

        List<ChatGeneration<R>> generations = Optional.ofNullable(chatResults).orElse(new ArrayList<>()).stream().filter((chatResult) -> CollectionUtil.isNotEmpty(chatResult.getChatGenerations())).flatMap((chatResult) -> chatResult.getChatGenerations().stream()).collect(Collectors.toList());

        return ChatResult.data(generations, baseLLMUsage);

    }

}
