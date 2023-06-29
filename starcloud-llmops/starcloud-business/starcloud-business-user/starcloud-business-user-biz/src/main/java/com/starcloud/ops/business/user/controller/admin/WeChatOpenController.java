package com.starcloud.ops.business.user.controller.admin;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.Objects;

@Tag(name = "星河云海 - 微信公众号回调")
@RestController
@RequestMapping("/llm/wechat/open")
public class WeChatOpenController {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WxMpMessageRouter wxMpMessageRouter;

    @Operation(summary = "校验签名")
    @GetMapping("/callback")
    @PermitAll
    @OperateLog(enable = false)
    public String test(@RequestParam(name = "signature", required = false) String signature,
                       @RequestParam(name = "timestamp", required = false) String timestamp,
                       @RequestParam(name = "nonce", required = false) String nonce,
                       @RequestParam(name = "echostr", required = false) String echostr) throws WxErrorException {
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "验证失败";
    }


    @PostMapping("/callback")
    @PermitAll
    @OperateLog(enable = false)
    public String token(@RequestBody String content,
                        @RequestParam(name = "signature", required = false) String signature,
                        @RequestParam(name = "timestamp", required = false) String timestamp,
                        @RequestParam(name = "nonce", required = false) String nonce,
                        @RequestParam(name = "encrypt_type", required = false) String type) throws Exception {

        WxMpXmlMessage inMessage = null;
        if (StrUtil.isBlank(type)) {
            // 明文模式
            inMessage = WxMpXmlMessage.fromXml(content);
        } else if (Objects.equals(type, "aes")) {
            // AES 加密模式
            inMessage = WxMpXmlMessage.fromEncryptedXml(content, wxMpService.getWxMpConfigStorage(),
                    timestamp, nonce, signature);
        }
        Assert.notNull(inMessage, "消息解析失败，原因：消息为空");

        WxMpXmlMessage wxMpXmlMessage = WxMpXmlMessage.fromXml(content);
        WxMpXmlOutMessage wxMpXmlOutMessage = wxMpMessageRouter.route(wxMpXmlMessage);

        if (wxMpXmlOutMessage == null) {
            return StringUtils.EMPTY;
        }
        if (StrUtil.isBlank(type)) {
            return wxMpXmlOutMessage.toXml();
        } else if (Objects.equals(type, "aes")) {
            return wxMpXmlOutMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
        }
        return StringUtils.EMPTY;
    }

}
