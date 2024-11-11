package com.starcloud.ops.business.open.service.handler;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpAutoReplyService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import com.starcloud.ops.business.open.service.WechatService;
import com.starcloud.ops.business.open.service.manager.WechatUserManager;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class WeChatSubscribeHandler implements WxMpMessageHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MpUserService mpUserService;

    @Resource
    private SendSocialMsgService sendSocialMsgService;

    @Resource
    private MpAutoReplyService mpAutoReplyService;

    @Resource
    private WechatService wechatService;

    @Resource
    private WechatUserManager wechatUserManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        log.info("接收到微信关注事件，内容：{}", wxMessage);
        try {
            // 托管公共号不注册用户
            if (!wechatService.isInternalAccount(MpContextHolder.getAppId())) {
                return null;
            }

            WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());
            // 第二步，保存粉丝信息
            MpUserDO mpUserDO = mpUserService.saveUser(MpContextHolder.getAppId(), wxMpUser);

            if (wechatUserManager.socialExist(wxMpUser.getOpenId())) {
                log.info("已存在用户，直接登录");
                if (StringUtils.isNotBlank(wxMessage.getTicket())) {
                    redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
                }
                return mpAutoReplyService.replyForSubscribe(MpContextHolder.getAppId(), wxMessage);
            }
            wechatUserManager.createSocialUser(wxMpUser, wxMessage);
            if (StringUtils.isNotBlank(wxMessage.getTicket())) {
                redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
            }
            sendSocialMsgService.asynSendWxRegisterMsg(mpUserDO);
            return mpAutoReplyService.replyForSubscribe(MpContextHolder.getAppId(), wxMessage);
        } catch (Exception e) {
            log.error("新增用户失败", e);
            redisTemplate.boundValueOps(wxMessage.getTicket() + "_error").set(e.getMessage(), 1L, TimeUnit.MINUTES);
        }
        return null;
    }
}
