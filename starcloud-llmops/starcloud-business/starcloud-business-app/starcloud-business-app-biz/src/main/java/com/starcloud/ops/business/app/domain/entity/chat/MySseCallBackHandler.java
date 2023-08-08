package com.starcloud.ops.business.app.domain.entity.chat;

import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import lombok.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author starcloud
 */
public class MySseCallBackHandler extends StreamingSseCallBackHandler {

    /**
     * 聊天请求参数
     */
    private ChatRequestVO chatRequestVO;

    public MySseCallBackHandler(SseEmitter emitter, ChatRequestVO chatRequestVO) {
        super(emitter, chatRequestVO.getConversationUid());
        this.chatRequestVO = chatRequestVO;
    }


    @Override
    @SneakyThrows
    public void onLLMNewToken(Object... objects) {
        if (this.getEmitter() == null) {
            return;
        }

        StreamResult streamResult = StreamResult.builder()
                .code(200)
                .type("m")
                .content(objects[0].toString())
                .conversationUid(chatRequestVO.getConversationUid())
                .messageUid(chatRequestVO.getMessageUid())
                .build();

        this.getEmitter().send(streamResult);
    }


    @Override
    @SneakyThrows
    public void onLLMError(String message) {
        if (this.getEmitter() == null) {
            return;
        }
        StreamResult streamResult = StreamResult.builder()
                .code(500)
                .type("m")
                .content(message)
                .conversationUid(chatRequestVO.getConversationUid())
                .messageUid(chatRequestVO.getMessageUid())
                .build();

        this.getEmitter().send(streamResult);
    }


    //@todo onLLMError 有空在处理


    @Builder
    @Data
    public static class StreamResult {

        private int code;

        private String type;

        private String content;

        private String error;

        private String messageUid;

        private String conversationUid;

    }

}
