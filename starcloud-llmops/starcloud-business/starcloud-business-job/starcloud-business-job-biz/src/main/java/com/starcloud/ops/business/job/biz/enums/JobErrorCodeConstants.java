package com.starcloud.ops.business.job.biz.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 创作中心异常码常量
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
public interface JobErrorCodeConstants {

    ErrorCode REQUEST_POWERJOB_ERROR = new ErrorCode(720100001, "请求powerjob失败：{}，{}");

    ErrorCode JOB_NOT_EXIST = new ErrorCode(720100002, "job任务不存在：{} = {}");

    ErrorCode JOB_CONFIG_ERROR = new ErrorCode(720100003, "任务配置错误，businessJobId={}, {}");

    ErrorCode EXIST_JOB = new ErrorCode(720100004, "已存在定时任务，ForeignKey={}");

    ErrorCode COZE_RESULT_ERROR = new ErrorCode(720100005, "coze生成结果格式错误，result={}");

    ErrorCode LIBRARY_COLUMN_ERROR = new ErrorCode(720100006, "素材库错误，libraryId={}");

    ErrorCode FIELD_VALID_ERROR = new ErrorCode(720100007, "{}");

}
