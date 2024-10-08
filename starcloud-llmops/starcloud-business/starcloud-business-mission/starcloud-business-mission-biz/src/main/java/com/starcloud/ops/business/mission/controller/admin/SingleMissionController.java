package com.starcloud.ops.business.mission.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import com.starcloud.ops.business.mission.controller.admin.vo.request.RefreshNoteDetailReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionImportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/llm/single")
@Tag(name = "星河云海 - 任务中心 单条任务", description = "任务中心 单条任务")
public class SingleMissionController {

    @Resource
    private SingleMissionService singleMissionService;

    @PutMapping("/add/{notificationUid}")
    @Operation(summary = "增加单条任务", description = "增加单条任务")
    public CommonResult<Boolean> addSingleMission(
            @PathVariable("notificationUid") String notificationUid,
            @RequestBody List<String> creativeUids) {
        if (CollectionUtils.isEmpty(creativeUids)) {
            return CommonResult.error(500, "绑定的任务uid不能为空");
        }
        singleMissionService.addSingleMission(notificationUid, creativeUids);
        return CommonResult.success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询任务", description = "分页查询任务")
    public CommonResult<PageResult<SingleMissionRespVO>> page(@Valid SinglePageQueryReqVO reqVO) {
        PageResult<SingleMissionRespVO> pageResult = singleMissionService.page(reqVO);
        return CommonResult.success(pageResult);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改单条任务", description = "修改单条任务")
    public CommonResult<Boolean> modify(@Valid @RequestBody SingleMissionModifyReqVO reqVO) {
        singleMissionService.modifySelective(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除单条任务", description = "删除单条任务")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        singleMissionService.delete(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/pick/{uid}")
    @Operation(summary = "领取任务", description = "领取任务")
    public CommonResult<Boolean> pick(@PathVariable("uid") String uid) {
        singleMissionService.pick(uid);
        return CommonResult.success(true);
    }

    @GetMapping("/export")
    @Operation(summary = "导出任务", description = "导出任务")
    public void exportSettlement(@Valid SinglePageQueryReqVO reqVO, HttpServletResponse response) throws IOException {
        List<SingleMissionExportVO> exported = singleMissionService.exportSettlement(reqVO);
        String fileName = reqVO.getNotificationUid() + ".xls";
        ExcelUtils.write(response, fileName, reqVO.getNotificationUid(), SingleMissionExportVO.class, exported);
    }

    @PostMapping("/import")
    @Operation(summary = "导入", description = "导入")
    public CommonResult<Boolean> importMission(@RequestParam("file") MultipartFile file) throws IOException {
        List<SingleMissionImportVO> importVOList = ExcelUtils.read(file, SingleMissionImportVO.class);
        singleMissionService.importSettlement(importVOList);
        return CommonResult.success(true);
    }

    @PostMapping("/refresh/note")
    @Operation(summary = "刷新互动信息", description = "刷新互动信息")
    public CommonResult<SingleMissionRespVO> refreshNote(@Valid @RequestBody RefreshNoteDetailReqVO reqVO) {
        SingleMissionRespVO singleMissionRespVO = singleMissionService.refreshNote(reqVO);
        return CommonResult.success(singleMissionRespVO);
    }

    @DeleteMapping("/batch/delete")
    @Operation(summary = "批量删除", description = "批量删除")
    public CommonResult<Boolean> batchDelete(@RequestBody List<String> uids) {
        singleMissionService.batchDelete(uids);
        return CommonResult.success(true);
    }

}
