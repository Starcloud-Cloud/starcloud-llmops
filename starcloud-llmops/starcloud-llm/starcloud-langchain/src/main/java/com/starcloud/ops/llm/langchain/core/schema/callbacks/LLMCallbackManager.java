package com.starcloud.ops.llm.langchain.core.schema.callbacks;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LLMCallbackManager {

    private List<BaseCallbackHandler> callbackHandlerList = new ArrayList<BaseCallbackHandler>() {{
        add(new StdOutCallbackHandler());
    }};

    public LLMCallbackManager addCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.callbackHandlerList.add(callbackHandler);
        return this;
    }

    public LLMCallbackManager(List<BaseCallbackHandler> callbackHandlerList) {
        this.callbackHandlerList = callbackHandlerList;
    }


    public LLMCallbackManager() {
    }


    public void onLLMStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMStart(objects);
        }));
    }

    public void onLLMNewToken(Object... objects) {
        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMNewToken(objects);
        }));
    }

    public void onLLMEnd(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMEnd(objects);
        }));
    }

    public void onLLMError(String message) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMError(message);
        }));
    }

    public void onLLMError(String message, Throwable throwable) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMError(message, throwable);
        }));
    }


}
