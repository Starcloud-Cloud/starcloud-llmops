package cn.iocoder.yudao.module.system.service.social;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.controller.admin.socail.vo.user.SocialUserPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserBindDO;
import cn.iocoder.yudao.module.system.dal.dataobject.social.SocialUserDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserBindMapper;
import cn.iocoder.yudao.module.system.dal.mysql.social.SocialUserMapper;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.xingyuv.jushauth.model.AuthToken;
import com.xingyuv.jushauth.model.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 社交用户 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class SocialUserServiceImpl implements SocialUserService {

    @Resource
    private SocialUserBindMapper socialUserBindMapper;
    @Resource
    private SocialUserMapper socialUserMapper;

    @Resource
    private SocialClientService socialClientService;

    @Resource
    private AdminUserService adminUserService;

    @Override
    public List<SocialUserDO> getSocialUserList(Long userId, Integer userType) {
        // 获得绑定
        List<SocialUserBindDO> socialUserBinds = socialUserBindMapper.selectListByUserIdAndUserType(userId, userType);
        if (CollUtil.isEmpty(socialUserBinds)) {
            return Collections.emptyList();
        }
        // 获得社交用户
        return socialUserMapper.selectBatchIds(convertSet(socialUserBinds, SocialUserBindDO::getSocialUserId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bindSocialUser(SocialUserBindReqDTO reqDTO) {
        SocialUserDO socialUser;
        if (reqDTO.getAuto()) {
            // 获得社交用户
            socialUser = authSocialUser(reqDTO.getSocialType(), reqDTO.getUserType(), reqDTO.getCode(), reqDTO.getState());
            Assert.notNull(socialUser, "社交用户不能为空");
        } else {
            socialUser = manualSocialUser(reqDTO.getSocialType(), reqDTO.getCode(), reqDTO.getRemark());
        }


        // 社交用户可能之前绑定过别的用户，需要进行解绑
        socialUserBindMapper.deleteByUserTypeAndSocialUserId(reqDTO.getUserType(), socialUser.getId());

        // 用户可能之前已经绑定过该社交类型，需要进行解绑
        if (!SocialTypeEnum.COZE.getType().equals(reqDTO.getSocialType())) {
            socialUserBindMapper.deleteByUserTypeAndUserIdAndSocialType(reqDTO.getUserType(), reqDTO.getUserId(), socialUser.getType());
        }

        // 绑定当前登录的社交用户
        SocialUserBindDO socialUserBind = SocialUserBindDO.builder().userId(reqDTO.getUserId()).userType(reqDTO.getUserType()).socialUserId(socialUser.getId()).socialType(socialUser.getType()).build();
        socialUserBindMapper.insert(socialUserBind);
        return socialUser.getOpenid();
    }


    @Override
    public void unbindSocialUser(Long userId, Integer userType, Integer socialType, String openid) {
        // 获得 openid 对应的 SocialUserDO 社交用户
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, openid);
        if (socialUser == null) {
            throw exception(SOCIAL_USER_NOT_FOUND);
        }
        socialUserMapper.deleteById(socialUser.getId());
        // 删除对应的社交绑定关系

        socialUserBindMapper.deleteByUserTypeAndSocialUserId(userType, socialUser.getId());
    }

    @Override
    public AdminUserDO getSocialUser(String openId, Integer socialUserType, Integer userType) {
        SocialUserDO socialUserDO = socialUserMapper.selectByTypeAndOpenid(socialUserType, openId);
        if (socialUserDO == null) {
            return null;
        }
        SocialUserBindDO socialUserBindDO = socialUserBindMapper.selectByUserTypeAndSocialUserId(userType, socialUserDO.getId());
        if (socialUserBindDO == null) {
            return null;
        }
        AdminUserDO user = adminUserService.getUser(socialUserBindDO.getUserId());
        return user;
    }

    @Override
    public SocialUserRespDTO getSocialUser(Integer userType, Integer socialType, String code, String state) {
        // 获得社交用户
        SocialUserDO socialUser = authSocialUser(socialType, userType, code, state);
        Assert.notNull(socialUser, "社交用户不能为空");

        // 如果未绑定的社交用户，则无法自动登录，进行报错
        SocialUserBindDO socialUserBind = socialUserBindMapper.selectByUserTypeAndSocialUserId(userType, socialUser.getId());
        if (socialUserBind == null) {
            throw exception(AUTH_THIRD_LOGIN_NOT_BIND);
        }
        return new SocialUserRespDTO(socialUser.getOpenid(), socialUserBind.getUserId());
    }

    // TODO 芋艿：调整下单测

    /**
     * 授权获得对应的社交用户
     * 如果授权失败，则会抛出 {@link ServiceException} 异常
     *
     * @param socialType 社交平台的类型 {@link SocialTypeEnum}
     * @param userType   用户类型
     * @param code       授权码
     * @param state      state
     * @return 授权用户
     */
    @NotNull
    public SocialUserDO authSocialUser(Integer socialType, Integer userType, String code, String state) {
        // 优先从 DB 中获取，因为 code 有且可以使用一次。
        // 在社交登录时，当未绑定 User 时，需要绑定登录，此时需要 code 使用两次
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndCodeAnState(socialType, code, state);
        if (socialUser != null) {
            return socialUser;
        }

        // 请求获取
        AuthUser authUser = socialClientService.getAuthUser(socialType, userType, code, state);
        Assert.notNull(authUser, "三方用户不能为空");

        // 保存到 DB 中
        socialUser = socialUserMapper.selectByTypeAndOpenid(socialType, authUser.getUuid());
        if (socialUser == null) {
            socialUser = new SocialUserDO();
        }
        socialUser.setType(socialType).setCode(code).setState(state) // 需要保存 code + state 字段，保证后续可查询
                .setOpenid(authUser.getUuid()).setToken(authUser.getToken().getAccessToken()).setRawTokenInfo((toJsonString(authUser.getToken()))).setNickname(authUser.getNickname()).setAvatar(authUser.getAvatar()).setRawUserInfo(toJsonString(authUser.getRawUserInfo())).setExpireIn(authUser.getToken().getExpireIn() > 0 ? authUser.getToken().getExpireIn() : -1).setRefreshTokenExpireIn(Objects.nonNull(authUser.getToken().getRefreshToken()) ? authUser.getToken().getRefreshTokenExpireIn() : -1).setRefreshToken(Objects.nonNull(authUser.getToken().getRefreshToken()) ? authUser.getToken().getRefreshToken() : null);
        if (socialUser.getId() == null) {
            socialUserMapper.insert(socialUser);
        } else {
            socialUserMapper.updateById(socialUser);
        }
        return socialUser;
    }

    @NotNull
    public SocialUserDO manualSocialUser(Integer socialType, String code, String remark) {
        // 优先从 DB 中获取，因为 code 有且可以使用一次。
        // 在社交登录时，当未绑定 User 时，需要绑定登录，此时需要 code 使用两次
        SocialUserDO socialUser = socialUserMapper.selectByTypeAndCodeAnState(socialType, code, null);
        if (socialUser != null) {
            return socialUser;
        }
        // 保存到 DB 中
        socialUser = new SocialUserDO();
        String openid = IdUtil.fastUUID();
        socialUser.setType(socialType)
                .setCode(code)
                .setState(null) // 需要保存 code + state 字段，保证后续可查询
                .setOpenid(openid)
                .setToken(code)
                .setAuto(false)
                .setRawTokenInfo(null)
                .setNickname(remark).setAvatar(null)
                .setRemark(remark)
                .setRawUserInfo(null).setExpireIn(-1).setRefreshTokenExpireIn(-1).setRefreshToken(null);
        if (socialUser.getId() == null) {
            socialUserMapper.insert(socialUser);
        } else {
            socialUserMapper.updateById(socialUser);
        }
        return socialUser;
    }

    // ==================== 社交用户 CRUD ====================

    @Override
    public SocialUserDO getSocialUser(Long id) {
        return socialUserMapper.selectById(id);
    }

    @Override
    public SocialUserDO getNewSocialUser(Long id) {
        SocialUserDO socialUser = socialUserMapper.selectById(id);

        if (socialUser == null) {
            throw exception(SOCIAL_USER_AUTH_NO_FOUND);
        }

        if (validatedTokenExpireIn(socialUser)) {
            return socialUser;
        }

        if (Objects.nonNull(socialUser.getAuto()) && !socialUser.getAuto()) {
            return socialUser;
        }

        validatedRefreshTokenExpireIn(socialUser);

        AuthToken authToken = socialClientService.refreshToken(socialUser.getType(), UserTypeEnum.ADMIN.getValue(), socialUser.getRefreshToken());

        LocalDateTime thirtyDaysLater = LocalDateTimeUtil.now().plusDays(30);
        // 指定时区（东八区）
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime zonedDateTime = thirtyDaysLater.atZone(zoneId);

        authToken.setRefreshTokenExpireIn((int) zonedDateTime.toEpochSecond());

        socialUserMapper.updateById(socialUser.setToken(authToken.getAccessToken()).setRawTokenInfo((toJsonString(authToken))).setExpireIn(authToken.getExpireIn() > 0 ? authToken.getExpireIn() : -1).setRefreshTokenExpireIn(Objects.nonNull(authToken.getRefreshToken()) ? authToken.getRefreshTokenExpireIn() : -1).setRefreshToken(Objects.nonNull(authToken.getRefreshToken()) ? authToken.getRefreshToken() : null));
        return socialUserMapper.selectById(id);
    }


    @Override
    public PageResult<SocialUserDO> getSocialUserPage(SocialUserPageReqVO pageReqVO) {

        // 获得绑定
        List<SocialUserBindDO> socialUserBinds = socialUserBindMapper.selectListByUserIdAndUserType(null, UserTypeEnum.ADMIN.getValue());
        if (CollUtil.isEmpty(socialUserBinds)) {
            return PageResult.empty();
        }

        return socialUserMapper.selectPage2(pageReqVO, convertSet(socialUserBinds, SocialUserBindDO::getSocialUserId));
    }

    @Override
    public void bindWechatUser(SocialUserDO socialUserDO, SocialUserBindDO socialUserBindDO) {
        socialUserMapper.insert(socialUserDO);
        socialUserBindDO.setSocialUserId(socialUserDO.getId());
        socialUserBindMapper.insert(socialUserBindDO);
    }

    @Override
    public SocialUserDO getSocialUser(Long userId, Integer userType) {
        return getSocialUserList(userId, UserTypeEnum.ADMIN.getValue())
                .stream().filter(socialUserDO -> SocialTypeEnum.WECHAT_MP.getType().equals(socialUserDO.getType()))
                .findFirst().orElse(null);
    }

    /**
     * 获得指定用户的社交用户
     *
     * @param userId     用户编号
     * @param userType   用户类型
     * @param socialType 社交平台的类型
     * @return 社交用户
     */
    @Override
    public SocialUserDO getSocialUser(Long userId, Integer userType, Integer socialType) {
        return getSocialUserList(userId, UserTypeEnum.ADMIN.getValue()).stream().filter(socialUserDO -> socialType.equals(socialUserDO.getType())).findFirst().orElse(null);
    }

    @Override
    public Map<String, SocialUserDO> getSocialUser(List<String> socialId) {
        List<SocialUserDO> socialUserList = socialUserMapper.selectList(SocialUserDO::getId, socialId);
        return socialUserList.stream().collect(Collectors.toMap(socialUserDO -> socialUserDO.getId().toString(), Function.identity(), (a, b) -> a));
    }

    private Boolean validatedTokenExpireIn(SocialUserDO socialUser) {
        DateTime expireData = DateUtil.date((long) socialUser.getExpireIn() * 1000);
        DateTime now = DateUtil.date();
        if (DateUtil.compare(expireData, now) <= 0) {
            return false;
        }

        return DateUtil.compare(expireData, now.offsetNew(DateField.MINUTE, 2)) > 0;

    }

    private void validatedRefreshTokenExpireIn(SocialUserDO socialUser) {
        long data = (long) socialUser.getRefreshTokenExpireIn() * 1000;
        DateTime expireData = DateUtil.date(data);
        DateTime now = DateUtil.date();
        if (DateUtil.compare(expireData, now) <= 0) {
            throw exception(SOCIAL_USER_REFRESH_AUTH_EXPIRE);
        }

    }

}
