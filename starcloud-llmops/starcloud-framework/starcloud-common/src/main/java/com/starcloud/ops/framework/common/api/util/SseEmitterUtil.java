package com.starcloud.ops.framework.common.api.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-01
 */
public class SseEmitterUtil {

    /**
     * 获取 SseEmitter 实例
     * <p>
     * 1. 发送准备消息 <br>
     * 2. 解决在未发送消息时候，出现异常，异常信息将不会以 'text/event-stream' 的方式返回。但是如果发送了消息。就会以'text/event-stream'返回数据 <br>
     * 如：<br>
     * 1. 如果未发送过消息，就出现异常，返回的信息为：'{"code":300300000,"data":null,"msg":"/ by zero"}%' <br>
     * 2. 如果发送过消息，出现异常的话，返回的信息为：'data:{"code":300300000,"content":"/ by zero"}' <br>
     * 为了保持数据格式一致，创建成功，发送一次消息。保证 com.starcloud.ops.server.web.CommonResultSseMessageConverter#canWrite 可以正确获取到 mediaType。
     *
     * @param timeout 超时时间
     * @param message 是否发送第一个数据
     * @return 发送的消息
     */
    public static SseEmitter ofSseEmitterExecutor(Long timeout, String message) {
        return ofSseEmitterExecutor(timeout, true, message);
    }

    /**
     * 获取 SseEmitter 实例
     *
     * @param timeout            超时时间
     * @param isSendReadyMessage 是否发送第一个数据
     * @param message            发送的消息
     * @return 返回 SseEmitter 实例
     */
    public static SseEmitter ofSseEmitterExecutor(Long timeout, Boolean isSendReadyMessage, String message) {
        SseEmitter sseEmitter = new SseEmitter(timeout);
        if (isSendReadyMessage) {
            try {
                message = "Subscribe successfully, about to execute " + message;
                StreamResult result = new StreamResult();
                result.setCode(250);
                result.setContent(message);
                sseEmitter.send(message);
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        }
        return sseEmitter;
    }

    /**
     * 返回结果
     */
    @Data
    @NoArgsConstructor
    private static class StreamResult {

        /**
         * 状态吗
         */
        private Integer code;

        /**
         * 内容
         */
        private String content;
    }
}
