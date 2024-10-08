package com.starcloud.ops.business.open.service.handler;

import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class WeChatUnsubscribeHandler implements WxMpMessageHandler {

    @Autowired
    private SocialUserMapper socialUserMapper;

    @Autowired
    private SocialUserBindMapper socialUserBindMapper;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到微信取消关注事件，内容：{}", wxMessage);
//        取消关注解绑
//        SocialUserDO socialUserDO = socialUserMapper.selectOne(new LambdaQueryWrapper<SocialUserDO>()
//                .eq(SocialUserDO::getType, SocialTypeEnum.WECHAT_MP.getType())
//                .eq(SocialUserDO::getOpenid, wxMessage.getFromUser())
//                .eq(SocialUserDO::getDeleted, 0)
//        );
//        if (socialUserDO == null) {
//            return null;
//        }
//        socialUserMapper.deleteById(socialUserDO);
//        socialUserBindMapper.deleteByUserTypeAndSocialUserId(UserTypeEnum.ADMIN.getValue(), socialUserDO.getId());
        return null;
    }
}
