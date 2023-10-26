package com.starcloud.ops.business.open.service.handler;

import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.open.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
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

import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_MSG;

@Component
@Slf4j
public class WeChatScanHandler implements WxMpMessageHandler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private MpUserService mpUserService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private WechatService wechatService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到微信扫描事件，内容：{}", wxMessage);
        if (!wechatService.isInternalAccount(MpContextHolder.getAppId())) {
            return null;
        }
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());
        mpUserService.saveUser(MpContextHolder.getAppId(), wxMpUser);
        redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);

        DictDataDO dictDataDO = dictDataService.parseDictData(WECHAT_MSG, "scan_Login");
        if (dictDataDO != null) {
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser())
                    .fromUser(wxMessage.getToUser()).content(dictDataDO.getValue()).build();
        }
        return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser())
                .fromUser(wxMessage.getToUser()).content("欢迎回到魔法AI").build();
    }
}
