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

    ErrorCode DATASETS_APPID_NOT_EXISTS = new ErrorCode(2007001007, "该应用下的知识库不存在");

    ErrorCode DATASETS_APPID_REPEAT_BIND = new ErrorCode(2007001008, "该应用下存在多个知识库");

    ErrorCode DATASETS_CONVERSATION_NOT_EXISTS = new ErrorCode(2007001009, "该会话下的知识库不存在");
    ErrorCode DATASETS_CONVERSATION_REPEAT_BIND = new ErrorCode(2007001010, "该会话下存在多个知识库");



    //======================================数据集源数据======================================
    ErrorCode DATASET_SOURCE_DATA_NOT_EXISTS = new ErrorCode(2007002001, "数据不存在,请刷新后重试");
    ErrorCode  DATASET_SOURCE_DATA_EXISTS= new ErrorCode(2007002002, "源数据已经存在");
    ErrorCode DATASET_SOURCE_DATA_ARCHIVED = new ErrorCode(2007002003, "数据集源数据归档失败，当前数据集已经归档");
    ErrorCode DATASET_SOURCE_DATA_UNARCHIVED = new ErrorCode(2007002004, "数据集源数据取消归档失败，当前数据集未归档");

    ErrorCode DATASET_SOURCE_DATA_STUDY_IN = new ErrorCode(2007002005, "数据学习中，请在学习完成后查看");
    ErrorCode DATASET_SOURCE_UPLOAD_DATA_FAIL_APPID = new ErrorCode(1003001006, "上传数据异常，应用 ID 为空");
    ErrorCode DATASET_SOURCE_DATA_ENABLE_STATUS_FAIL = new ErrorCode(2007002007, "数据状态已经刷新,请刷新后重试");
    ErrorCode SUMMARY_ERROR = new ErrorCode(1003001011, "总结文档失败");





    //======================================数据集源数据存储======================================

    ErrorCode DATASET_STORAGE_NOT_EXISTS = new ErrorCode(2007003001, "数据集源数据存储不存在");

    //======================================数据集源数据上传======================================
    ErrorCode SOURCE_DATA_UPLOAD_URL_EMPTY = new ErrorCode(2007004001, "上传失败，HTML 列表为空");
    ErrorCode SOURCE_DATA_UPLOAD_STRING_EMPTY = new ErrorCode(2007004002, "上传失败，字符串 列表为空");
    ErrorCode SOURCE_DATA_UPLOAD_FILE_EMPTY = new ErrorCode(2007004003, "上传失败，文件 列表为空");
    ErrorCode SOURCE_DATA_UPLOAD_SPLIT_RULE_EMPTY = new ErrorCode(2007004002, "上传失败，分割规则不可以为空");
    ErrorCode SOURCE_DATA_UPLOAD_DATASET_NO_EXIST = new ErrorCode(2007004002, "上传失败，数据集不存在");

    //======================================数据集源数据上传======================================
    ErrorCode DATASET_HANDLE_SYS_RULE_NO_EXISTS = new ErrorCode(2007005000, "系统预处理规则不存在，请联系管理员");
    ErrorCode DATASET_HANDLE_RULE_EXISTS = new ErrorCode(2007005001, "知识库预处理规则不存在");

    ErrorCode DATASET_HANDLE_RULES_NULL = new ErrorCode(2007005002, "知识库预处理规则不存在,无法预处理数据");

    ErrorCode DATASET_HANDLE_RULE_REPEAT_NORMAL = new ErrorCode(2007005003, "数据预处理失败，规则匹配到多条预处理规则{}");

    ErrorCode DATASET_HANDLE_RULE_TYPE_UNKNOWN = new ErrorCode(2007005004, " 未知数据类型，请核对数据后重新提交");
    ErrorCode DATASET_HANDLE_RULE_FAIL = new ErrorCode(2007005004, " 调试规则失败，未匹配到规则，请核对数据后重新提交");


}
