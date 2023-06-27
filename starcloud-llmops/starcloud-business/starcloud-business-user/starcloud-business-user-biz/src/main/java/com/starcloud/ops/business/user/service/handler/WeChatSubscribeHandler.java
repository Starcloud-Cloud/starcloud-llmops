package com.starcloud.ops.business.user.service.handler;

import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.mq.producer.permission.PermissionProducer;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starcloud.ops.business.user.service.StarUserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
        log.info("接收到微信关注事件，内容：{}", wxMessage);

        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMessage.getFromUser());

        SocialUserDO socialUserDO = socialUserMapper.selectOne(new LambdaQueryWrapper<SocialUserDO>()
                .eq(SocialUserDO::getType, SocialTypeEnum.WECHAT_MP.getType())
                .eq(SocialUserDO::getOpenid, wxMpUser.getOpenId())
                .eq(SocialUserDO::getDeleted, 0));

        if (socialUserDO != null) {
            //取消关注后重新关注 已有帐号
            redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
            return null;
        }

        socialUserDO = SocialUserDO.builder().code(wxMessage.getTicket())
                .nickname(wxMessage.getFromUser())
                .type(SocialTypeEnum.WECHAT_MP.getType())
                .openid(wxMpUser.getOpenId())
                .rawTokenInfo(JSON.toJSONString(wxMessage))
                .rawUserInfo(JSON.toJSONString(wxMpUser)).build();

        socialUserMapper.insert(socialUserDO);
        String password = RandomUtil.randomString(10);
        String username = userName(wxMessage.getFromUser());
        Long userId = starUserService.createNewUser(username, StringUtils.EMPTY, passwordEncoder.encode(password), 2L);
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
        redisTemplate.boundValueOps(wxMessage.getTicket()).set(wxMpUser.getOpenId(), 1L, TimeUnit.MINUTES);
        return null;
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
