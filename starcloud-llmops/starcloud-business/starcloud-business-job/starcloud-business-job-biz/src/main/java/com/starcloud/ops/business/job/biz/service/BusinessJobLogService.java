package com.starcloud.ops.business.job.biz.service;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.LibraryJobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.CozeJobLogRespVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.JobLogDTO;

public interface BusinessJobLogService {

    /**
     * 记录日志
     */
    Long recordLog(JobLogBaseVO logBaseVO);

    /**
     * 分页查询任务日志
     */
    PageResult<CozeJobLogRespVO> page(JobLogPageReqVO pageReqVO);

    PageResult<CozeJobLogRespVO> libraryPage(LibraryJobLogPageReqVO pageReqVO);

    /**
     * 我的插件执行日志
     * @param pageParam
     * @return
     */
    PageResult<JobLogDTO> pluginLog(PageParam pageParam);
}
