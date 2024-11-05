package com.starcloud.ops.business.open.config;

import cn.iocoder.yudao.module.mp.framework.mp.core.DefaultMpServiceFactory;
import cn.iocoder.yudao.module.mp.framework.mp.core.MpServiceFactory;
import cn.iocoder.yudao.module.mp.service.handler.menu.MenuHandler;
import cn.iocoder.yudao.module.mp.service.handler.message.MessageAutoReplyHandler;
import cn.iocoder.yudao.module.mp.service.handler.message.MessageReceiveHandler;
import cn.iocoder.yudao.module.mp.service.handler.other.KfSessionHandler;
import cn.iocoder.yudao.module.mp.service.handler.other.NullHandler;
import cn.iocoder.yudao.module.mp.service.handler.other.StoreCheckNotifyHandler;
import cn.iocoder.yudao.module.mp.service.handler.user.LocationHandler;
import cn.iocoder.yudao.module.mp.service.handler.user.UnsubscribeHandler;
import com.binarywang.spring.starter.wxjava.mp.properties.WxMpProperties;
import com.starcloud.ops.business.open.service.handler.*;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeChatConfiguration {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WeChatUnMatchHandler unMatchHandler;

    @Autowired
    private WeChatSubscribeHandler subscribeHandler;

    @Autowired
    private WeChatUnsubscribeHandler unsubscribeHandler;

    @Autowired
    private WeChatScanHandler scanHandler;

    //    @Bean("wxMpMessageRouter")
    public WxMpMessageRouter wxMpMessageRouter() {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);

        // 用户未关注时，进行关注后的事件推送
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SUBSCRIBE)
                .handler(subscribeHandler).end();

        // 取消关注
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.UNSUBSCRIBE)
                .handler(unsubscribeHandler).end();

        // 扫码事件 已关注时的事件推送
        router.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                .event(WxConsts.EventType.SCAN).handler(scanHandler).end();

        // 没有匹配的handler
        router.rule().async(true).handler(unMatchHandler).end();

        return router;
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public MpServiceFactory mpServiceFactory(RedisTemplateWxRedisOps redisTemplateWxRedisOps,
                                             WxMpProperties wxMpProperties,
                                             MessageReceiveHandler messageReceiveHandler,
                                             KfSessionHandler kfSessionHandler,
                                             StoreCheckNotifyHandler storeCheckNotifyHandler,
                                             MenuHandler menuHandler,
                                             NullHandler nullHandler,
                                             WeChatSubscribeHandler subscribeHandler,
                                             UnsubscribeHandler unsubscribeHandler,
                                             LocationHandler locationHandler,
                                             WeChatScanHandler scanHandler,
                                             WxTextMessageHandler wxTextMessageHandler,
                                             WeChatSpecialHandler weChatSpecialHandler,
                                             MessageAutoReplyHandler messageAutoReplyHandler) {
        return new DefaultMpServiceFactory(redisTemplateWxRedisOps, wxMpProperties,
                messageReceiveHandler, kfSessionHandler, storeCheckNotifyHandler, menuHandler,
                nullHandler, subscribeHandler, unsubscribeHandler, locationHandler, scanHandler, messageAutoReplyHandler, wxTextMessageHandler, weChatSpecialHandler);
    }

}
