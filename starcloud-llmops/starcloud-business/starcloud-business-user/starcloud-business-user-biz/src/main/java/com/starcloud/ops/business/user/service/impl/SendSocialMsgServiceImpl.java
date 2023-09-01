package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.message.MpAutoReplyDO;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.enums.message.MpAutoReplyTypeEnum;
import cn.iocoder.yudao.module.mp.service.message.MpAutoReplyServiceImpl;
import cn.iocoder.yudao.module.mp.service.message.MpMessageService;
import cn.iocoder.yudao.module.system.controller.admin.dict.vo.data.DictDataExportReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.user.dal.dataObject.InvitationRecordsDO;
import com.starcloud.ops.business.user.service.InvitationRecordsService;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.*;

import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_MSG;
import static com.starcloud.ops.business.user.enums.DictTypeConstants.WX_REGISTER_MSG;


@Service
@Slf4j
public class SendSocialMsgServiceImpl implements SendSocialMsgService {

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
    private InvitationRecordsService invitationRecordsService;

    @Resource
    private MpAutoReplyServiceImpl mpAutoReplyService;

    @Resource
    private MpMessageService messageService;


    @Override
    public void sendInviteMsg(Long inviteUserid) {
        List<SocialUserDO> socialUserList = socialUserService.getSocialUserList(inviteUserid, UserTypeEnum.ADMIN.getValue());
        if (CollectionUtils.isEmpty(socialUserList)) {
            return;
        }
        List<InvitationRecordsDO> invitationRecords = invitationRecordsService.getInvitationRecords(inviteUserid);

        if (invitationRecords.size() <= 3) {
            log.info("用户: {} 已邀请了{}个人", inviteUserid, invitationRecords.size());
            return;
        }
        log.info("邀请记录超过3个，发送推广信息");
        SocialUserDO socialUserDO = socialUserList.get(0);
        DictDataDO dictDataDO = dictDataService.parseDictData(WECHAT_MSG, "invite_reply");
        MpMessageSendReqVO messageSendReqVO = new MpMessageSendReqVO();
        String format = String.format(dictDataDO.getValue(), invitationRecords.size());
        messageSendReqVO.setContent(format);
        messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
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

}
