package com.starcloud.ops.api.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode TEMPLATETYPE_NOT_EXISTS = new ErrorCode(600003001, "海报模板类型不存在");

    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(600003002, "海报模板不存在");

    ErrorCode ELEMENTTYPE_NOT_EXISTS = new ErrorCode(600003003, "海报元素类型不存在");

    ErrorCode ELEMENT_NOT_EXISTS = new ErrorCode(600003004, "海报元素不存在");
}