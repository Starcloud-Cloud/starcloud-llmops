package com.starcloud.ops.biz.controller.admin.material;

import com.starcloud.ops.biz.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.biz.controller.admin.material.vo.MaterialRespVO;
import com.starcloud.ops.biz.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.biz.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.biz.service.material.MaterialService;
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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;


@Tag(name = "管理后台 - 海报素材")
@RestController
@RequestMapping("/poster/material")
@Validated
public class MaterialController {

    @Resource
    private MaterialService materialService;

    @PostMapping("/create")
    @Operation(summary = "创建海报素材")
    @PreAuthorize("@ss.hasPermission('poster:material:create')")
    public CommonResult<Long> createMaterial(@Valid @RequestBody MaterialSaveReqVO createReqVO) {
        return success(materialService.createMaterial(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新海报素材")
    @PreAuthorize("@ss.hasPermission('poster:material:update')")
    public CommonResult<Boolean> updateMaterial(@Valid @RequestBody MaterialSaveReqVO updateReqVO) {
        materialService.updateMaterial(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除海报素材")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('poster:material:delete')")
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        materialService.deleteMaterial(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得海报素材")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('poster:material:query')")
    public CommonResult<MaterialRespVO> getMaterial(@RequestParam("id") Long id) {
        MaterialDO material = materialService.getMaterial(id);
        return success(BeanUtils.toBean(material, MaterialRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得海报素材分页")
    @PreAuthorize("@ss.hasPermission('poster:material:query')")
    public CommonResult<PageResult<MaterialRespVO>> getMaterialPage(@Valid MaterialPageReqVO pageReqVO) {
        PageResult<MaterialDO> pageResult = materialService.getMaterialPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出海报素材 Excel")
    @PreAuthorize("@ss.hasPermission('poster:material:export')")
    @OperateLog(type = EXPORT)
    public void exportMaterialExcel(@Valid MaterialPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MaterialDO> list = materialService.getMaterialPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "海报素材.xls", "数据", MaterialRespVO.class,
                        BeanUtils.toBean(list, MaterialRespVO.class));
    }

}