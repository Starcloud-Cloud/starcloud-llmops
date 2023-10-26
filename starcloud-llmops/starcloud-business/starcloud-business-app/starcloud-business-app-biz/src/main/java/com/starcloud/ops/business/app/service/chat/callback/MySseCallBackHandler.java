package com.starcloud.ops.business.app.service.chat.callback;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author starcloud
 */
@Slf4j
public class MySseCallBackHandler extends StreamingSseCallBackHandler {

    /**
     * 聊天请求参数
     */
    private ChatRequestVO chatRequest;

    public MySseCallBackHandler(SseEmitter emitter) {
        super(emitter);
    }

    public MySseCallBackHandler(SseEmitter emitter, ChatRequestVO chatRequest) {
        super(emitter);
        this.chatRequest = chatRequest;
    }

    private Boolean canSend() {
        if (this.getEmitter() == null) {
            return false;
        }
        return true;
    }

    @SneakyThrows
    private void sendStreamResult(StreamResult streamResult) {

        if (this.canSend()) {
            try {
                this.getEmitter().send(streamResult);
            } catch (IOException e) {
                //判断为See断开了，前端停止操作
                log.error("See is error: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    @SneakyThrows
    public void onLLMNewToken(Object... objects) {
        if (Boolean.FALSE.equals(this.canSend())) {
            return;
        }

        StreamResult streamResult = StreamResult.builder()
                .code(200)
                .type("m")
                .content(objects[0].toString())
                .build();

        if (chatRequest != null) {

            streamResult = StreamResult.builder()
                    .code(200)
                    .type("m")
                    .content(objects[0].toString())
                    .conversationUid(chatRequest.getConversationUid())
                    .messageUid(chatRequest.getMessageUid())
                    .build();
        }

        this.sendStreamResult(streamResult);
    }


    @Override
    @SneakyThrows
    public void onLLMError(String message) {
        if (Boolean.FALSE.equals(this.canSend())) {
            return;
        }

        StreamResult streamResult = StreamResult.builder()
                .code(500)
                .type("m")
                .content(message)
                .build();

        if (chatRequest != null) {
            streamResult = StreamResult.builder()
                    .code(500)
                    .type("m")
                    .content(message)
                    .conversationUid(chatRequest.getConversationUid())
                    .messageUid(chatRequest.getMessageUid())
                    .build();
        }

        this.sendStreamResult(streamResult);
    }

    @Override
    @SneakyThrows
    public void onLLMError(String message, Throwable throwable) {
        if (Boolean.FALSE.equals(this.canSend())) {
            return;
        }

        message = parseOpenApiError(message, throwable);

        StreamResult streamResult = StreamResult.builder()
                .code(500)
                .type("m")
                .content(message)
                .build();

        if (chatRequest != null) {
            streamResult = StreamResult.builder()
                    .code(500)
                    .type("m")
                    .content(message)
                    .conversationUid(chatRequest.getConversationUid())
                    .messageUid(chatRequest.getMessageUid())
                    .build();
        }

        log.error("MySseCallBack onLLMError send: {}", JsonUtils.toJsonString(streamResult));

        this.sendStreamResult(streamResult);
    }


    @Override
    public <T> T onChainStart(Object... objects) {
        return null;
    }


    @Override
    @SneakyThrows
    public void onChainEnd(Object... objects) {
        log.info("onChainEnd: {}", objects[0].getClass().getSimpleName());
        //因为Agent执行是同步的，所以最后才有返回结果，需要手动See下
        if (Boolean.FALSE.equals(this.canSend())) {
            return;
        }

        if (objects[0] instanceof AgentExecutor) {

            if (objects[1] instanceof AgentFinish) {
                AgentFinish agentFinish = (AgentFinish) objects[1];

                StreamResult streamResult = StreamResult.builder()
                        .code(200)
                        .type("m")
                        .content(String.valueOf(agentFinish.getOutput()))
                        .build();

                //结束失败了
                if (!Boolean.TRUE.equals(agentFinish.getStatus())) {

                    streamResult = StreamResult.builder()
                            .code(agentFinish.getErrorCode())
                            .type("m")
                            .error(agentFinish.getError())
                            .build();
                }

                if (chatRequest != null) {
                    streamResult.setConversationUid(chatRequest.getConversationUid());
                    streamResult.setMessageUid(chatRequest.getMessageUid());
                }

                log.info("MySseCallBack onChainEnd send: {}", JsonUtils.toJsonString(streamResult));

                this.sendStreamResult(streamResult);

            }
        }
    }

    @SneakyThrows
    @Override
    public void onChainError(String message, Throwable throwable) {

        //chain 抛异常，让上游统一处理 跟前端的结果
        log.error("onChainError: {}", message, throwable);

        //因为Agent执行是同步的，所以最后才有返回结果，需要手动See下
        if (Boolean.FALSE.equals(this.canSend())) {
            return;
        }

        StreamResult streamResult = StreamResult.builder()
                .code(-1)
                .type("m")
                .error(message)
                .build();

        if (throwable instanceof ServiceException) {

            streamResult = StreamResult.builder()
                    .code(((ServiceException) throwable).getCode())
                    .type("m")
                    .error(message)
                    .build();
        }

        this.sendStreamResult(streamResult);

    }

    @Override
    public <T> T onToolStart(Object... objects) {

        log.info("onToolStart: {} {}", objects);
        return null;
    }


    @Override
    public void onToolEnd(Object... objects) {

        log.info("onToolEnd: {} {} {}", objects);
    }


    @Override
    public void onToolError(String message, Throwable throwable) {
        log.info("onToolError: {}", message, throwable);

    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class StreamResult {

        private int code;

        /**
         * m: AI生成内容
         * i: AI思考交互
         */
        private String type;

        private String content;

        private String error;

        private String messageUid;

        private String conversationUid;

        public StreamResult(int code, String content) {
            this.code = code;
            this.content = content;
        }
    }

}
