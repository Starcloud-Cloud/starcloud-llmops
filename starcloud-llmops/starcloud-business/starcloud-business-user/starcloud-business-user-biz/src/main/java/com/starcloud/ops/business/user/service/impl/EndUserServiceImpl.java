package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.service.user.MemberUserServiceImpl;
import cn.iocoder.yudao.module.system.api.logger.LoginLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.oauth2.OAuth2TokenApi;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;


/**
 * 使用若以的表和逻辑，实现一个简单的游客状态的记录功能
 * 1，根据游客访问渠道（网页，微信对话，钉钉等唯一标识)，直接生成一个会员 参考 MemberAuthServiceImpl
 * 2，生成令牌和登录记录
 */
@Service
@Slf4j
public class EndUserServiceImpl {

    @Resource
    private MemberUserMapper memberUserMapper;

    @Resource
    private MemberUserServiceImpl userService;

    @Resource
    private LoginLogApi loginLogApi;

    @Resource
    private OAuth2TokenApi oauth2TokenApi;


    public Boolean checkUser(String endUserCode) {
        // 用户已经存在
        MemberUserDO user = memberUserMapper.selectByMobile(endUserCode);
        return user != null;
    }

    /**
     * 网页登录
     * 1,js
     * 2,iframe
     * 3,单页面分析
     *
     * @param endUserCode
     * @return
     */
    public String webLogin(String endUserCode) {

        // 用户已经存在
        MemberUserDO user = memberUserMapper.selectByMobile(endUserCode);

        if (user == null) {
            user = createUser(endUserCode, AppSceneEnum.SHARE_WEB);
        } else {
            log.info("webLogin user: {}", user.getId());
        }

        return String.valueOf(user.getId());
    }


    /**
     * 微信账户登录 openId
     * 1，微信公共号
     *
     * @param openId
     * @return
     */
    public String weMpLogin(String openId) {

        // 用户已经存在
        MemberUserDO user = memberUserMapper.selectByMobile(openId);

        if (user == null) {
            user = createUser(openId, AppSceneEnum.MP);
        } else {
            log.info("weChatLogin user: {}", user.getId());
        }

        return String.valueOf(user.getId());
    }

    /**
     * 企业微信，微信用户名md5登录
     * 1，微信群聊天
     *
     * @param userCode
     * @return
     */
    public String weChatLogin(String userCode) {

        // 用户已经存在
        MemberUserDO user = memberUserMapper.selectByMobile(userCode);

        if (user == null) {
            user = createUser(userCode, AppSceneEnum.WECOM_GROUP);
        } else {
            log.info("weChatLogin user: {}", user.getId());
        }

        return String.valueOf(user.getId());
    }


    /**
     * 钉钉登录
     *
     * @param userCode
     * @return
     */
    public String ddLogin(String userCode) {

        // 用户已经存在
        MemberUserDO user = memberUserMapper.selectByMobile(userCode);

        if (user == null) {
            user = createUser(userCode, null);
        } else {
            log.info("ddLogin user: {}", user.getId());
        }

        return String.valueOf(user.getId());
    }

    /**
     * 抖音登录
     *
     * @param userCode
     * @return
     */
    public String dyLogin(String userCode) {

        // 用户已经存在
        MemberUserDO user = memberUserMapper.selectByMobile(userCode);

        if (user == null) {
            user = createUser(userCode, null);
        } else {
            log.info("dyLogin user: {}", user.getId());
        }

        return String.valueOf(user.getId());
    }


    @Transactional
    protected MemberUserDO createUser(String endUserCode, AppSceneEnum appScene) {

        // 校验验证码
        String userIp = getClientIP();

        // 用户不存在，则进行创建
        MemberUserDO user = userService.createUser(endUserCode, appScene.name(), userIp);
        // 插入登陆日志
        createLoginLog(user.getId(), endUserCode, 106, LoginResultEnum.SUCCESS);

        log.info("EndUserService webLogin user create and login: {}", endUserCode);

        return user;
    }

    private void createLoginLog(Long userId, String mobile, Integer logType, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logType);
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(getUserType().getValue());
        reqDTO.setUsername(StrUtil.subPre(mobile, 30));
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogApi.createLoginLog(reqDTO);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, getClientIP());
        }
    }

    private UserTypeEnum getUserType() {
        return UserTypeEnum.MEMBER;
    }
}
