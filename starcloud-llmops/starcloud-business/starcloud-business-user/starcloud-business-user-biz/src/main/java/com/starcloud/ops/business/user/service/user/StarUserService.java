package com.starcloud.ops.business.user.service.user;

import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import com.starcloud.ops.business.user.controller.admin.vo.AdminUserInfoRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import com.starcloud.ops.business.user.pojo.dto.UserDTO;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.pojo.request.UserProfileUpdateRequest;

public interface StarUserService {

    /**
     * 邮箱注册帐号
     *
     * @param request
     * @return
     */
    boolean register(RegisterRequest request);

    /**
     * 激活用户
     *
     * @param activationCode
     * @return
     */
    boolean activation(String activationCode);

    /**
     * 增加权益
     *
     * @param currentUserId
     * @param inviteUserId
     */
    void addBenefits(Long currentUserId, Long inviteUserId);

    /**
     * 增加权益
     * @param currentUserId
     * @param inviteCode 邀请码
     */
    void addInviteBenefits(Long currentUserId, String inviteCode);

    /**
     * 找回密码
     *
     * @param request
     * @return
     */
    boolean recoverPassword(RecoverPasswordRequest request);

    /**
     * 验证code是否过期
     */
    boolean checkCode(String verificationCode);

    /**
     * 修改密码
     */
    boolean changePassword(ChangePasswordRequest request);

    /**
     * 获取邀请链接
     *
     * @return
     */
    UserDetailVO userDetail();

    /**
     * 获取用户详情 包含用户等级 用户权益
     * @param userId
     * @return
     */
    AdminUserInfoRespVO userDetail(Long userId);

    /**
     * 创建新用户 部门 绑定角色
     *
     * @param userDTO
     * @return
     */
    Long createNewUser(UserDTO userDTO);


    /**
     * 修改用户个人信息
     *
     * @param request
     * @return
     */
    Boolean updateUserProfile(UserProfileUpdateRequest request);

    /**
     * 登录并记录日志
     * @param userId
     * @param username
     * @param logType
     * @return
     */
    AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType);


    /**
     * 用户是否是新用户
     * @param userId
     * @return
     */
    Boolean isNewUser(Long userId);
}
