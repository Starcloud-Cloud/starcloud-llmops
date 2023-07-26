package com.starcloud.ops.business.user.service.handler;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.dal.dataobject.message.MpAutoReplyDO;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpAutoReplyService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.chat.service.WxMpChatService;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WxTextMessageHandler implements WxMpMessageHandler {
    private static final String regex = "^(https?)://([A-Za-z0-9.-]+)(:[0-9]+)?(/[A-Za-z0-9.-]*)*(\\?[A-Za-z0-9-._%&+=]*)?(#[A-Za-z0-9-]*)?$";

    private static final String PREFIX = "#openai-chat";

    @Resource
    private WxMpChatService wxMpChatService;

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private MpUserService mpUserService;

    @Resource
    private LogAppConversationService logAppConversationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MpAutoReplyService mpAutoReplyService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        try {
            Boolean aBoolean = redisLock(wxMessage.getMsgId());
            if (!aBoolean) {
                return null;
            }
            String wxAppId = MpContextHolder.getAppId();
            MpAutoReplyDO mpAutoReplyDO = mpAutoReplyService.selectListByAppIdAndMessage(wxAppId, wxMessage.getMsgType());
            if (mpAutoReplyDO == null || mpAutoReplyDO.getResponseContent() == null
                    || !mpAutoReplyDO.getResponseContent().startsWith(PREFIX)) {
                return mpAutoReplyService.replyForMessage(MpContextHolder.getAppId(), wxMessage);
            }
            String prompt = mpAutoReplyDO.getResponseContent().substring(PREFIX.length());
            //限流
            if (!limiter(wxMessage.getFromUser())) {
                return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("超过限制! 60秒后重试").build();
            }

            // 上次对话结束
            if (!ready(wxMessage.getFromUser())) {
                log.info("上次对话未结束，{}", wxMessage.getFromUser());
                return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("请求频率过快，请稍后重试").build();
            }


            Pattern pattern = Pattern.compile(regex);
            // 设置用户上下文
            String openId = wxMessage.getFromUser();
            AdminUserDO userDO = socialUserService.getSocialUser(openId, SocialTypeEnum.WECHAT_MP.getType(), UserTypeEnum.ADMIN.getValue());
            TenantContextHolder.setTenantId(userDO.getTenantId());
            TenantContextHolder.setIgnore(false);
            UserContextHolder.setUserId(userDO.getId());

            String content = wxMessage.getContent();
            MpUserDO user = mpUserService.getUser(wxAppId, openId);
            if (pattern.matcher(content).matches()) {
                // 解析url 新建聊天应用
                // wxMpChatService.parseUrl(content, user.getId());
                return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("暂不支持url解析").build();
            } else {
                // 普通聊天
                String appUid = wxMpChatService.getRecentlyChatApp(prompt);
                LogAppConversationDO recentlyConversation = logAppConversationService.getRecentlyConversation(appUid);
                ChatRequestVO chatRequestVO = new ChatRequestVO();
                chatRequestVO.setAppUid(appUid);
                chatRequestVO.setQuery(content);
                chatRequestVO.setScene(AppSceneEnum.WEB_MARKET.name());
                chatRequestVO.setUserId(userDO.getId());
                if (recentlyConversation != null) {
                    chatRequestVO.setConversationUid(recentlyConversation.getUid());
                }
                wxMpChatService.chatAndReply(chatRequestVO, user.getId(), wxMessage.getFromUser());
            }
        } catch (Exception e) {
            log.error("wx chat error, from= {}, content= {}", wxMessage.getFromUser(), wxMessage.getContent(), e);
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("服务异常！请稍后重试或联系管理员").build();
        }
        return null;
    }

    private Boolean ready(String openId) {
        return redisTemplate.boundValueOps(openId + "-ready").setIfAbsent("lock", 60, TimeUnit.SECONDS);
    }

    private Boolean limiter(String openId) {
        Long currentTime = System.currentTimeMillis();

        if (Boolean.TRUE.equals(redisTemplate.hasKey(openId))) {
            // intervalTime是限流的时间
            Long intervalTime = 60000L;
            Integer count = redisTemplate.opsForZSet().rangeByScore(openId, currentTime - intervalTime, currentTime).size();
            if (count != null && count >= 5) {
                log.info("请求超过每分钟5次 {}", openId);
                return false;
            }
        }
        redisTemplate.opsForZSet().add(openId, IdUtil.fastSimpleUUID(), currentTime);
        return true;
    }

    private Boolean redisLock(Long msgId) {
        return redisTemplate.boundValueOps(msgId.toString()).setIfAbsent(msgId.toString(), 60, TimeUnit.SECONDS);
    }
}
