package com.starcloud.ops.business.dataset.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * @className    : ErrorCodeConstants
 * @description  : [数据集 错误码]
 * @author       : [wuruiqiang]
 * @version      : [v1.0]
 * @createTime   : [2023/5/31 16:14]
 * @updateUser   : [wuruiqiang]
 * @updateTime   : [2023/5/31 16:14]
 * @updateRemark : [暂无修改]
 */
public interface ErrorCodeConstants {


    ErrorCode DATASETS_PARAM_NULL = new ErrorCode(1002001001, "数据集参数不可以为空");
    ErrorCode DATASETS_NOT_EXISTS = new ErrorCode(1002001001, "数据集不存在");
    ErrorCode DATASETS_ERROR_REPEAT = new ErrorCode(1003001009, "数据集编号为{},数据异常");


}
