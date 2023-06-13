package com.starcloud.ops.llm.langchain.core.model.llm.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Data
public abstract class BaseLLM<R> extends BaseLanguageModel<R> {

    private static final Logger logger = LoggerFactory.getLogger(BaseLLM.class);

    private Boolean cache;

    private LLMCallbackManager callbackManager = new LLMCallbackManager();

    public LLMCallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(LLMCallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }


    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    @Override
    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(BaseLLM.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }


    protected abstract BaseLLMResult<R> _generate(List<String> texts);

    protected BaseLLMResult<R> _agenerate(List<String> texts) {
        return null;
    }


    public BaseLLMResult<R> generate(List<String> prompts) {


        logger.debug("BaseLLM.generate: {}", prompts);

        this.getCallbackManager().onLLMStart();

        if (!this.isLLMCache()) {

            try {

                BaseLLMResult<R> baseLLMResult = this._generate(prompts);

                logger.debug("BaseLLM.generate result: {}", baseLLMResult);

                this.getCallbackManager().onLLMEnd();

                return baseLLMResult;

            } catch (Exception exc) {

                this.getCallbackManager().onLLMError(exc.getMessage(), exc);
            }

        } else {

            Map<String, List<BaseGeneration<R>>> cachePrompts = getCachePrompts(prompts);

            try {

                BaseLLMResult<R> baseLLMResult = this._generate(prompts);

                logger.debug("BaseLLM.generate result: {}", baseLLMResult);

                this.getCallbackManager().onLLMEnd();

                Map llmOutput = updatePromptsCache(prompts, baseLLMResult);

                return BaseLLMResult.data(baseLLMResult.getGenerations(), llmOutput);

            } catch (Exception exc) {

                this.getCallbackManager().onLLMError(exc.getMessage(), exc);
            }
        }


        return null;
    }

    @Override
    public BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues) {

        return this.generate(Optional.ofNullable(promptValues).orElse(new ArrayList<>()).stream().map(PromptValue::toStr).collect(Collectors.toList()));
    }

    public String call(String text) {
        BaseLLMResult<R> baseLLMResult = this._generate(Arrays.asList(text));
        return baseLLMResult.getGenerations().get(0).getText();
    }


    private Boolean isLLMCache() {
        return false;
    }

    private Map<String, List<BaseGeneration<R>>> getCachePrompts(List<String> promptValueList) {

        return null;
    }

    private Map updatePromptsCache(List<String> prompts, BaseLLMResult<R> baseLLMResult) {
        return baseLLMResult.getLlmOutput();
    }

    public void save(String path) {

    }

}

