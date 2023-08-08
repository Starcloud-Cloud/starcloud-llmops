package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.entity.chat.MySseCallBackHandler;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class HandlerContext<Q> {

    private Long userId;

    private String conversationUid;

    private String messageUid;

    private SseEmitter sseEmitter;

    private Q request;

    private HandlerContext() {
    }

    /**
     * 设置回调信息，现在只有前端 SSE使用
     *
     * @param interactiveInfo
     */
    @SneakyThrows
    public void sendCallbackInteractive(InteractiveInfo interactiveInfo) {

        if (this.getSseEmitter() != null) {

            MySseCallBackHandler.StreamResult result = MySseCallBackHandler.StreamResult.builder()
                    .code(200)
                    .type("i")
                    .content(JSONUtil.toJsonStr(interactiveInfo))
                    .conversationUid(this.getConversationUid())
                    .messageUid(this.getMessageUid())
                    .build();

            this.getSseEmitter().send(result);
        }


    }


    public static <Q> HandlerContext<Q> createContext(String conversationUid, Long userId, Q request) {

        return new HandlerContext<Q>().setUserId(userId).setConversationUid(conversationUid).setRequest(request);
    }


}
