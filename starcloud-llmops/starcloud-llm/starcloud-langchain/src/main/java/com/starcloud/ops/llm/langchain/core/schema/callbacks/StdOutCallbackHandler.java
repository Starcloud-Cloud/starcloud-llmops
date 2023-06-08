package com.starcloud.ops.llm.langchain.core.schema.callbacks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class StdOutCallbackHandler extends BaseCallbackHandler {


    @Override
    public void onLLMStart(Object... objects) {

        log.info("onLLMStart: {}", objects);
    }

    @Override
    public void onLLMNewToken(Object... objects) {
        log.info("onLLMNewToken: {}", objects);
    }

    @Override
    public void onLLMEnd(Object... objects) {
        log.info("onLLMEnd: {}", objects);
    }


    @Override
    public void onLLMError(String message) {
        log.info("onLLMError: {}", message);
    }


    @Override
    public void onLLMError(String message, Throwable throwable) {
        log.info("onLLMError:{}", message, throwable);
    }

}
