package com.starcloud.ops.llm.langchain.core.callbacks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class StdOutCallbackHandler implements BaseCallbackHandler {


    @Override
    public Void onChainStart(Object... objects) {
        log.info("onChainStart: {}\n {} \n{}", objects);
        return null;
    }

    @Override
    public void onChainEnd(Object... objects) {
        log.info("onChainEnd: {} {} {}", objects);
    }

    @Override
    public void onChainError(String message, Throwable throwable) {
        log.error("onChainError: {} {}", message, throwable);
    }


    @Override
    public <T> T onLLMStart(Object... objects) {
        return null;
    }

    @Override
    public void onLLMError(String message, Throwable throwable) {
        log.error("onLLMError:{}", message, throwable);
    }


    @Override
    public <T> T onChatModelStart(Object... objects) {
        return null;
    }

    @Override
    public <T> T onToolStart(Object... objects) {
        return null;
    }
}
