package com.starcloud.ops.business.listing.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import com.starcloud.ops.business.listing.controller.admin.vo.request.*;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.service.DraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/listing/draft")
@Tag(name = "listing草稿管理", description = "listing草稿管理")
public class ListingDraftController {

    @Resource
    private DraftService draftService;

    @PutMapping("/create")
    @Operation(summary = "新建草稿", description = "新建草稿")
    public CommonResult<DraftRespVO> create(@Valid @RequestBody DraftCreateReqVO reqVO) {
        DraftRespVO draftRespVO = draftService.create(reqVO);
        return CommonResult.success(draftRespVO);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询草稿列表", description = "分页查询草稿列表")
    public CommonResult<PageResult<DraftRespVO>> page(@Valid DraftPageReqVO draftPageReqVO) {
        PageResult<DraftRespVO> resp = draftService.getDraftPage(draftPageReqVO);
        return CommonResult.success(resp);
    }

    @GetMapping("/versions/{uid}")
    @Operation(summary = "查询草稿版本列表", description = "查询草稿版本列表")
    public CommonResult<List<DraftRespVO>> versions(@PathVariable("uid") String uid) {
        List<DraftRespVO> draftResp = draftService.listVersion(uid);
        return CommonResult.success(draftResp);
    }

    @GetMapping("/detail/{uid}/{version}")
    @Operation(summary = "查询草稿明细", description = "查询草稿明细")
    public CommonResult<DraftRespVO> detail(@PathVariable("uid") String uid,
                                            @PathVariable("version") Integer version) {
        DraftRespVO draftResp = draftService.detail(uid, version);
        return CommonResult.success(draftResp);
    }

    @GetMapping("/import/temp")
    @Operation(summary = "导入模板", description = "导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtils.write(response, "导入模板.xls", "草稿模板", DraftDetailExcelVO.class, Collections.emptyList());
    }

    @GetMapping("/export")
    @Operation(summary = "导出最新版草稿", description = "导出最新版草稿")
    public void export(@Valid @RequestBody List<DraftOperationReqVO> operationReq, HttpServletResponse response) throws IOException {
        List<DraftDetailExcelVO> export = draftService.export(operationReq);
        ExcelUtils.write(response, "导出草稿.xls", "草稿", DraftDetailExcelVO.class, export);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除草稿", description = "删除草稿")
    public CommonResult<Boolean> delete(@RequestBody List<DraftOperationReqVO> operationReq) {
        draftService.delete(operationReq);
        return CommonResult.success(true);
    }


    @PostMapping("/add")
    @Operation(summary = "新增关键词", description = "新增关键词")
    public void addKeyword(@RequestBody DraftSaveReqVO reqVO) {
        draftService.addKeyword(reqVO);
    }


    // 删除关键词



}
