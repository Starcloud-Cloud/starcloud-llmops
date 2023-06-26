package com.starcloud.ops.business.user.service;

import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
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
     * 创建新用户 部门 绑定角色
     *
     * @param username
     * @param email
     * @param password
     * @return
     */
    Long createNewUser(String username, String email, String password, Long parentDeptId);


    /**
     * 修改用户个人信息
     * @param request
     * @return
     */
    Boolean updateUserProfile(UserProfileUpdateRequest request);
}
