package com.starcloud.ops.business.app.domain.handler.common;

import lombok.Data;

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

    private Q request;

    private HandlerContext() {
    }

    public static <Q> HandlerContext<Q> createContext(String conversationUid, Long userId, Q request) {

        return new HandlerContext<Q>().setUserId(userId).setConversationUid(conversationUid).setRequest(request);
    }


}
