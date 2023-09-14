package com.starcloud.ops.business.app.util;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Sse 返回结构封装类
 */
@Slf4j
@Data
@Builder
public class SseResultUtil {

    private SseEmitter sseEmitter;

    private String conversationUid;

    private String messageUid;

    /**
     * 设置回调信息，现在只有前端 SSE使用
     *
     * @param interactiveInfo
     */
    public void sendCallbackInteractive(InteractiveInfo interactiveInfo) {
        this.sendCallbackInteractive("i", interactiveInfo);
    }

    @SneakyThrows
    public void sendCallbackInteractive(String type, InteractiveInfo interactiveInfo) {

        if (this.getSseEmitter() != null) {

            MySseCallBackHandler.StreamResult result = MySseCallBackHandler.StreamResult.builder()
                    .code(200)
                    .type(type)
                    .content(JsonUtils.toJsonString(interactiveInfo))
                    .conversationUid(this.getConversationUid())
                    .messageUid(this.getMessageUid())
                    .build();

            log.info("SseResultUtil sendCallbackInteractive: {}", JsonUtils.toJsonString(result));

            this.getSseEmitter().send(result);
        }

    }

}
