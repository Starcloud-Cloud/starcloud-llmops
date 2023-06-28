package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.convert.auth.AuthConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.logger.LoginLogService;
import cn.iocoder.yudao.module.system.service.oauth2.OAuth2TokenService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starcloud.ops.business.user.controller.admin.vo.QrCodeTicketVO;
import com.starcloud.ops.business.user.convert.QrCodeConvert;
import com.starcloud.ops.business.user.pojo.request.ScanLoginRequest;
import com.starcloud.ops.business.user.service.WeChatService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.CREATE_QR_ERROR;


@Service
@Slf4j
public class WeChatServiceImpl implements WeChatService {


    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AdminUserService userService;

    @Autowired
    private LoginLogService loginLogService;

    @Autowired
    private OAuth2TokenService oauth2TokenService;

    @Autowired
    private SocialUserBindMapper socialUserBindMapper;

    @Autowired
    private AdminUserMapper userMapper;

    @Autowired
    private SocialUserMapper socialUserMapper;


    @Override
    public QrCodeTicketVO qrCodeCreate() {
        try {
            WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket("login", 60 * 5);
            String url = wxMpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
            QrCodeTicketVO ticketVO = QrCodeConvert.INSTANCE.toVO(wxMpQrCodeTicket);
            ticketVO.setUrl(url);
            return ticketVO;
        } catch (WxErrorException e) {
            log.error("获取微信二维码异常",e);
            throw exception(CREATE_QR_ERROR);
        }
    }

    @Override
    public Long authUser(ScanLoginRequest request) {
        String error = redisTemplate.boundValueOps(request.getTicket() + "_error").get();
        if (StringUtils.isNotBlank(error)) {
            throw new ServiceException(500,error);
        }

        String openId = redisTemplate.boundValueOps(request.getTicket()).get();
        if (StringUtils.isBlank(openId)) {
            return null;
        }
        SocialUserDO socialUserDO = socialUserMapper.selectOne(new LambdaQueryWrapper<SocialUserDO>()
                .eq(SocialUserDO::getType, SocialTypeEnum.WECHAT_MP.getType())
                .eq(SocialUserDO::getOpenid, openId)
                .eq(SocialUserDO::getDeleted, 0)
        );
        if (socialUserDO == null) {
            return null;
        }

        SocialUserBindDO socialUserBindDO = socialUserBindMapper.selectByUserTypeAndSocialUserId(UserTypeEnum.ADMIN.getValue(), socialUserDO.getId());
        if (socialUserBindDO == null) {
            return null;
        }
        return socialUserBindDO.getUserId();
    }

    @Override
    public AuthLoginRespVO createTokenAfterLoginSuccess(Long userId) {
        AdminUserDO userDO = userMapper.selectById(userId);
        TenantContextHolder.setTenantId(userDO.getTenantId());
        createLoginLog(userId, userDO.getUsername(), LoginLogTypeEnum.LOGIN_SOCIAL, LoginResultEnum.SUCCESS);
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, UserTypeEnum.ADMIN.getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(logTypeEnum.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUsername(username);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserType(UserTypeEnum.ADMIN.getValue());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogService.createLoginLog(reqDTO);
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, ServletUtils.getClientIP());
        }
    }
}
