package com.starcloud.ops.biz.controller.admin.materialcategory;

import com.starcloud.ops.biz.controller.admin.materialcategory.vo.MaterialCategoryPageReqVO;
import com.starcloud.ops.biz.controller.admin.materialcategory.vo.MaterialCategoryRespVO;
import com.starcloud.ops.biz.controller.admin.materialcategory.vo.MaterialCategorySaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.materialcategory.MaterialCategoryDO;
import com.starcloud.ops.biz.service.materialcategory.MaterialCategoryService;
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


@Tag(name = "管理后台 - 素材分类")
@RestController
@RequestMapping("/poster/material-category")
@Validated
public class MaterialCategoryController {

    @Resource
    private MaterialCategoryService materialCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建素材分类")
    @PreAuthorize("@ss.hasPermission('poster:material-category:create')")
    public CommonResult<Long> createMaterialCategory(@Valid @RequestBody MaterialCategorySaveReqVO createReqVO) {
        return success(materialCategoryService.createMaterialCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材分类")
    @PreAuthorize("@ss.hasPermission('poster:material-category:update')")
    public CommonResult<Boolean> updateMaterialCategory(@Valid @RequestBody MaterialCategorySaveReqVO updateReqVO) {
        materialCategoryService.updateMaterialCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('poster:material-category:delete')")
    public CommonResult<Boolean> deleteMaterialCategory(@RequestParam("id") Long id) {
        materialCategoryService.deleteMaterialCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得素材分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('poster:material-category:query')")
    public CommonResult<MaterialCategoryRespVO> getMaterialCategory(@RequestParam("id") Long id) {
        MaterialCategoryDO materialCategory = materialCategoryService.getMaterialCategory(id);
        return success(BeanUtils.toBean(materialCategory, MaterialCategoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得素材分类分页")
    @PreAuthorize("@ss.hasPermission('poster:material-category:query')")
    public CommonResult<PageResult<MaterialCategoryRespVO>> getMaterialCategoryPage(@Valid MaterialCategoryPageReqVO pageReqVO) {
        PageResult<MaterialCategoryDO> pageResult = materialCategoryService.getMaterialCategoryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialCategoryRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出素材分类 Excel")
    @PreAuthorize("@ss.hasPermission('poster:material-category:export')")
    @OperateLog(type = EXPORT)
    public void exportMaterialCategoryExcel(@Valid MaterialCategoryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MaterialCategoryDO> list = materialCategoryService.getMaterialCategoryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "素材分类.xls", "数据", MaterialCategoryRespVO.class,
                        BeanUtils.toBean(list, MaterialCategoryRespVO.class));
    }

}