package com.starcloud.ops.business.log.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.message.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
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

@Tag(name = "管理后台 - 应用执行日志结果")
@RestController
@RequestMapping("/log/app-message")
@Validated
public class LogAppMessageController {

    @Resource
    private LogAppMessageService appMessageService;

    @PostMapping("/create")
    @Operation(summary = "创建应用执行日志结果")
    @PreAuthorize("@ss.hasPermission('log:app-message:create')")
    public CommonResult<Long> createAppMessage(@Valid @RequestBody LogAppMessageCreateReqVO createReqVO) {
        return success(appMessageService.createAppMessage(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用执行日志结果")
    @PreAuthorize("@ss.hasPermission('log:app-message:update')")
    public CommonResult<Boolean> updateAppMessage(@Valid @RequestBody LogAppMessageUpdateReqVO updateReqVO) {
        appMessageService.updateAppMessage(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用执行日志结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('log:app-message:delete')")
    public CommonResult<Boolean> deleteAppMessage(@RequestParam("id") Long id) {
        appMessageService.deleteAppMessage(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用执行日志结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('log:app-message:query')")
    public CommonResult<LogAppMessageRespVO> getAppMessage(@RequestParam("id") Long id) {
        LogAppMessageDO appMessage = appMessageService.getAppMessage(id);
        return success(LogAppMessageConvert.INSTANCE.convert(appMessage));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用执行日志结果列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('log:app-message:query')")
    public CommonResult<List<LogAppMessageRespVO>> getAppMessageList(@RequestParam("ids") Collection<Long> ids) {
        List<LogAppMessageDO> list = appMessageService.getAppMessageList(ids);
        return success(LogAppMessageConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用执行日志结果分页")
    @PreAuthorize("@ss.hasPermission('log:app-message:query')")
    public CommonResult<PageResult<LogAppMessageRespVO>> getAppMessagePage(@Valid LogAppMessagePageReqVO pageVO) {
        PageResult<LogAppMessageDO> pageResult = appMessageService.getAppMessagePage(pageVO);
        return success(LogAppMessageConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用执行日志结果 Excel")
    @PreAuthorize("@ss.hasPermission('log:app-message:export')")
    @OperateLog(type = EXPORT)
    public void exportAppMessageExcel(@Valid LogAppMessageExportReqVO exportReqVO,
                                      HttpServletResponse response) throws IOException {
        List<LogAppMessageDO> list = appMessageService.getAppMessageList(exportReqVO);
        // 导出 Excel
        List<LogAppMessageExcelVO> datas = LogAppMessageConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "应用执行日志结果.xls", "数据", LogAppMessageExcelVO.class, datas);
    }

}