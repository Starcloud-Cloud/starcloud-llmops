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
    ErrorCode USER_RIGHTS_MATRIX_BEAN_NOT_ENOUGH= new ErrorCode(2_004_008_006, "用户魔法豆权益不足");

    ErrorCode USER_RIGHTS_EXPIRE_FAIL_STATUS_NOT_ENABLE = new ErrorCode(2_004_008_005, "用户权益过期失败，用户等级不是启用状态");
    ErrorCode USER_RIGHTS_LIMIT_USE = new ErrorCode(2_004_008_006, " 今日{}权益已经使用完，请升级或明天再试！");
    ErrorCode USER_RIGHTS_LIMIT_USE_TYPE_NO_FOUND = new ErrorCode(2_004_008_007, "权益未找到，请核对后重试");




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
    ErrorCode NOT_IN_THIS_DEPT = new ErrorCode(2000500001, "用户未绑定此空间");

    ErrorCode INSUFFICIENT_PERMISSIONS = new ErrorCode(2000500002, "空间权限不足");

    ErrorCode INVALID_CODE = new ErrorCode(2000500003, "无效的邀请码");

    ErrorCode DEPT_BIND_ERROR = new ErrorCode(2000500004, "绑定空间失败，请刷新后重试");

    ErrorCode SUPER_ADMIN_DELETED = new ErrorCode(2000500005, "超级管理员不允许移除");

    ErrorCode DEPT_ROLE_NOT_EXIST = new ErrorCode(2000500006, "空间角色不存在");

    ErrorCode DELETE_ERROR = new ErrorCode(2000500007, "删除失败");

    ErrorCode DEPT_IS_FULL =  new ErrorCode(2000500008, "空间人数超过 {} 个");

    ErrorCode TOO_MANY_DEPT_NUM =  new ErrorCode(2000500009, "加入空间数超过 {} 个");

    ErrorCode NOT_BIND_DEPT =  new ErrorCode(2000500010, "用户:{} 未绑定空间:{}");

    ErrorCode NO_PERMISSION =  new ErrorCode(2000500011, "用户没有[{}]权限");

    ErrorCode SUPER_ADMIN_PERMISSION =  new ErrorCode(2000500012, "管理员无法修改数据");

    // 用户通知

    ErrorCode NOT_SUPPORTED_NOTIFY_MEDIA =  new ErrorCode(2000600001, "不支持的消息媒介 {}");

    ErrorCode NOT_SUPPORTED_TEMPLATE_CODE =  new ErrorCode(2000600002, "不支持的模板类型 {}");

    ErrorCode PARAMS_ERROR =  new ErrorCode(2000600003, "用户 {} 模板参数({})缺失");

    ErrorCode MISSING_TEMP_CODE =  new ErrorCode(2000600004, " {} 模板code未配置");

    ErrorCode TEMP_CODE_NOT_EXITS =  new ErrorCode(2000600005, " {} 消息平台模板code不存在");

    ErrorCode TEMP_PARAMS_NOT_CONSISTENT =  new ErrorCode(2000600006, "自定义模板参数[{}]与平台模板参数[{}] 不一致");

    ErrorCode WX_SERVICE_ERROR =  new ErrorCode(2000600007, "查询模板失败, [{}]");

}
