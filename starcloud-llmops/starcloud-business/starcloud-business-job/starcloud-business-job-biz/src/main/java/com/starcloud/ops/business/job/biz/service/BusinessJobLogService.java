package com.starcloud.ops.business.job.biz.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.LibraryJobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.JobLogRespVO;

public interface BusinessJobLogService {

    /**
     * 记录日志
     */
    Long recordLog(JobLogBaseVO logBaseVO);

    /**
     * 分页查询任务日志
     */
    PageResult<JobLogRespVO> page(JobLogPageReqVO pageReqVO);

    PageResult<JobLogRespVO> libraryPage(LibraryJobLogPageReqVO pageReqVO);

}
