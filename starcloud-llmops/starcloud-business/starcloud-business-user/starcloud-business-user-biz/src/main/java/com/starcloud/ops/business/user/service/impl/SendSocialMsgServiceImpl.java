package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.service.message.MpMessageService;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_MSG;


@Service
@Slf4j
public class SendSocialMsgServiceImpl implements SendSocialMsgService {

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private InvitationRecordsService invitationRecordsService;

    @Resource
    private MpMessageService messageService;


    @Override
    public void sendInviteMsg(Long inviteUserid) {
        List<SocialUserDO> socialUserList = socialUserService.getSocialUserList(inviteUserid, SocialTypeEnum.WECHAT_MP.getType());
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
        messageSendReqVO.setContent(dictDataDO.getValue());
        messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
        messageService.sendMessage(socialUserDO.getOpenid(), messageSendReqVO);
    }


}
