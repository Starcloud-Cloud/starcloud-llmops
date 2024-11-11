package com.starcloud.ops.business.user.service.notify.adapter;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.controller.admin.message.vo.message.MpMessageSendReqVO;
import cn.iocoder.yudao.module.mp.dal.dataobject.message.MpMessageDO;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpMessageService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import cn.iocoder.yudao.module.system.controller.admin.notify.vo.template.NotifyTemplateCreateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.notify.NotifyMessageService;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyReqDTO;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyResultDTO;
import com.starcloud.ops.business.user.enums.notify.NotifyMediaEnum;
import com.starcloud.ops.business.user.service.MpAppManager;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_APP;

@Slf4j
@Service
public class MpNotifyMediaAdapterImpl implements NotifyMediaAdapter {

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private MpUserService mpUserService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private MpMessageService messageService;

    @Resource
    private NotifyMessageService notifyMessageService;

    @Override
    public NotifyMediaEnum supportType() {
        return NotifyMediaEnum.wx_mp;
    }

    @Override
    public SendNotifyResultDTO sendNotify(SendNotifyReqDTO reqDTO) {
        try {
            Optional<SocialUserDO> socialUserOptional = socialUserService.getSocialUserList(reqDTO.getUserId(), UserTypeEnum.ADMIN.getValue())
                    .stream().filter(socialUserDO -> SocialTypeEnum.WECHAT_MP.getType().equals(socialUserDO.getType()))
                    .findFirst();
            if (!socialUserOptional.isPresent()) {
                return SendNotifyResultDTO.error(null, "未绑定公共号");
            }

            String appId = MpAppManager.getMpAppId(TenantContextHolder.getTenantId());
            String openid = socialUserOptional.get().getOpenid();
            MpUserDO user = mpUserService.getUser(appId, openid);

            MpContextHolder.setAppId(appId);
            MpMessageSendReqVO messageSendReqVO = new MpMessageSendReqVO();

            messageSendReqVO.setContent(reqDTO.getContent());
            messageSendReqVO.setType(WxConsts.KefuMsgType.TEXT);
            messageSendReqVO.setUserId(user.getId());

            MpMessageDO mpMessageDO = messageService.sendKefuMessage(messageSendReqVO);
            return SendNotifyResultDTO.success(mpMessageDO.getId());
        } catch (Exception e) {
            log.warn("send mp error, userId = {}", reqDTO.getUserId(), e);
            return SendNotifyResultDTO.error(null, e.getMessage());
        }
    }

    @Override
    public void updateLog(Long logId, SendNotifyResultDTO resultDTO) {
        NotifyMessageDO notifyMessage = notifyMessageService.getNotifyMessage(logId);
        notifyMessage.setMpSuccess(resultDTO.getSuccess());
        notifyMessage.setMpLog(JSONUtil.toJsonStr(resultDTO));
        notifyMessage.setUpdateTime(LocalDateTime.now());
        notifyMessageService.updateById(notifyMessage);
    }
}
