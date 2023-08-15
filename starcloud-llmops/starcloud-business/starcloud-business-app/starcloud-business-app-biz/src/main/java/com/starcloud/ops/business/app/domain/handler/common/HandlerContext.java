package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.entity.chat.MySseCallBackHandler;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
public class HandlerContext<Q> {

    private Long userId;

    private String appUid;

    private String conversationUid;

    private String messageUid;

    private SseEmitter sseEmitter;

    private Q request;

    private HandlerContext() {
    }


    /**
     * 开始交互反馈
     *
     * @param interactiveInfo
     */
    public void sendCallbackInteractiveStart(InteractiveInfo interactiveInfo) {

        interactiveInfo.setTime(DateUtil.date());
        interactiveInfo.setStatus(0);
        //新建一个
        if (StrUtil.isBlank(interactiveInfo.getId())) {
            interactiveInfo.setId(RandomUtil.randomNumbers(5));
        }

        this.sendCallbackInteractive(interactiveInfo);
    }

    /**
     * 完成交互反馈
     *
     * @param interactiveInfo
     */
    public void sendCallbackInteractiveEnd(InteractiveInfo interactiveInfo) {

        interactiveInfo.setStatus(1);
        this.sendCallbackInteractive(interactiveInfo);

    }

    /**
     * 设置回调信息，现在只有前端 SSE使用
     *
     * @param interactiveInfo
     */
    @SneakyThrows
    private void sendCallbackInteractive(InteractiveInfo interactiveInfo) {

        if (this.getSseEmitter() != null) {

            MySseCallBackHandler.StreamResult result = MySseCallBackHandler.StreamResult.builder()
                    .code(200)
                    .type("i")
                    .content(JSONUtil.toJsonStr(interactiveInfo))
                    .conversationUid(this.getConversationUid())
                    .messageUid(this.getMessageUid())
                    .build();

            log.info("sendCallbackInteractive: {}", interactiveInfo);

            this.getSseEmitter().send(result);
        }

    }

    public static <Q> HandlerContext<Q> createContext(String appUid, String conversationUid, Long userId) {

        return new HandlerContext<Q>().setAppUid(appUid).setUserId(userId).setConversationUid(conversationUid);
    }

    public static <Q> HandlerContext<Q> createContext(String appUid, String conversationUid, Long userId, Q request) {

        return new HandlerContext<Q>().setAppUid(appUid).setUserId(userId).setConversationUid(conversationUid).setRequest(request);
    }


}
