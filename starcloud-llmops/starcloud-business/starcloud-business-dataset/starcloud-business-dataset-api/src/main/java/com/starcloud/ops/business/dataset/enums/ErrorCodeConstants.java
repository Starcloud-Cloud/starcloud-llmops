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

    //======================================数据集-错误码区间 [2-007-001-000 ~ 2-008-001-100======================================
    ErrorCode DATASETS_PARAM_NULL = new ErrorCode(2007001001, "数据集参数不可以为空");
    ErrorCode DATASETS_NOT_EXISTS = new ErrorCode(2007001002, "数据集不存在");
    ErrorCode DATASETS_ERROR_REPEAT = new ErrorCode(2007001003, "数据集编号为{},数据异常");

    ErrorCode DATASETS_EMBEDDING_ERROR = new ErrorCode(2007001004, "embedding索引异常");
    ErrorCode DATASETS_NOT_EXIST_ERROR = new ErrorCode(2007001005, "dataset不存在");
    ErrorCode FILE_TYPE_NOT_ALLOW = new ErrorCode(2007001006, "文件类型不支持");



    //======================================数据集源数据======================================
    ErrorCode DATASET_SOURCE_DATA_NOT_EXISTS = new ErrorCode(2007002001, "数据不存在,请刷新后重试");
    ErrorCode  DATASET_SOURCE_DATA_EXISTS= new ErrorCode(2007002002, "源数据已经存在");
    ErrorCode DATASET_SOURCE_DATA_ARCHIVED = new ErrorCode(2007002003, "数据集源数据归档失败，当前数据集已经归档");
    ErrorCode DATASET_SOURCE_DATA_UNARCHIVED = new ErrorCode(2007002004, "数据集源数据取消归档失败，当前数据集未归档");

    ErrorCode SUMMARY_ERROR = new ErrorCode(1003001011, "总结文档失败");


    //======================================数据集源数据存储======================================

    ErrorCode DATASET_STORAGE_NOT_EXISTS = new ErrorCode(2007003001, "数据集源数据存储不存在");

    //======================================数据集源数据上传======================================
    ErrorCode SOURCE_DATA_UPLOAD_URL_EMPTY = new ErrorCode(2007004001, "上传失败，URL 列表为空");
    ErrorCode SOURCE_DATA_UPLOAD_STRING_EMPTY = new ErrorCode(2007004002, "上传失败，字符串 列表为空");
    ErrorCode SOURCE_DATA_UPLOAD_FILE_EMPTY = new ErrorCode(2007004003, "上传失败，文件 列表为空");
    ErrorCode SOURCE_DATA_UPLOAD_SPLIT_RULE_EMPTY = new ErrorCode(2007004002, "上传失败，分割规则不可以为空");
    ErrorCode SOURCE_DATA_UPLOAD_DATASET_NO_EXIST = new ErrorCode(2007004002, "上传失败，数据集不存在");


}
