package com.starcloud.ops.business.app.controller.admin.appinfrajob;

import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobPageReqVO;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobRespVO;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.appinfrajob.AppInfraJobDO;
import com.starcloud.ops.business.app.service.app.appinfrajob.AppInfraJobService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;


@Tag(name = "管理后台 - 应用定时执行任务")
@RestController
@RequestMapping("/llm/app-infra-job")
@Validated
public class AppInfraJobController {

    @Resource
    private AppInfraJobService appInfraJobService;

    @PostMapping("/create")
    @Operation(summary = "创建应用定时执行任务")
    @PreAuthorize("@ss.hasPermission('llm:app-infra-job:create')")
    public CommonResult<Long> createAppInfraJob(@Valid @RequestBody AppInfraJobSaveReqVO createReqVO) {
        return success(appInfraJobService.createAppInfraJob(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用定时执行任务")
    @PreAuthorize("@ss.hasPermission('llm:app-infra-job:update')")
    public CommonResult<Boolean> updateAppInfraJob(@Valid @RequestBody AppInfraJobSaveReqVO updateReqVO) {
        appInfraJobService.updateAppInfraJob(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用定时执行任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('llm:app-infra-job:delete')")
    public CommonResult<Boolean> deleteAppInfraJob(@RequestParam("id") Long id) {
        appInfraJobService.deleteAppInfraJob(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用定时执行任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('llm:app-infra-job:query')")
    public CommonResult<AppInfraJobRespVO> getAppInfraJob(@RequestParam("id") Long id) {
        AppInfraJobDO appInfraJob = appInfraJobService.getAppInfraJob(id);
        return success(BeanUtils.toBean(appInfraJob, AppInfraJobRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用定时执行任务分页")
    @PreAuthorize("@ss.hasPermission('llm:app-infra-job:query')")
    public CommonResult<PageResult<AppInfraJobRespVO>> getAppInfraJobPage(@Valid AppInfraJobPageReqVO pageReqVO) {
        PageResult<AppInfraJobDO> pageResult = appInfraJobService.getAppInfraJobPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppInfraJobRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用定时执行任务 Excel")
    @PreAuthorize("@ss.hasPermission('llm:app-infra-job:export')")
    @OperateLog(type = EXPORT)
    public void exportAppInfraJobExcel(@Valid AppInfraJobPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppInfraJobDO> list = appInfraJobService.getAppInfraJobPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "应用定时执行任务.xls", "数据", AppInfraJobRespVO.class,
                        BeanUtils.toBean(list, AppInfraJobRespVO.class));
    }

}