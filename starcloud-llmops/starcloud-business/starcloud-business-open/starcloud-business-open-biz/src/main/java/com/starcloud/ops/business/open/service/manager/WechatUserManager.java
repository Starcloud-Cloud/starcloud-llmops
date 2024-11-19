package com.starcloud.ops.business.open.service.manager;

import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.social.SocialUserService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.user.pojo.dto.UserDTO;
import com.starcloud.ops.business.user.service.user.StarUserService;
import com.starcloud.ops.business.user.service.user.handler.NewUserHandler;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static java.lang.Character.MAX_RADIX;

@Slf4j
@Component
public class WechatUserManager {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private StarUserService starUserService;

    @Resource
    @Lazy
    private AdminUserService userService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SocialUserService socialUserService;

    @Resource
    private List<NewUserHandler> newUserHandlers;


    public Boolean socialExist(String openId) {
        AdminUserDO socialUser = socialUserService.getSocialUser(openId, SocialTypeEnum.WECHAT_MP.getType(), UserTypeEnum.ADMIN.getValue());
        return socialUser != null;
    }

    public void createSocialUser(WxMpUser wxMpUser, WxMpXmlMessage wxMessage) {
        log.info("新增微信公共号用户 openId {}", wxMpUser.getOpenId());

        SocialUserDO socialUserDO = SocialUserDO.builder().code(wxMpUser.getOpenId())
                .nickname(wxMessage.getFromUser())
                .type(SocialTypeEnum.WECHAT_MP.getType())
                .openid(wxMpUser.getOpenId())
                .rawTokenInfo(JSON.toJSONString(wxMessage))
                .rawUserInfo(JSON.toJSONString(wxMpUser)).build();

        String password = "mofaai123456";
        String username = userName(wxMessage.getFromUser()) + TenantContextHolder.getTenantId();
        UserDTO userDTO = UserDTO.builder().username(username)
                .email(StringUtils.EMPTY)
                .password(passwordEncoder.encode(password))
                .tenantId(TenantContextHolder.getTenantId())
                // 父部门id与租户id保持一致 ！！！
                .parentDeptId(TenantContextHolder.getTenantId())
                .userStatus(CommonStatusEnum.ENABLE.getStatus()).build();
        Long userId = starUserService.createNewUser(userDTO);

        SocialUserBindDO socialUserBind = SocialUserBindDO.builder()
                .userId(userId).userType(UserTypeEnum.ADMIN.getValue())
                .socialType(socialUserDO.getType()).build();
        Long inviteUserid = 0L;
        try {
            UserContextHolder.setUserId(userId);
            socialUserService.bindWechatUser(socialUserDO, socialUserBind);
            String inviteCode = redisTemplate.boundValueOps(wxMessage.getTicket() + "_inviteCode").get();
            if (StringUtils.isNotBlank(inviteCode)) {
                inviteUserid = EncryptionUtils.decrypt(inviteCode);
            }

        } catch (Exception e) {
            log.warn("获取邀请用户失败，currentUser={}", userId, e);
        } finally {
            UserContextHolder.clear();
        }

        Long finalInviteUserid = inviteUserid;
        try {
            newUserHandlers.forEach(handler -> handler.afterUserRegister(userService.getUser(userId), finalInviteUserid == 0L ? null : userService.getUser(finalInviteUserid)));
        } catch (RuntimeException e) {
            log.error("新用户权益发放失败，失败原因{}", e.getMessage(), e);
        }
    }


    private String userName(String fromUser) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return fromUser.replaceAll("[^a-zA-Z0-9]", "").substring(0, 10);
        }
        byte[] hashBytes = md.digest(fromUser.getBytes());
        BigInteger hashInt = new BigInteger(1, hashBytes);
        String compressedString = hashInt.toString(MAX_RADIX);

        if (compressedString.length() <= 10) {
            return compressedString;
        }
        // 只取前10位
        return compressedString.substring(0, 10);

    }
}
