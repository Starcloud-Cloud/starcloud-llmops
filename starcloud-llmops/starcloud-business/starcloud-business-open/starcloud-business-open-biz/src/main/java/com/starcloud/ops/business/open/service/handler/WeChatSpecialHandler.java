package com.starcloud.ops.business.open.service.handler;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.mp.enums.click.WeChatClickTypeEnum;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static com.starcloud.ops.business.user.enums.DictTypeConstants.WECHAT_APP;

@Component
@Slf4j
public class WeChatSpecialHandler implements WxMpMessageHandler {
    @Resource
    private UserBenefitsService userBenefitsService;

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private DictDataService dictDataService;

    @Override
    @DataPermission(enable = false)
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到公共号指定菜单请求，内容：{}", wxMessage);
        AdminUserDO userDO = socialUserService.getSocialUser(wxMessage.getFromUser(), SocialTypeEnum.WECHAT_MP.getType(), UserTypeEnum.ADMIN.getValue());
        if (userDO == null) {
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content("用户不存在").build();
        }

        if (WeChatClickTypeEnum.SPECIAL_SIGN_IN.getCode().equalsIgnoreCase(wxMessage.getEventKey())) {
            // 签到
            Boolean success = userBenefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_ATTENDANCE.getName(), userDO.getId());
            String msg = success ? "签到新增权益成功" : "今日已签到";
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content(msg).build();
        }

        if (WeChatClickTypeEnum.SPECIAL_SHARE.getCode().equalsIgnoreCase(wxMessage.getEventKey())) {
            // 分享
            DictDataDO dictDataDO = dictDataService.parseDictData(WECHAT_APP, "invite_url");
            String msg = dictDataDO.getValue() + EncryptionUtils.encrypt(userDO.getId());
            return WxMpXmlOutMessage.TEXT().toUser(wxMessage.getFromUser()).fromUser(wxMessage.getToUser()).content(msg).build();
        }
        log.warn("未匹配到菜单类型: {}", wxMessage.getEventKey());
        return null;
    }

}
