package com.starcloud.ops.business.open.service.handler;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpAutoReplyService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import com.starcloud.ops.business.open.api.dto.WeChatRequestDTO;
import com.starcloud.ops.business.open.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WxTextMessageHandler implements WxMpMessageHandler {

    @Resource
    private MpUserService mpUserService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MpAutoReplyService mpAutoReplyService;

    @Resource
    private WechatService wechatService;

    @Override
    @DataPermission(enable = false)
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        log.info("[handle][接收到文本消息，内容：{}]", wxMessage);

        try {
            // 重复消费
            Boolean aBoolean = redisLock(wxMessage.getMsgId());
            if (!aBoolean) {
                return null;
            }

            // 同步存量用户
            WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());
            mpUserService.saveUser(MpContextHolder.getAppId(), wxMpUser);

            // 自动回复
            WxMpXmlOutMessage wxMpXmlOutMessage = mpAutoReplyService.replyForMessage(MpContextHolder.getAppId(), wxMessage);
            if (wxMpXmlOutMessage != null) {
                return wxMpXmlOutMessage;
            }

            // 上次对话结束
            if (!ready(wxMessage.getFromUser())) {
                log.info("上次对话未结束，{}", wxMessage.getFromUser());
                return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("AI正在思考中，请务重复提问！").build();
            }

            WeChatRequestDTO chatRequestDTO = new WeChatRequestDTO();
            chatRequestDTO.setQuery(wxMessage.getContent());
            chatRequestDTO.setFromUser(wxMessage.getFromUser());
            wechatService.asynReplyMsg(chatRequestDTO);
        } catch (Exception e) {
            log.error("wx chat error, from= {}, content= {}", wxMessage.getFromUser(), wxMessage.getContent(), e);
            redisTemplate.delete(wxMessage.getFromUser() + "-ready");
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("服务异常！请稍后重试或联系管理员").build();
        }
        return null;
    }

    private Boolean ready(String openId) {
        return redisTemplate.boundValueOps(openId + "-ready").setIfAbsent("lock", 120, TimeUnit.SECONDS);
    }

    private Boolean redisLock(Long msgId) {
        return redisTemplate.boundValueOps(msgId.toString()).setIfAbsent(msgId.toString(), 60, TimeUnit.SECONDS);
    }
}
