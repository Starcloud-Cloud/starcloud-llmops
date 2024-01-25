package com.starcloud.ops.business.listing.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import com.starcloud.ops.business.listing.controller.admin.vo.request.*;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftDetailExcelVO;
import com.starcloud.ops.business.listing.controller.admin.vo.response.DraftRespVO;
import com.starcloud.ops.business.listing.service.DraftService;
import com.starcloud.ops.framework.common.api.util.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
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

    @PutMapping("/save")
    @Operation(summary = "保存草稿", description = "保存草稿")
    public CommonResult<DraftRespVO> save(@Valid @RequestBody DraftReqVO saveReqVO) {
        DraftRespVO draftRespVO = draftService.saveDraftVersion(saveReqVO);
        return CommonResult.success(draftRespVO);
    }

    @PostMapping("/clone")
    @Operation(summary = "克隆模板", description = "克隆模板")
    public CommonResult<DraftRespVO> importTemplate(@Valid @RequestBody DraftOperationReqVO reqVO) throws IOException {
        DraftRespVO draftRespVO = draftService.cloneDraft(reqVO);
        return CommonResult.success(draftRespVO);
    }

    @PostMapping("/export")
    @Operation(summary = "导出草稿", description = "导出草稿")
    public void export(@Valid @RequestBody List<Long> ids, HttpServletResponse response) throws IOException {
        List<DraftDetailExcelVO> export = draftService.export(ids);
        String prefix = DateUtil.formatNow();
        ExcelUtils.write(response, prefix + "-导出草稿.xls", "草稿", DraftDetailExcelVO.class, export);
    }

    @PostMapping("/delete")
    @Operation(summary = "删除草稿", description = "删除草稿")
    public CommonResult<Boolean> delete(@RequestBody List<Long> ids) {
        draftService.delete(ids);
        return CommonResult.success(true);
    }

    @PutMapping("/execute")
    @Operation(summary = "批量执行", description = "批量执行")
    public CommonResult<Boolean> execute(@RequestBody List<Long> ids) {
        draftService.batchExecute(ids);
        return CommonResult.success(true);
    }

    @PostMapping("/key/add")
    @Operation(summary = "新增关键词", description = "新增关键词")
    public CommonResult<Boolean> addKeyword(@Valid @RequestBody DraftOperationReqVO reqVO) {
        draftService.addKeyword(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/key/remove")
    @Operation(summary = "删除关键词", description = "删除关键词")
    public CommonResult<Boolean> removeKeyword(@Valid @RequestBody DraftOperationReqVO reqVO) {
        draftService.removeKeyword(reqVO);
        return CommonResult.success(true);
    }

    @PutMapping("/import/dict")
    @Operation(summary = "导入词库中的关键词", description = "导入词库中的关键词")
    public CommonResult<DraftRespVO> importDict(@Valid @RequestBody ImportDictReqVO reqVO) {
        DraftRespVO respVO = draftService.importDict(reqVO);
        return CommonResult.success(respVO);
    }

    @PutMapping("/recommend")
    @Operation(summary = "智能推荐", description = "智能推荐")
    public CommonResult<String> searchTermRecommend(@RequestBody DraftReqVO reqVO) {
        String recommend = draftService.searchTermRecommend(reqVO);
        return CommonResult.success(recommend);
    }


    @PutMapping("/score")
    @Operation(summary = "计算分值", description = "计算分值")
    public CommonResult<DraftRespVO> score(@RequestBody DraftReqVO reqVO) {
        DraftRespVO score = draftService.score(reqVO);
        return CommonResult.success(score);
    }

    @PutMapping("/refresh/{uid}/{version}")
    @Operation(summary = "刷新搜索量", description = "刷新搜索量")
    public CommonResult<Boolean> refresh(@PathVariable("uid") String uid,
                                             @PathVariable("version") Integer version) {
        draftService.refresh(uid, version);
        return CommonResult.success(true);
    }

}
