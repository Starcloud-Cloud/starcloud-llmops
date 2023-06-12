package com.starcloud.ops.business.log.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.annotations.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageAnnotationsConvert;
import com.starcloud.ops.business.log.dal.dataobject.*;
import com.starcloud.ops.business.log.service.annotations.LogAppMessageAnnotationsService;
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
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

@Tag(name = "管理后台 - 应用执行日志结果反馈标注")
@RestController
@RequestMapping("/log/app-message-annotations")
@Validated
public class LogAppMessageAnnotationsController {

    @Resource
    private LogAppMessageAnnotationsService appMessageAnnotationsService;

    @PostMapping("/create")
    @Operation(summary = "创建应用执行日志结果反馈标注")
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:create')")
    public CommonResult<Long> createAppMessageAnnotations(@Valid @RequestBody LogAppMessageAnnotationsCreateReqVO createReqVO) {
        return success(appMessageAnnotationsService.createAppMessageAnnotations(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用执行日志结果反馈标注")
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:update')")
    public CommonResult<Boolean> updateAppMessageAnnotations(@Valid @RequestBody LogAppMessageAnnotationsUpdateReqVO updateReqVO) {
        appMessageAnnotationsService.updateAppMessageAnnotations(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用执行日志结果反馈标注")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:delete')")
    public CommonResult<Boolean> deleteAppMessageAnnotations(@RequestParam("id") Long id) {
        appMessageAnnotationsService.deleteAppMessageAnnotations(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用执行日志结果反馈标注")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:query')")
    public CommonResult<LogAppMessageAnnotationsRespVO> getAppMessageAnnotations(@RequestParam("id") Long id) {
        LogAppMessageAnnotationsDO appMessageAnnotations = appMessageAnnotationsService.getAppMessageAnnotations(id);
        return success(LogAppMessageAnnotationsConvert.INSTANCE.convert(appMessageAnnotations));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用执行日志结果反馈标注列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:query')")
    public CommonResult<List<LogAppMessageAnnotationsRespVO>> getAppMessageAnnotationsList(@RequestParam("ids") Collection<Long> ids) {
        List<LogAppMessageAnnotationsDO> list = appMessageAnnotationsService.getAppMessageAnnotationsList(ids);
        return success(LogAppMessageAnnotationsConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用执行日志结果反馈标注分页")
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:query')")
    public CommonResult<PageResult<LogAppMessageAnnotationsRespVO>> getAppMessageAnnotationsPage(@Valid LogAppMessageAnnotationsPageReqVO pageVO) {
        PageResult<LogAppMessageAnnotationsDO> pageResult = appMessageAnnotationsService.getAppMessageAnnotationsPage(pageVO);
        return success(LogAppMessageAnnotationsConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用执行日志结果反馈标注 Excel")
    @PreAuthorize("@ss.hasPermission('log:app-message-annotations:export')")
    @OperateLog(type = EXPORT)
    public void exportAppMessageAnnotationsExcel(@Valid LogAppMessageAnnotationsExportReqVO exportReqVO,
                                                 HttpServletResponse response) throws IOException {
        List<LogAppMessageAnnotationsDO> list = appMessageAnnotationsService.getAppMessageAnnotationsList(exportReqVO);
        // 导出 Excel
        List<LogAppMessageAnnotationsExcelVO> datas = LogAppMessageAnnotationsConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "应用执行日志结果反馈标注.xls", "数据", LogAppMessageAnnotationsExcelVO.class, datas);
    }

}