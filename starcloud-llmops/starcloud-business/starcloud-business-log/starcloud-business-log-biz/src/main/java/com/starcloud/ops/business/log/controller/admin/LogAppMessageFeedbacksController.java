package com.starcloud.ops.business.log.controller.admin;


import com.starcloud.ops.business.log.api.feedbacks.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageFeedbacksConvert;
import com.starcloud.ops.business.log.dal.dataobject.*;
import com.starcloud.ops.business.log.service.feedbacks.LogAppMessageFeedbacksService;
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

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;


@Tag(name = "管理后台 - 应用执行日志结果反馈")
@RestController
@RequestMapping("/log/app-message-feedbacks")
@Validated
public class LogAppMessageFeedbacksController {

    @Resource
    private LogAppMessageFeedbacksService appMessageFeedbacksService;

    @PostMapping("/create")
    @Operation(summary = "创建应用执行日志结果反馈")
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:create')")
    public CommonResult<Long> createAppMessageFeedbacks(@Valid @RequestBody LogAppMessageFeedbacksCreateReqVO createReqVO) {
        return success(appMessageFeedbacksService.createAppMessageFeedbacks(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用执行日志结果反馈")
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:update')")
    public CommonResult<Boolean> updateAppMessageFeedbacks(@Valid @RequestBody LogAppMessageFeedbacksUpdateReqVO updateReqVO) {
        appMessageFeedbacksService.updateAppMessageFeedbacks(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用执行日志结果反馈")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:delete')")
    public CommonResult<Boolean> deleteAppMessageFeedbacks(@RequestParam("id") Long id) {
        appMessageFeedbacksService.deleteAppMessageFeedbacks(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用执行日志结果反馈")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:query')")
    public CommonResult<LogAppMessageFeedbacksRespVO> getAppMessageFeedbacks(@RequestParam("id") Long id) {
        LogAppMessageFeedbacksDO appMessageFeedbacks = appMessageFeedbacksService.getAppMessageFeedbacks(id);
        return success(LogAppMessageFeedbacksConvert.INSTANCE.convert(appMessageFeedbacks));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用执行日志结果反馈列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:query')")
    public CommonResult<List<LogAppMessageFeedbacksRespVO>> getAppMessageFeedbacksList(@RequestParam("ids") Collection<Long> ids) {
        List<LogAppMessageFeedbacksDO> list = appMessageFeedbacksService.getAppMessageFeedbacksList(ids);
        return success(LogAppMessageFeedbacksConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用执行日志结果反馈分页")
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:query')")
    public CommonResult<PageResult<LogAppMessageFeedbacksRespVO>> getAppMessageFeedbacksPage(@Valid LogAppMessageFeedbacksPageReqVO pageVO) {
        PageResult<LogAppMessageFeedbacksDO> pageResult = appMessageFeedbacksService.getAppMessageFeedbacksPage(pageVO);
        return success(LogAppMessageFeedbacksConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用执行日志结果反馈 Excel")
    @PreAuthorize("@ss.hasPermission('log:app-message-feedbacks:export')")
    @OperateLog(type = EXPORT)
    public void exportAppMessageFeedbacksExcel(@Valid LogAppMessageFeedbacksExportReqVO exportReqVO,
                                               HttpServletResponse response) throws IOException {
        List<LogAppMessageFeedbacksDO> list = appMessageFeedbacksService.getAppMessageFeedbacksList(exportReqVO);
        // 导出 Excel
        List<LogAppMessageFeedbacksExcelVO> datas = LogAppMessageFeedbacksConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "应用执行日志结果反馈.xls", "数据", LogAppMessageFeedbacksExcelVO.class, datas);
    }

}