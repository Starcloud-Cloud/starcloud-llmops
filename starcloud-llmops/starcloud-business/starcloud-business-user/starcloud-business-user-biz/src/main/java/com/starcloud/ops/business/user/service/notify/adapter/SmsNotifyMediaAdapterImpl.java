package com.starcloud.ops.business.user.service.notify.adapter;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.member.MemberService;
import cn.iocoder.yudao.module.system.service.notify.NotifyMessageService;
import cn.iocoder.yudao.module.system.service.sms.SmsSendService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyReqDTO;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyResultDTO;
import com.starcloud.ops.business.user.enums.notify.NotifyMediaEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
//@Service
public class SmsNotifyMediaAdapterImpl implements NotifyMediaAdapter {

    @Resource
    private SmsSendService smsSendService;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private MemberService memberService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private NotifyMessageService notifyMessageService;

    @Override
    public NotifyMediaEnum supportType() {
//        return NotifyMediaEnum.sms;
        return null;
    }

    @Override
    public SendNotifyResultDTO sendNotify(SendNotifyReqDTO sendNotifyReqDTO) {
        try {
            String mobile = getMobile(sendNotifyReqDTO.getUserId(), sendNotifyReqDTO.getUserType());
            if (StringUtils.isBlank(mobile)) {
                return SendNotifyResultDTO.error(null, "用户没有绑定手机号");
            }
            //   todo templateId
            String apiTempLateId = dictDataService.parseDictData("","").getValue();
            Long logId = smsSendService.syncSendSingleSms(mobile, sendNotifyReqDTO.getUserId(), sendNotifyReqDTO.getUserType().getValue(), sendNotifyReqDTO.getContent(),apiTempLateId);
            return SendNotifyResultDTO.success(logId);
        } catch (Exception e) {
            log.warn("send sms error, userId = {}", sendNotifyReqDTO.getUserId(), e);
            return SendNotifyResultDTO.error(null, e.getMessage());
        }
    }

    private String getMobile(Long userId, UserTypeEnum userType) {
        String mobile = null;
        switch (userType) {
            case ADMIN:
                AdminUserDO user = adminUserService.getUser(userId);
                mobile = user.getMobile();
                break;
            case MEMBER:
                mobile = memberService.getMemberUserMobile(userId);
                break;
        }
        return mobile;
    }

    @Override
    public void updateLog(Long logId, SendNotifyResultDTO resultDTO) {
        NotifyMessageDO notifyMessage = notifyMessageService.getNotifyMessage(logId);
        notifyMessage.setSmsSuccess(resultDTO.getSuccess());
        notifyMessage.setSmsLog(JSONUtil.toJsonStr(resultDTO));
        notifyMessage.setUpdateTime(LocalDateTime.now());
        notifyMessageService.updateById(notifyMessage);
    }
}
