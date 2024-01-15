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



    //========== 用户等级 2-004-011-000 ==========
    ErrorCode LEVEL_NOT_EXISTS = new ErrorCode(2_004_011_000, "用户等级不存在");
    ErrorCode LEVEL_NAME_EXISTS = new ErrorCode(2_004_011_001, "用户等级名称[{}]已被使用");
    ErrorCode LEVEL_VALUE_EXISTS = new ErrorCode(2_004_011_002, "用户等级值[{}]已被[{}]使用");
    ErrorCode LEVEL_ROLE_EXISTS = new ErrorCode(2_004_011_001, "用户等级所绑定的角色[{}]已被使用");
    ErrorCode LEVEL_HAS_USER = new ErrorCode(2_004_011_005, "用户等级下存在用户，无法删除");

    ErrorCode LEVEL_BIZ_NOT_SUPPORT = new ErrorCode(2_004_011_201, "用户等级业务类型不支持");
    ErrorCode LEVEL_EXPIRE_FAIL_STATUS_NOT_ENABLE = new ErrorCode(2_004_011_202, "用户等级过期失败，用户等级不是启用状态");



    //========== 权益记录 2-004-008-000 ==========
    ErrorCode RIGHTS_BIZ_NOT_SUPPORT = new ErrorCode(2_004_008_000, "用户权益记录业务类型不支持");
    ErrorCode USER_RIGHTS_NOT_ENOUGH= new ErrorCode(2_004_008_001, "用户{}权益不足");
    ErrorCode RIGHTS_VALID_TIME_NOT_EXISTS= new ErrorCode(2_004_008_002, "权益有效期不可以为空");

    ErrorCode USER_RIGHTS_BEAN_NOT_ENOUGH= new ErrorCode(2_004_008_003, "用户魔法豆权益不足");
    ErrorCode USER_RIGHTS_IMAGE_NOT_ENOUGH= new ErrorCode(2_004_008_004, "用户图片权益不足");

    ErrorCode USER_RIGHTS_EXPIRE_FAIL_STATUS_NOT_ENABLE = new ErrorCode(2_004_008_005, "用户权益过期失败，用户等级不是启用状态");

    //========== 签到配置 2-004-010-000 ==========

    //========== 签到配置 1-004-009-000 ==========
    ErrorCode SIGN_IN_CONFIG_NOT_EXISTS = new ErrorCode(2_004_009_000, "签到天数规则不存在");
    ErrorCode SIGN_IN_CONFIG_EXISTS = new ErrorCode(2_004_009_001, "签到天数规则已存在");

    //========== 签到配置 1-004-010-000 ==========
    ErrorCode SIGN_IN_RECORD_TODAY_EXISTS = new ErrorCode(2_004_010_000, "今日已签到，请勿重复签到");



    //========== 用户标签 2-004-006-000 ==========
    ErrorCode TAG_NOT_EXISTS = new ErrorCode(2_004_006_000, "用户标签不存在");
    ErrorCode TAG_NAME_EXISTS = new ErrorCode(2_004_006_001, "用户标签已经存在");
    ErrorCode TAG_HAS_USER = new ErrorCode(2_004_006_002, "用户标签下存在用户，无法删除");


    // 用户部门
    ErrorCode NOT_IN_THIS_DEPT = new ErrorCode(2000500001, "用户未绑定此部门");

    ErrorCode INSUFFICIENT_PERMISSIONS = new ErrorCode(2000500002, "部门权限不足");

    ErrorCode INVALID_CODE = new ErrorCode(2000500003, "无效的邀请码");

    ErrorCode DEPT_BIND_ERROR = new ErrorCode(2000500004, "绑定部门失败，请刷新后重试");

    ErrorCode SUPER_ADMIN_DELETED = new ErrorCode(2000500005, "超级管理员不允许移除");

    ErrorCode DEPT_ROLE_NOT_EXIST = new ErrorCode(2000500006, "部门角色不存在");



}
