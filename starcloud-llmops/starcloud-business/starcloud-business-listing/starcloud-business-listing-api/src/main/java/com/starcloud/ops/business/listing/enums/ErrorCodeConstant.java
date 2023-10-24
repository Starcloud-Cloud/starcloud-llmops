package com.starcloud.ops.business.listing.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

public interface ErrorCodeConstant {

    ErrorCode DICT_NAME_EXISTS = new ErrorCode(500010001, "词库名称已存在，{}");
    ErrorCode DICT_NOT_EXISTS = new ErrorCode(500010002, "词库不存在，{}");
    ErrorCode KEYWORD_IS_ANALYSIS = new ErrorCode(500010003, "关键词正在分析稍后重试，{}");

    ErrorCode DRAFT_NOT_EXISTS = new ErrorCode(500020001, "草稿不存在，uid = {}, version = {}");

    ErrorCode DRAFT_IS_EXECUTING = new ErrorCode(500020002, "草稿正在执行中");

}
