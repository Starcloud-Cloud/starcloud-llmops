package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
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


@Tag(name = "管理后台 - 素材知识库表格信息")
@RestController
@RequestMapping("/llm/material-library-table-column")
@Validated
public class MaterialLibraryTableColumnController {

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @PostMapping("/create")
    @Operation(summary = "创建素材知识库表格信息")
    @PreAuthorize("@ss.hasPermission('llm:material-library-table-column:create')")
    public CommonResult<Long> createMaterialLibraryTableColumn(@Valid @RequestBody MaterialLibraryTableColumnSaveReqVO createReqVO) {
        return success(materialLibraryTableColumnService.createMaterialLibraryTableColumn(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材知识库表格信息")
    @PreAuthorize("@ss.hasPermission('llm:material-library-table-column:update')")
    public CommonResult<Boolean> updateMaterialLibraryTableColumn(@Valid @RequestBody MaterialLibraryTableColumnSaveReqVO updateReqVO) {
        materialLibraryTableColumnService.updateMaterialLibraryTableColumn(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材知识库表格信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('llm:material-library-table-column:delete')")
    public CommonResult<Boolean> deleteMaterialLibraryTableColumn(@RequestParam("id") Long id) {
        materialLibraryTableColumnService.deleteMaterialLibraryTableColumn(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得素材知识库表格信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('llm:material-library-table-column:query')")
    public CommonResult<MaterialLibraryTableColumnRespVO> getMaterialLibraryTableColumn(@RequestParam("id") Long id) {
        MaterialLibraryTableColumnDO materialLibraryTableColumn = materialLibraryTableColumnService.getMaterialLibraryTableColumn(id);
        return success(BeanUtils.toBean(materialLibraryTableColumn, MaterialLibraryTableColumnRespVO.class));
    }



    @GetMapping("/export-excel")
    @Operation(summary = "导出素材知识库表格信息 Excel")
    @OperateLog(type = EXPORT)
    public void exportMaterialLibraryTableColumnExcel(@Valid MaterialLibraryTableColumnPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MaterialLibraryTableColumnDO> list = materialLibraryTableColumnService.getMaterialLibraryTableColumnPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "素材知识库表格信息.xls", "数据", MaterialLibraryTableColumnRespVO.class,
                        BeanUtils.toBean(list, MaterialLibraryTableColumnRespVO.class));
    }

}