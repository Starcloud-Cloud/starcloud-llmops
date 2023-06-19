package com.starcloud.ops.business.user.service;

import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;

public interface LlmUserService {

    /**
     * 邮箱注册帐号
     * @param request
     * @return
     */
    boolean register(RegisterRequest request);

    /**
     * 激活用户
     * @param activationCode
     * @return
     */
    boolean activation(String activationCode);

    /**
     * 找回密码
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
     * @return
     */
    String inviteUser();


}
