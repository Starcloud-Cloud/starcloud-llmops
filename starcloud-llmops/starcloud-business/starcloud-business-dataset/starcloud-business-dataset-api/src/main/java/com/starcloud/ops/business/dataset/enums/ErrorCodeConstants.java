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

    //======================================数据集======================================
    ErrorCode DATASETS_PARAM_NULL = new ErrorCode(1002001001, "数据集参数不可以为空");
    ErrorCode DATASETS_NOT_EXISTS = new ErrorCode(1002001001, "数据集不存在");
    ErrorCode DATASETS_ERROR_REPEAT = new ErrorCode(1003001009, "数据集编号为{},数据异常");

    ErrorCode DATASETS_EMBEDDING_ERROR = new ErrorCode(1003001010, "embedding索引异常");



    //======================================数据集源数据======================================
    ErrorCode DATASET_SOURCE_DATA_NOT_EXISTS = new ErrorCode(1003001009, "数据集源数据不存在");
    ErrorCode  DATASET_SOURCE_DATA_EXISTS= new ErrorCode(1003001009, "源数据已经存在");

    ErrorCode DATASET_SOURCE_DATA_ARCHIVED = new ErrorCode(99999999, "数据集源数据归档失败，当前数据集已经归档");
    ErrorCode DATASET_SOURCE_DATA_UNARCHIVED = new ErrorCode(99999999, "数据集源数据取消归档失败，当前数据集未归档");

    //======================================数据集源数据存储======================================

    ErrorCode DATASET_STORAGE_NOT_EXISTS = new ErrorCode(99999999, "数据集源数据存储不存在");

}
