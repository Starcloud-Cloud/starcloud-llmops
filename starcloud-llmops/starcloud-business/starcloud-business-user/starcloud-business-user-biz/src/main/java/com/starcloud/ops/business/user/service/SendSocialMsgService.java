package com.starcloud.ops.business.user.service;

import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;

public interface SendSocialMsgService {

    void sendInviteMsg(Long inviteUserid);

    /**
     * 发送注册信息
     *
     * @param mpUser
     * @param suffix
     */
    void asynSendWxRegisterMsg(MpUserDO mpUser);

}
