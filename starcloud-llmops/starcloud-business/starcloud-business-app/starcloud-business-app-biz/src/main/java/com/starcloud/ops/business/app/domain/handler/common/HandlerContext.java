package com.starcloud.ops.business.app.domain.handler.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.SseResultUtil;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
public class HandlerContext<Q> {

    private Long userId;

    private Long endUser;

    private String appUid;

    private String conversationUid;

    private String messageUid;

    private SseEmitter sseEmitter;

    private AppSceneEnum scene;

    private Q request;

    @JsonIgnore
    private InteractiveInfo currentInteractive;

    private HandlerContext() {
    }


    /**
     * 开始交互反馈
     *
     * @param interactiveInfo
     */
    public void sendCallbackInteractiveStart(InteractiveInfo interactiveInfo) {

        this.currentInteractive = interactiveInfo;

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
     * 当前交互信息最终异常
     *
     * @param code
     * @param error
     */
    public void sendCurrentInteractiveError(Integer code, String error) {

        //异常，使用最近一次的互动信息
        if (this.getCurrentInteractive() != null) {
            InteractiveInfo current = this.getCurrentInteractive();
            current.setStatus(1);
            current.setSuccess(false);
            current.setErrorCode(code);
            current.setErrorMsg(error);
            this.sendCallbackInteractiveEnd(current);

            log.info("BaseHandler {} execute sendCallbackInteractiveEnd: {}", this.getClass().getSimpleName(), JsonUtils.toJsonString(current));
        }

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
                    .content(JsonUtils.toJsonString(interactiveInfo))
                    .conversationUid(this.getConversationUid())
                    .messageUid(this.getMessageUid())
                    .build();

            log.info("sendCallbackInteractive: {}", JsonUtils.toJsonString(interactiveInfo));

            this.getSseEmitter().send(result);
        }
    }

    public static <Q> HandlerContext<Q> createContext(ChatRequestVO chatRequestVO) {

        return new HandlerContext<Q>().setAppUid(chatRequestVO.getAppUid()).setUserId(chatRequestVO.getUserId()).setEndUser(Optional.ofNullable(chatRequestVO.getEndUser()).map(Long::valueOf).orElse(null)).setConversationUid(chatRequestVO.getConversationUid()).setScene(AppSceneEnum.valueOf(chatRequestVO.getScene()));
    }


    public static <Q> HandlerContext<Q> createContext(String appUid, String conversationUid, Long userId, Long endUser, AppSceneEnum scene, Q request) {

        return new HandlerContext<Q>().setAppUid(appUid).setUserId(userId).setEndUser(endUser).setConversationUid(conversationUid).setScene(scene).setRequest(request);
    }


}
