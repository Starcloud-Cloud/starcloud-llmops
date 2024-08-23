package com.starcloud.ops.business.job.biz.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.job.biz.controller.admin.vo.request.JobLogPageReqVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.JobLogRespVO;
import com.starcloud.ops.business.job.biz.service.BusinessJobLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/llm/job/log")
@Tag(name = "星河云海-定时任务日志", description = "定时任务日志")
public class BusinessJobLogController {

    @Resource
    private BusinessJobLogService jobLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询插件定时任务日志", description = "分页查询定时任务日志")
    public CommonResult<PageResult<JobLogRespVO>> page(JobLogPageReqVO pageReqVO) {
        PageResult<JobLogRespVO> result = jobLogService.page(pageReqVO);
        return CommonResult.success(result);
    }
}
