package com.starcloud.ops.llm.langchain.core.schema.callbacks;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LLMCallbackManager extends BaseCallbackManager {

    private List<BaseCallbackHandler> callbackHandlerList = new ArrayList<BaseCallbackHandler>() {{
        add(new StdOutCallbackHandler());
    }};

    @Override
    public LLMCallbackManager addCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.callbackHandlerList.add(callbackHandler);
        return this;
    }

    @Override
    BaseCallbackManager removeCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.callbackHandlerList.remove(callbackHandler);
        return this;
    }

    public LLMCallbackManager(List<BaseCallbackHandler> callbackHandlerList) {
        this.callbackHandlerList = callbackHandlerList;
    }


    public LLMCallbackManager() {
    }


    @Override
    public void onChainStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainStart(objects);
        }));
    }

    @Override
    public void onChainEnd(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainEnd(objects);
        }));
    }

    @Override
    public void onChainError(String message, Throwable throwable) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainError(message, throwable);
        }));
    }

    @Override
    public void onLLMStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMStart(objects);
        }));
    }

    @Override
    public void onLLMNewToken(Object... objects) {
        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMNewToken(objects);
        }));
    }

    @Override
    public void onLLMEnd(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMEnd(objects);
        }));
    }

    @Override
    public void onLLMError(String message) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMError(message);
        }));
    }

    @Override
    public void onLLMError(String message, Throwable throwable) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMError(message, throwable);
        }));
    }

    @Override
    public void onToolStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolStart(objects);
        }));
    }

    @Override
    public void onToolEnd(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolEnd(objects);
        }));
    }

    @Override
    public void onToolError(String message, Throwable throwable) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolError(message, throwable);
        }));
    }

    @Override
    public void onAgentAction(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolStart(objects);
        }));
    }

    @Override
    public void onAgentFinish(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolEnd(objects);
        }));
    }

}
