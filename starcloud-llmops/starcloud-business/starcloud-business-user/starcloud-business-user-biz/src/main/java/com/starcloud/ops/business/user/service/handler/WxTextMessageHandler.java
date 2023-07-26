package com.starcloud.ops.business.user.service.handler;

import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.dal.dataobject.user.MpUserDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
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

    private static final String REQUEST_ATTRIBUTE_LOGIN_USER_ID = "login_user_id";

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

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) {
        try {
            Boolean aBoolean = redisLock(wxMessage.getMsgId());
            if (!aBoolean) {
                return null;
            }
            Pattern pattern = Pattern.compile(regex);
            // 设置用户上下文
            String openId = wxMessage.getFromUser();
            AdminUserDO userDO = socialUserService.getSocialUser(openId, SocialTypeEnum.WECHAT_MP.getType(), UserTypeEnum.ADMIN.getValue());
            TenantContextHolder.setTenantId(userDO.getTenantId());
            TenantContextHolder.setIgnore(false);
            UserContextHolder.setUserId(userDO.getId());

            String content = wxMessage.getContent();
            String wxAppId = MpContextHolder.getAppId();
            MpUserDO user = mpUserService.getUser(wxAppId, openId);
            if (pattern.matcher(content).matches()) {
                // 解析url 新建聊天应用
                // wxMpChatService.parseUrl(content, user.getId());
                return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("暂不支持url解析").build();
            } else {
                // 普通聊天
                String appUid = wxMpChatService.getRecentlyChatApp();
                LogAppConversationDO recentlyConversation = logAppConversationService.getRecentlyConversation(appUid);
                ChatRequestVO chatRequestVO = new ChatRequestVO();
                chatRequestVO.setAppUid(appUid);
                chatRequestVO.setQuery(content);
                chatRequestVO.setScene(AppSceneEnum.WEB_MARKET.name());
                chatRequestVO.setUserId(userDO.getId());
                if (recentlyConversation != null) {
                    chatRequestVO.setConversationUid(recentlyConversation.getUid());
                }
                wxMpChatService.chatAndReply(chatRequestVO, user.getId());
            }
        } catch (Exception e) {
            log.error("wx chat error, from= {}, content= {}", wxMessage.getFromUser(), wxMessage.getContent(), e);
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("服务异常!请稍后重试或联系管理员").build();
        }
        return null;
    }


    private Boolean redisLock(Long msgId) {
        return redisTemplate.boundValueOps(msgId.toString()).setIfAbsent(msgId.toString(), 60, TimeUnit.SECONDS);
    }
}
