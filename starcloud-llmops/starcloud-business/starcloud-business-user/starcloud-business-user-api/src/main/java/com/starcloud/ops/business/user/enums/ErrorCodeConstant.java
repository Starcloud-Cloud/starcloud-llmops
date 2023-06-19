package com.starcloud.ops.business.user.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstant {

    ErrorCode OPERATE_TIME_OUT = new ErrorCode(2001000000, "请在30分钟之内操作");

    ErrorCode ACTIVATION_CODE_ERROR = new ErrorCode(2001000001, "无效的激活码");

    ErrorCode REPEAT_RECOVER = new ErrorCode(2001000002, "此邮箱30分钟内已找回");

    ErrorCode IP_CONVERT_ERROR = new ErrorCode(2001000003, "ip地址转换异常");

    ErrorCode ACTIVATION_CODE = new ErrorCode(2001000004, "无效的修改码");

    ErrorCode ENCRYPTION_ERROR = new ErrorCode(2001000004, "计算邀请码异常");


}
