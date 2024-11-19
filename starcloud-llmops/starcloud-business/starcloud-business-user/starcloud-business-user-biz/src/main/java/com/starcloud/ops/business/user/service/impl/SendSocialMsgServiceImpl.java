package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpMessageService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteDO;
import com.starcloud.ops.business.user.service.MpAppManager;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_MSG;
import static com.starcloud.ops.business.user.enums.DictTypeConstants.WX_REGISTER_MSG;


@Service
@Slf4j
public class SendSocialMsgServiceImpl implements SendSocialMsgService, SendUserMsgService {

    private final static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 8,
            60, TimeUnit.MICROSECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r, "wx-msg-thread");
            thread.setDaemon(true);
            return thread;
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("The wx-msg-thread pool is full");
        }
    });

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private AdminUserInviteService adminUserInviteService;

    @Resource
    private MpMessageService messageService;

    @Resource
    private MpUserService mpUserService;


    @Override
    public void sendInviteMsg(Long inviteUserid) {
        List<SocialUserDO> socialUserList = socialUserService.getSocialUserList(inviteUserid, UserTypeEnum.ADMIN.getValue())
                .stream().filter(socialUserDO -> SocialTypeEnum.WECHAT_MP.getType().equals(socialUserDO.getType()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(socialUserList)) {
            return;
        }
        List<AdminUserInviteDO> invitationRecords = adminUserInviteService.getInvitationRecords(inviteUserid);

        if (invitationRecords.size() <= 3) {
            log.info("用户: {} 已邀请了{}个人", inviteUserid, invitationRecords.size());
            return;
        }
        log.info("邀请记录超过3个，发送推广信息");
        SocialUserDO socialUserDO = socialUserList.get(0);
        DictDataDO dictDataDO = dictDataService.parseDictData(WECHAT_MSG, "invite_reply");
        MpContextHolder.setAppId(MpAppManager.getMpAppId(TenantContextHolder.getTenantId()));
        MpMessageSendReqVO messageSendReqVO = new MpMessageSendReqVO();
        String format = String.format(dictDataDO.getValue(), invitationRecords.size());
        messageSendReqVO.setContent(format);
        messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
        messageSendReqVO.setUserId(inviteUserid);
        messageService.sendMessage(socialUserDO.getOpenid(), messageSendReqVO);
    }

    @Override
    public void asynSendWxRegisterMsg(MpUserDO mpUser) {
        log.info("发送微信注册消息,mpUserId:{}", mpUser.getId());
        threadPoolExecutor.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1L);
                sendWxRegisterMsg(mpUser);
            } catch (Exception e) {
                log.error("发送图片消息失败", e);
            }
        });

    }

    private void sendWxRegisterMsg(MpUserDO mpUser) {
        DictDataExportReqVO exportReqVO = new DictDataExportReqVO();
        exportReqVO.setDictType(WX_REGISTER_MSG);
        exportReqVO.setLabel("image");
        exportReqVO.setStatus(0);
        List<DictDataDO> dictDataList = dictDataService.getDictDataList(exportReqVO);
        dictDataList.forEach(dictDataDO -> {
            MpMessageSendReqVO reqVO = new MpMessageSendReqVO();
            reqVO.setUserId(mpUser.getId());
            reqVO.setType("image");
            reqVO.setMediaId(dictDataDO.getValue());
            messageService.sendKefuMessage(reqVO);
        });
    }

    @Override
    public void sendWxMsg(String appId, String openId, String content) {
        MpUserDO user = mpUserService.getUser(appId, openId);
        if (user == null) {
            throw exception(new ErrorCode(300005001, "未找到指定的用户,请重新关注此公共号或者重新扫描公共号二维码"));
        }
        MpMessageSendReqVO messageSendReqVO = new MpMessageSendReqVO();
        messageSendReqVO.setUserId(user.getId());
        messageSendReqVO.setContent(content);
        messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
        messageService.sendKefuMessage(messageSendReqVO);
    }

    @Override
    public void sendMsgToWx(Long userId, String content) {
        Optional<SocialUserDO> socialUserOptional = socialUserService.getSocialUserList(userId, UserTypeEnum.ADMIN.getValue())
                .stream().filter(socialUserDO -> SocialTypeEnum.WECHAT_MP.getType().equals(socialUserDO.getType()))
                .findFirst();
        if (!socialUserOptional.isPresent()) {
            log.warn("用户:{},未绑定公共号", userId);
            return;
        }
        SocialUserDO socialUserDO = socialUserOptional.get();
        String appId = MpAppManager.getMpAppId(TenantContextHolder.getTenantId());
        MpContextHolder.setAppId(appId);
        MpUserDO user = mpUserService.getUser(appId, socialUserDO.getOpenid());

        MpMessageSendReqVO messageSendReqVO = new MpMessageSendReqVO();
        messageSendReqVO.setContent(content);
        messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
        messageSendReqVO.setUserId(user.getId());
        try {

            messageService.sendKefuMessage(messageSendReqVO);
        } catch (Exception e) {
            log.warn("发送公共号消息失败, userId = {}, error = {}", userId, e.getMessage());
        }
    }
}
