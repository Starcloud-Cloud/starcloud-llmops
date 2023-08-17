package com.starcloud.ops.business.user.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstant {

    ErrorCode OPERATE_TIME_OUT = new ErrorCode(2000100000, "请在30分钟之内操作");

    ErrorCode ACTIVATION_CODE_ERROR = new ErrorCode(2000100001, "无效的激活码");

    ErrorCode REPEAT_RECOVER = new ErrorCode(2000100002, "此邮箱30分钟内已找回");

    ErrorCode IP_CONVERT_ERROR = new ErrorCode(2000100003, "ip地址转换异常");

    ErrorCode ACTIVATION_CODE = new ErrorCode(2000100004, "无效的修改码");

    ErrorCode ENCRYPTION_ERROR = new ErrorCode(2000100005, "计算邀请码异常");

    ErrorCode CREATE_QR_ERROR = new ErrorCode(2000100006, "获取公共号二维码异常");

    ErrorCode UN_AUTH_ERROR = new ErrorCode(2000100007, "请扫描二维码授权");

    ErrorCode USERNAME_SIZE_ERROR = new ErrorCode(2000100008, "账号长度为 4-16 位");

    ErrorCode ROLE_NOT_EXIST = new ErrorCode(2000100009, "角色不存在");

    ErrorCode ACTIVATION_USER_ERROR = new ErrorCode(2000100010, "激活用户失败，请联系管理员激活");

    ErrorCode INVALID_PHONE_NUMBER = new ErrorCode(2000100011, "请填入正确的手机号");

    ErrorCode INVALID_EMAIL = new ErrorCode(2000100012, "请填入正确的邮箱地址");

    ErrorCode VERIFICATION_CODE_OVERDUE = new ErrorCode(2000100013, "无效的验证码，请重新发送验证码");

    ErrorCode INVALID_VERIFICATION_CODE = new ErrorCode(2000100013, "无效的验证码，请重新输入");



}
