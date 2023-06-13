package com.starcloud.ops.business.log.controller.admin;

import com.starcloud.ops.business.log.api.messagesave.vo.*;
import com.starcloud.ops.business.log.convert.LogAppMessageSaveConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageSaveDO;
import com.starcloud.ops.business.log.service.messagesave.LogAppMessageSaveService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
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

@Tag(name = "管理后台 - 应用执行日志结果保存")
@RestController
@RequestMapping("/log/app-message-save")
@Validated
public class LogAppMessageSaveController {

    @Resource
    private LogAppMessageSaveService appMessageSaveService;

    @PostMapping("/create")
    @Operation(summary = "创建应用执行日志结果保存")
    @PreAuthorize("@ss.hasPermission('log:app-message-save:create')")
    public CommonResult<Long> createAppMessageSave(@Valid @RequestBody LogAppMessageSaveCreateReqVO createReqVO) {
        return success(appMessageSaveService.createAppMessageSave(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用执行日志结果保存")
    @PreAuthorize("@ss.hasPermission('log:app-message-save:update')")
    public CommonResult<Boolean> updateAppMessageSave(@Valid @RequestBody LogAppMessageSaveUpdateReqVO updateReqVO) {
        appMessageSaveService.updateAppMessageSave(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用执行日志结果保存")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('log:app-message-save:delete')")
    public CommonResult<Boolean> deleteAppMessageSave(@RequestParam("id") Long id) {
        appMessageSaveService.deleteAppMessageSave(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用执行日志结果保存")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('log:app-message-save:query')")
    public CommonResult<LogAppMessageSaveRespVO> getAppMessageSave(@RequestParam("id") Long id) {
        LogAppMessageSaveDO appMessageSave = appMessageSaveService.getAppMessageSave(id);
        return success(LogAppMessageSaveConvert.INSTANCE.convert(appMessageSave));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用执行日志结果保存列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('log:app-message-save:query')")
    public CommonResult<List<LogAppMessageSaveRespVO>> getAppMessageSaveList(@RequestParam("ids") Collection<Long> ids) {
        List<LogAppMessageSaveDO> list = appMessageSaveService.getAppMessageSaveList(ids);
        return success(LogAppMessageSaveConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用执行日志结果保存分页")
    @PreAuthorize("@ss.hasPermission('log:app-message-save:query')")
    public CommonResult<PageResult<LogAppMessageSaveRespVO>> getAppMessageSavePage(@Valid LogAppMessageSavePageReqVO pageVO) {
        PageResult<LogAppMessageSaveDO> pageResult = appMessageSaveService.getAppMessageSavePage(pageVO);
        return success(LogAppMessageSaveConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用执行日志结果保存 Excel")
    @PreAuthorize("@ss.hasPermission('log:app-message-save:export')")
    @OperateLog(type = EXPORT)
    public void exportAppMessageSaveExcel(@Valid LogAppMessageSaveExportReqVO exportReqVO,
                                          HttpServletResponse response) throws IOException {
        List<LogAppMessageSaveDO> list = appMessageSaveService.getAppMessageSaveList(exportReqVO);
        // 导出 Excel
        List<LogAppMessageSaveExcelVO> datas = LogAppMessageSaveConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "应用执行日志结果保存.xls", "数据", LogAppMessageSaveExcelVO.class, datas);
    }

}