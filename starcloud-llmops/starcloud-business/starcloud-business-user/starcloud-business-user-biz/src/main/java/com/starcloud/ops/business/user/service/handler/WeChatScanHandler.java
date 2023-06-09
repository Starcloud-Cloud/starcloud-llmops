package com.starcloud.ops.business.user.service.handler;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WeChatScanHandler implements WxMpMessageHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到微信扫描事件，内容：{}", wxMessage);
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());
        redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
        WxMpXmlOutTextMessage outTextMessage = WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("欢迎回到魔法AI").build();
        return outTextMessage;
    }
}
