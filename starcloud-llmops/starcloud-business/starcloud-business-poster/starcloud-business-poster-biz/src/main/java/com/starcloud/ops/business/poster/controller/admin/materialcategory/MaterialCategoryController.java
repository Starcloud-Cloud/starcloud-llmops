package com.starcloud.ops.business.poster.controller.admin.materialcategory;

import cn.hutool.core.lang.tree.TreeUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryListReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategoryRespVO;
import com.starcloud.ops.business.poster.controller.admin.materialcategory.vo.MaterialCategorySaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialcategory.MaterialCategoryDO;
import com.starcloud.ops.business.poster.service.materialcategory.MaterialCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 素材分类")
@RestController
@RequestMapping("/llm/poster/material-category")
@Validated
public class MaterialCategoryController {

    @Resource
    private MaterialCategoryService materialCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建素材分类")
    // @PreAuthorize("@ss.hasPermission('poster:material-category:create')")
    public CommonResult<Long> createMaterialCategory(@Valid @RequestBody MaterialCategorySaveReqVO createReqVO) {
        return success(materialCategoryService.createMaterialCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材分类")
    // @PreAuthorize("@ss.hasPermission('poster:material-category:update')")
    public CommonResult<Boolean> updateMaterialCategory(@Valid @RequestBody MaterialCategorySaveReqVO updateReqVO) {
        materialCategoryService.updateMaterialCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材分类")
    @Parameter(name = "id", description = "编号", required = true)
    // @PreAuthorize("@ss.hasPermission('poster:material-category:delete')")
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


    @GetMapping("/list")
    @Operation(summary = "获得素材分类列表")
    @PermitAll
//    @PreAuthorize("@ss.hasPermission('product:category:query')")
    public CommonResult<List<MaterialCategoryRespVO>> getCategoryList(@Valid MaterialCategoryListReqVO treeListReqVO) {
        List<MaterialCategoryDO> list = materialCategoryService.getEnableCategoryList(treeListReqVO);
        list.sort(Comparator.comparing(MaterialCategoryDO::getSort));
        return success(BeanUtils.toBean(list, MaterialCategoryRespVO.class));
    }



    @GetMapping("/u/list")
    @PermitAll
    @Operation(summary = "系统会员 - 获得素材分类列表")
    public CommonResult<List<MaterialCategoryRespVO>> getProductCategoryList() {
        List<MaterialCategoryDO> list = materialCategoryService.getEnableCategoryList();
        list.sort(Comparator.comparing(MaterialCategoryDO::getSort));
        return success(BeanUtils.toBean(list, MaterialCategoryRespVO.class));
    }


}