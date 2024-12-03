package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.*;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 素材知识库数据")
@RestController
@RequestMapping("/llm/material-library-slice")
@Validated
public class MaterialLibrarySliceController {


    @Resource
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @PostMapping("/create")
    @Operation(summary = "创建素材知识库数据")
    public CommonResult<Long> createMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceSaveReqVO createReqVO) {
        return success(materialLibrarySliceService.createMaterialLibrarySlice(createReqVO));
    }


    @PostMapping("/create-batch")
    @Operation(summary = "批量-创建素材知识库数据")
    public CommonResult< List<Long>> createBatchMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceBatchSaveReqVO batchSaveReqVO) {
        return success(materialLibrarySliceService.createBatchMaterialLibrarySlice(batchSaveReqVO));
    }


    @PutMapping("/update")
    @Operation(summary = "更新素材知识库数据")
    public CommonResult<Boolean> updateMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceSaveReqVO updateReqVO) {
        materialLibrarySliceService.updateMaterialLibrarySlice(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-batch")
    @Operation(summary = "批量-更新素材知识库数据")
    public CommonResult<Boolean> updateBatchMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceBatchSaveReqVO batchUpdateReqVO) {
        materialLibrarySliceService.updateBatchMaterialLibrarySlice(batchUpdateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材知识库数据")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterialLibrarySlice(@RequestParam("id") Long id) {
        materialLibrarySliceService.deleteMaterialLibrarySlice(id);
        return success(true);
    }


    @PostMapping("/page")
    @Operation(summary = "获得素材知识库数据分页")
    public CommonResult<PageResult<MaterialLibrarySliceRespVO>> getMaterialLibrarySlicePage(@Valid @RequestBody MaterialLibrarySlicePageReqVO pageReqVO) {
        PageResult<MaterialLibrarySliceDO> pageResult = materialLibrarySliceService.getMaterialLibrarySlicePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialLibrarySliceRespVO.class));
    }


    @PostMapping("/page-uid")
    @Operation(summary = "获得素材库 UID 素材知识库数据分页")
    public CommonResult<PageResult<MaterialLibrarySliceRespVO>> getMaterialLibrarySlicePageByLibraryUid(@Valid @RequestBody MaterialLibrarySlicePageReqVO pageReqVO) {
        PageResult<MaterialLibrarySliceDO> pageResult = materialLibrarySliceService.getMaterialLibrarySlicePageByLibraryUid(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialLibrarySliceRespVO.class));
    }

    @PostMapping("/page-app-uid")
    @Operation(summary = "通过应用UID获得素材知识库数据分页")
    public CommonResult<PageResult<MaterialLibrarySliceRespVO>> getMaterialLibraryByAppUid(@Valid @RequestBody MaterialLibrarySliceAppPageReqVO appPageReqVO) {

        return success(materialLibrarySliceService.getMaterialLibrarySlicePageByApp(appPageReqVO));
    }


    @GetMapping("/list")
    @Operation(summary = "获得素材知识库数据列表")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<List<MaterialLibrarySliceRespVO>> getMaterialLibrarySliceList(@RequestParam("libraryId") Long libraryId) {
        List<MaterialLibrarySliceDO> librarySliceDOS = materialLibrarySliceService.getMaterialLibrarySliceByLibraryId(libraryId);
        return success(BeanUtils.toBean(librarySliceDOS, MaterialLibrarySliceRespVO.class));
    }


    @PostMapping("/delete-batch")
    @Operation(summary = "批量删除素材", description = "批量删除素材")
    public CommonResult<Boolean> delete(@RequestBody List<Long> ids) {
        materialLibrarySliceService.deleteBatch(ids);
        return CommonResult.success(true);
    }


    @PostMapping("/copy")
    @Operation(summary = "批量删除素材", description = "批量删除素材")
    public CommonResult<Boolean> copy(@RequestBody Long id) {
        return CommonResult.success(materialLibrarySliceService.copy(id));
    }



}