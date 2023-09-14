package com.starcloud.ops.business.open.service.impl;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.exception.AppLimitException;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.chat.context.RobotContextHolder;
import com.starcloud.ops.business.open.controller.admin.vo.request.QaCallbackReqVO;
import com.starcloud.ops.business.chat.worktool.WorkToolClient;
import com.starcloud.ops.business.chat.worktool.request.BaseReq;
import com.starcloud.ops.business.chat.worktool.request.SendMessageReq;
import com.starcloud.ops.business.chat.worktool.response.BaseResponse;
import com.starcloud.ops.business.open.service.WecomChatService;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

import static com.starcloud.ops.business.limits.enums.ErrorCodeConstants.USER_BENEFITS_USELESS_INSUFFICIENT;

@Slf4j
@Service
public class WecomChatServiceImpl implements WecomChatService {

    @Resource
    private WorkToolClient workToolClient;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private ThreadWithContext threadWithContext;

    @Resource
    private EndUserServiceImpl endUserService;

    @Resource
    private AppLimitService appLimitService;


    @Override
    public void asynReplyMsg(QaCallbackReqVO reqVO) {
        TenantContextHolder.setIgnore(true);
        AppPublishChannelRespVO channelRespVO = appPublishChannelService.getAllByMediumUid(reqVO.getGroupRemark());
        if (channelRespVO == null) {
            sendMsg(reqVO.getGroupRemark(), "渠道绑定不存在，请联系管理员", reqVO.getReceivedName());
            return;
        }

        if (channelRespVO.getStatus() == null || channelRespVO.getStatus() != 0) {
            sendMsg(reqVO.getGroupRemark(), "此渠道已禁用，请联系管理员启用", reqVO.getReceivedName());
            return;
        }
        String userNameMd5 = userNameMd5(reqVO.getGroupRemark() + reqVO.getReceivedName());
        String endUserId = endUserService.weChatLogin(userNameMd5);

        AppLimitRequest limitRequest = AppLimitRequest.of(channelRespVO.getMediumUid(), AppSceneEnum.WECOM_GROUP.name(), endUserId);

        try {
            appLimitService.channelLimit(limitRequest);
        } catch (AppLimitException e) {
            sendMsg(reqVO.getGroupRemark(), e.getMessage(), reqVO.getReceivedName());
            return;
        }

        ChatRequestVO chatRequestVO = new ChatRequestVO();
        chatRequestVO.setAppUid(channelRespVO.getAppUid());
        chatRequestVO.setQuery(reqVO.getSpoken());
        chatRequestVO.setScene(AppSceneEnum.WECOM_GROUP.name());
        chatRequestVO.setEndUser(endUserId);
        chatRequestVO.setConversationUid(userNameMd5);
        chatRequestVO.setMediumUid(channelRespVO.getMediumUid());
        String robotId = RobotContextHolder.getRobotId();
        threadWithContext.asyncExecute(() -> {
            try {
                TenantContextHolder.setIgnore(true);
                RobotContextHolder.setRobotId(robotId);
                ChatAppEntity<ChatRequestVO, JsonData> appEntity = AppFactory.factoryChatAppByPublishUid(channelRespVO.getPublishUid());
                JsonData execute = appEntity.execute(chatRequestVO);
                String msg = JSONUtil.parseObj(execute.getData()).getStr("text");

                if (StringUtils.isNotBlank(msg)) {
                    sendMsg(reqVO.getGroupRemark(), msg, reqVO.getReceivedName());
                } else {
                    sendMsg(reqVO.getGroupRemark(), "机器人异常请稍后重试", reqVO.getReceivedName());
                }
            } catch (ServiceException e) {
                if (USER_BENEFITS_USELESS_INSUFFICIENT.getCode().intValue() == e.getCode()) {
                    sendMsg(reqVO.getGroupRemark(), "令牌不足，请联系群管理员添加。", reqVO.getReceivedName());
                } else {
                    log.error("execute error:", e);
                    sendMsg(reqVO.getGroupRemark(), "应用执行异常，请联系管理员", reqVO.getReceivedName());
                }
            } catch (Exception e) {
                log.error("execute error:", e);
                sendMsg(reqVO.getGroupRemark(), "机器人异常请稍后重试!", reqVO.getReceivedName());
            }
        });
    }

    @Override
    public void sendMsg(String groupRemark, String msg, String endUser) {
        String robotId = RobotContextHolder.getRobotId();
        BaseReq<SendMessageReq> baseReq = new BaseReq<>();
        SendMessageReq sendMessageReq = new SendMessageReq();
        sendMessageReq.setTitleList(Collections.singletonList(groupRemark));
        sendMessageReq.setReceivedContent(msg);
        sendMessageReq.setAtList(Collections.singletonList(endUser));
        baseReq.setList(Collections.singletonList(sendMessageReq));
        BaseResponse<String> resp = workToolClient.sendMsg(robotId, baseReq);
        if (resp == null || resp.getCode() != 200) {
            log.error("发送群消息失败: {}", resp);
            throw new ServiceException(new ErrorCode(500, resp.getMessage()));
        }
    }


    private String userNameMd5(String receivedName) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = md5.digest(receivedName.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(Integer.toHexString((0x000000FF & aByte) | 0xFFFFFF00).substring(6));
        }
        return builder.toString();
    }


}
