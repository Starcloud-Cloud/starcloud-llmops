package com.starcloud.ops.llm.langchain.core.callbacks;

import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

/**
 * @author starcloud
 */
public class StreamingSseCallBackHandler implements BaseCallbackHandler {
    private SseEmitter emitter;

    private String conversationId;

    public StreamingSseCallBackHandler(SseEmitter emitter) {
        this.emitter = emitter;
    }

    public StreamingSseCallBackHandler(SseEmitter emitter, String conversationId) {
        this.emitter = emitter;
        this.conversationId = conversationId;
    }

    @Override
    public Void onLLMStart(Object... objects) {

        return null;

    }

    @Override
    @SneakyThrows
    public void onLLMNewToken(Object... objects) {
        StreamResult streamResult = new StreamResult(200, objects[0].toString());
//        emitter.send(event().name("success").data(streamResult));
        emitter.send(streamResult);
    }

    @SneakyThrows
    @Override
    public void onLLMEnd(Object... objects) {

    }


    @Override
    @SneakyThrows
    public void onLLMError(String message) {
        emitter.send(event().name("error").data(new StreamResult(500, message)));
    }


    @Override
    @SneakyThrows
    public void onLLMError(String message, Throwable throwable) {

        if (message != null && message.contains("timeout")) {

            emitter.send(event().name("error").data(new StreamResult(500, "[Timeout] " + throwable.getMessage())));

        } else if (message != null && message.contains("Incorrect API key")) {
            emitter.send(event().name("error").data(new StreamResult(500, "[Incorrect Key]")));
        } else {
            emitter.send(event().name("error").data(new StreamResult(500, "[Other] Please try again later")));
        }


    }

    @Override
    public <T> T onChainStart(Object... objects) {
        return null;
    }

    @Override
    public <T> T onToolStart(Object... objects) {
        return null;
    }


    @Data
    private class StreamResult {

        private int code;

        private String content;

        private String conversationId;

        public StreamResult(int code, String content) {
            this.code = code;
            this.content = content;
        }

    }

}
