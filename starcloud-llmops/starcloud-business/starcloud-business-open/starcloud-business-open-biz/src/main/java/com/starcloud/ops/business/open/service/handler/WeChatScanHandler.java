package com.starcloud.ops.business.open.service.handler;

import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpAutoReplyService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import com.starcloud.ops.business.open.service.WxMpChatService;
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

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WeChatScanHandler implements WxMpMessageHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MpAutoReplyService mpAutoReplyService;

    @Resource
    private WxMpChatService wxMpChatService;

    @Resource
    private MpUserService mpUserService;

    private List<String> openIds = Arrays.asList(
            "oyCo06RRyVQxlIAkR00Lxj4PNQXo","oyCo06X7q7kLyA-be8AAhJQqevbE","oyCo06aChvHe_ZhSXrVNRYnxWwRo"
    );

    private static final String MSG = "你好，我是魔法AI小助手，注意到您热心的邀请了朋友一起使用魔法AI，过程中是否有问题，可以加客服的微信，让我帮你一起解决。\n" +
            "https://mp.weixin.qq.com/s/kCCbpZOx-2PumVGdJt3i7Q";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到微信扫描事件，内容：{}", wxMessage);
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());
        mpUserService.saveUser(MpContextHolder.getAppId(), wxMpUser);
        redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
        WxMpXmlOutTextMessage outTextMessage = WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("欢迎回到魔法AI").build();
        try {
            if (openIds.contains(wxMpUser.getOpenId())) {
                String wxAppId = MpContextHolder.getAppId();
                MpUserDO user = mpUserService.getUser(wxAppId, wxMpUser.getOpenId());
                wxMpChatService.sendMsg(user.getId(),MSG);
            }
        } catch (Exception e) {
            log.info("发消息失败",e);
        }
        return outTextMessage;
    }
}
