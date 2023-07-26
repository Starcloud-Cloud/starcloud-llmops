package com.starcloud.ops.business.user.service.handler;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.mp.framework.mp.core.context.MpContextHolder;
import cn.iocoder.yudao.module.mp.service.message.MpAutoReplyService;
import cn.iocoder.yudao.module.mp.service.user.MpUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.mq.producer.permission.PermissionProducer;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starcloud.ops.business.user.service.StarUserService;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Character.MAX_RADIX;

@Component
@Slf4j
public class WeChatSubscribeHandler implements WxMpMessageHandler {

    @Autowired
    private SocialUserMapper socialUserMapper;

    @Autowired
    private SocialUserBindMapper socialUserBindMapper;

    @Autowired
    private StarUserService starUserService;

    @Autowired
    private PermissionProducer permissionProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private MpUserService mpUserService;

    @Resource
    private MpAutoReplyService mpAutoReplyService;

    @Value("${starcloud-llm.tenant.id:2}")
    private Long tenantId;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到微信关注事件，内容：{}", wxMessage);
        try {
            WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());
            // 第二步，保存粉丝信息
            mpUserService.saveUser(MpContextHolder.getAppId(), wxMpUser);

            SocialUserDO socialUserDO = socialUserMapper.selectOne(new LambdaQueryWrapper<SocialUserDO>()
                    .eq(SocialUserDO::getType, SocialTypeEnum.WECHAT_MP.getType())
                    .eq(SocialUserDO::getOpenid, wxMpUser.getOpenId())
                    .eq(SocialUserDO::getDeleted, 0));

            if (socialUserDO != null) {
                //取消关注后重新关注 已有帐号
                log.info("已存在用户，直接登录");
                if (StringUtils.isNotBlank(wxMessage.getTicket())) {
                    redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
                }
                return mpAutoReplyService.replyForSubscribe(MpContextHolder.getAppId(), wxMessage);
            }

            socialUserDO = SocialUserDO.builder().code(wxMpUser.getOpenId())
                    .nickname(wxMessage.getFromUser())
                    .type(SocialTypeEnum.WECHAT_MP.getType())
                    .openid(wxMpUser.getOpenId())
                    .rawTokenInfo(JSON.toJSONString(wxMessage))
                    .rawUserInfo(JSON.toJSONString(wxMpUser)).build();

            socialUserMapper.insert(socialUserDO);
//            String password = RandomUtil.randomString(10);
            String password = "mofaai123456";
            String username = userName(wxMessage.getFromUser());
            Long userId = starUserService.createNewUser(username, StringUtils.EMPTY, passwordEncoder.encode(password), 2L, CommonStatusEnum.ENABLE.getStatus());
            SocialUserBindDO socialUserBind = SocialUserBindDO.builder()
                    .userId(userId).userType(UserTypeEnum.ADMIN.getValue())
                    .socialUserId(socialUserDO.getId()).socialType(socialUserDO.getType()).build();
            socialUserBindMapper.insert(socialUserBind);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    permissionProducer.sendUserRoleRefreshMessage();
                }

            });
            if (StringUtils.isNotBlank(wxMessage.getTicket())) {
                redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
            }
            Long inviteUserid = null;
            try {
                String inviteCode = redisTemplate.boundValueOps(wxMessage.getTicket() + "_inviteCode").get();
                if (StringUtils.isNotBlank(inviteCode)) {
                    inviteUserid = EncryptionUtils.decrypt(inviteCode);
                }
            } catch (Exception e) {
                log.warn("获取邀请用户失败，currentUser={}", userId, e);
            }

            starUserService.addBenefits(userId, inviteUserid);

            String msg = String.format("您可以使用帐号密码登录，帐号是：%s  登录密码是：%s", username, password);
            return  mpAutoReplyService.replyForSubscribe(MpContextHolder.getAppId(),msg, wxMessage);
        } catch (Exception e) {
            log.error("新增用户失败", e);
            redisTemplate.boundValueOps(wxMessage.getTicket() + "_error").set(e.getMessage(), 1L, TimeUnit.MINUTES);
        }
        return null;
    }

    private Long existUserId(String openId) {
        SocialUserDO socialUserDO = socialUserMapper.selectDeleteDO(openId, SocialTypeEnum.WECHAT_MP.getType());
        if (socialUserDO == null) {
            return null;
        }

        SocialUserBindDO socialUserBindDO = socialUserBindMapper.selectDeleteDO(socialUserDO.getId(), UserTypeEnum.ADMIN.getValue());
        if (socialUserBindDO == null) {
            return null;
        }
        return socialUserBindDO.getUserId();
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
