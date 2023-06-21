package com.starcloud.ops.business.user.config;

import com.starcloud.ops.business.user.service.handler.WeChatUnMatchHandler;
import com.starcloud.ops.business.user.service.handler.WeChatScanHandler;
import com.starcloud.ops.business.user.service.handler.WeChatSubscribeHandler;
import com.starcloud.ops.business.user.service.handler.WeChatUnsubscribeHandler;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Bean("wxMpMessageRouter")
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
}
