package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySlicePageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceShareReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibrarySliceDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 素材知识库数据")
@RestController
@RequestMapping("/llm/material-library-slice")
@Validated
public class MaterialLibrarySliceController {

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @PostMapping("/create")
    @Operation(summary = "创建素材知识库数据")
    public CommonResult<Long> createMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceSaveReqVO createReqVO) {
        return success(materialLibrarySliceService.createMaterialLibrarySlice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材知识库数据")
    public CommonResult<Boolean> updateMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceSaveReqVO updateReqVO) {
        materialLibrarySliceService.updateMaterialLibrarySlice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材知识库数据")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterialLibrarySlice(@RequestParam("id") Long id) {
        materialLibrarySliceService.deleteMaterialLibrarySlice(id);
        return success(true);
    }


    @DeleteMapping("/share")
    @Operation(summary = "设置可以分享的的素材")
    public CommonResult<Boolean> deleteMaterialLibrarySlice(@Valid @RequestBody MaterialLibrarySliceShareReqVO shareReqVO) {
        materialLibrarySliceService.updateSliceShareStatus(shareReqVO);
        return success(true);
    }


    @GetMapping("/page")
    @Operation(summary = "获得素材知识库数据分页")
    public CommonResult<PageResult<MaterialLibrarySliceRespVO>> getMaterialLibrarySlicePage(@Valid MaterialLibrarySlicePageReqVO pageReqVO) {
        PageResult<MaterialLibrarySliceDO> pageResult = materialLibrarySliceService.getMaterialLibrarySlicePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialLibrarySliceRespVO.class));
    }


}