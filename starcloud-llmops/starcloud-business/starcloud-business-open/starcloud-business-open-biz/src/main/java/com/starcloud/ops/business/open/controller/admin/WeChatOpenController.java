package com.starcloud.ops.business.open.controller.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.mp.dal.dataobject.account.MpAccountDO;
import cn.iocoder.yudao.module.mp.framework.mp.core.MpServiceFactory;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.account.MpAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.Objects;

@Tag(name = "星河云海 - 微信公众号回调")
@RestController
@RequestMapping("/llm/wechat/open")
@Slf4j
public class WeChatOpenController {

    @Autowired
    private MpAccountService mpAccountService;

    @Autowired
    private MpServiceFactory mpServiceFactory;

    @Operation(summary = "校验签名")
    @GetMapping("/callback/{appId}")
    @PermitAll
    @OperateLog(enable = false)
    public String checkSignature(@PathVariable("appId") String appId,
                       @RequestParam(name = "signature", required = false) String signature,
                       @RequestParam(name = "timestamp", required = false) String timestamp,
                       @RequestParam(name = "nonce", required = false) String nonce,
                       @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("[checkSignature][appId({})]", appId);
        // 校验请求签名
        WxMpService wxMpService = mpServiceFactory.getRequiredMpService(appId);
        // 校验通过
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        // 校验不通过
        return "非法请求";
    }

    @PostMapping("/callback/{appId}")
    @PermitAll
    @OperateLog(enable = false)
    public String token(@RequestBody String content,
                        @PathVariable("appId") String appId,
                        @RequestParam(name = "signature", required = false) String signature,
                        @RequestParam(name = "timestamp", required = false) String timestamp,
                        @RequestParam(name = "nonce", required = false) String nonce,
                        @RequestParam(name = "encrypt_type", required = false) String type) {
        log.info("[mp callback][appId({})]", appId);
        MpAccountDO account = mpAccountService.getAccountFromCache(appId);
        WxMpXmlMessage wxMpXmlMessage = WxMpXmlMessage.fromXml(content);
        if (account == null) {
            return WxMpXmlOutMessage.TEXT().toUser(wxMpXmlMessage.getFromUser()).fromUser(wxMpXmlMessage.getToUser()).content("公众号不存在，请联系管理员重新绑定").build().toXml();
        }

        try {
            WxMpService mppService = mpServiceFactory.getRequiredMpService(appId);
            Assert.isTrue(mppService.checkSignature(timestamp, nonce, signature),
                    "非法请求");

            MpContextHolder.setAppId(appId);

            TenantContextHolder.setTenantId(account.getTenantId());
            TenantContextHolder.setIgnore(false);
            WxMpXmlMessage inMessage = null;
            if (StrUtil.isBlank(type)) {
                // 明文模式
                inMessage = WxMpXmlMessage.fromXml(content);
            } else if (Objects.equals(type, "aes")) {
                // AES 加密模式
                inMessage = WxMpXmlMessage.fromEncryptedXml(content, mppService.getWxMpConfigStorage(),
                        timestamp, nonce, signature);
            }
            Assert.notNull(inMessage, "消息解析失败，原因：消息为空");


            WxMpMessageRouter mpMessageRouter = mpServiceFactory.getRequiredMpMessageRouter(appId);
            WxMpXmlOutMessage wxMpXmlOutMessage = mpMessageRouter.route(wxMpXmlMessage);

            if (wxMpXmlOutMessage == null) {
                return StringUtils.EMPTY;
            }
            if (StrUtil.isBlank(type)) {
                return wxMpXmlOutMessage.toXml();
            } else if (Objects.equals(type, "aes")) {
                return wxMpXmlOutMessage.toEncryptedXml(mppService.getWxMpConfigStorage());
            }
            return StringUtils.EMPTY;
        } finally {
            MpContextHolder.clear();
        }
    }

}
