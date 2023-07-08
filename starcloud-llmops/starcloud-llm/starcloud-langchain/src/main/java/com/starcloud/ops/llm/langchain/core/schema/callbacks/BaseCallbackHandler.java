package com.starcloud.ops.llm.langchain.core.schema.callbacks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

public interface BaseCallbackHandler {

    default Boolean ignoreLLM() {
        return false;
    }

    default Boolean ignoreChain() {
        return false;
    }


    default void onChainStart(Object... objects) {

    }

    default void onChainEnd(Object... objects) {
    }


    default void onChainError(String message, Throwable throwable) {
    }


    default void onLLMStart(Object... objects) {
    }

    default void onLLMNewToken(Object... objects) {
    }

    default void onLLMEnd(Object... objects) {
    }


    default void onLLMError(String message) {
    }

    default void onLLMError(String message, Throwable throwable) {
    }


    default void onToolStart(Object... objects) {
    }

    ;

    default void onToolEnd(Object... objects) {
    }

    default void onToolError(String message, Throwable throwable) {
    }


    default void onAgentAction(Object... objects) {
    }

    default void onAgentFinish(Object... objects) {
    }

}
