package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnBatchSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 素材知识库表格信息")
@RestController
@RequestMapping("/llm/material-library-table-column")
@Validated
public class MaterialLibraryTableColumnController {

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @PostMapping("/create")
    @Operation(summary = "创建素材知识库表格信息")
    public CommonResult<Long> createMaterialLibraryTableColumn(@Valid @RequestBody MaterialLibraryTableColumnSaveReqVO createReqVO) {
        return success(materialLibraryTableColumnService.createMaterialLibraryTableColumn(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材知识库表格信息")
    public CommonResult<Boolean> updateMaterialLibraryTableColumn(@Valid @RequestBody MaterialLibraryTableColumnSaveReqVO updateReqVO) {
        materialLibraryTableColumnService.updateMaterialLibraryTableColumn(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材知识库表格信息")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterialLibraryTableColumn(@RequestParam("id") Long id) {
        materialLibraryTableColumnService.deleteMaterialLibraryTableColumn(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得素材知识库表格信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialLibraryTableColumnRespVO> getMaterialLibraryTableColumn(@RequestParam("id") Long id) {
        MaterialLibraryTableColumnDO materialLibraryTableColumn = materialLibraryTableColumnService.getMaterialLibraryTableColumn(id);
        return success(BeanUtils.toBean(materialLibraryTableColumn, MaterialLibraryTableColumnRespVO.class));
    }


    @GetMapping("/update-batch")
    @Operation(summary = "批量更新表头数据")
    public CommonResult<Boolean> updateBatch(@Valid MaterialLibraryTableColumnBatchSaveReqVO batchSaveReqVO) {
        materialLibraryTableColumnService.updateBatchByLibraryId(batchSaveReqVO);
        return success(true);
    }


    // @GetMapping("/export-excel")
    // @Operation(summary = "导出素材知识库表格信息 Excel")
    // @OperateLog(type = EXPORT)
    // public void exportMaterialLibraryTableColumnExcel(@Valid MaterialLibraryTableColumnPageReqVO pageReqVO,
    //           HttpServletResponse response) throws IOException {
    //     pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
    //     List<MaterialLibraryTableColumnDO> list = materialLibraryTableColumnService.getMaterialLibraryTableColumnPage(pageReqVO).getList();
    //     // 导出 Excel
    //     ExcelUtils.write(response, "素材知识库表格信息.xls", "数据", MaterialLibraryTableColumnRespVO.class,
    //                     BeanUtils.toBean(list, MaterialLibraryTableColumnRespVO.class));
    // }

}