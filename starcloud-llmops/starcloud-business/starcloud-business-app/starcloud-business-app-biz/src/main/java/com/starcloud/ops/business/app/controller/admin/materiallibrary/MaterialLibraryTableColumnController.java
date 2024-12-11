package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        DataPermissionUtils.executeIgnore(() -> materialLibraryTableColumnService.deleteMaterialLibraryTableColumn(id));
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得素材知识库表格信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialLibraryTableColumnRespVO> getMaterialLibraryTableColumn(@RequestParam("id") Long id) {
        MaterialLibraryTableColumnDO materialLibraryTableColumn = materialLibraryTableColumnService.getMaterialLibraryTableColumn(id);
        return success(BeanUtils.toBean(materialLibraryTableColumn, MaterialLibraryTableColumnRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获取表头数据列表")
    public CommonResult<List<MaterialLibraryTableColumnRespVO>> list(@RequestParam("libraryId") Long libraryId) {
        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        AtomicReference<List<MaterialLibraryTableColumnRespVO>>  dataPermissionResult = new AtomicReference<>();
        DataPermissionUtils.executeIgnore(() -> {
            List<MaterialLibraryTableColumnRespVO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibraryAndName(libraryId);
            dataPermissionResult.set(tableColumnDOList);
        });
        return success(dataPermissionResult.get());
    }

    @PostMapping("/update-batch")
    @Operation(summary = "批量更新表头数据")
    public CommonResult<Boolean> updateBatch(@Valid @RequestBody MaterialLibraryTableColumnBatchSaveReqVO batchSaveReqVO) {
        // 关闭数据权限，避免因为没有数据权限，查询不到数据，进而导致唯一校验不正确
        DataPermissionUtils.executeIgnore(() -> {
            materialLibraryTableColumnService.updateBatchByLibraryId(batchSaveReqVO);
        });
        return success(true);
    }

    @PostMapping("/validate-switch-bind")
    @Operation(summary = "切换绑定表头校验")
    public CommonResult<Boolean> validateSwitchBind(@Valid @RequestBody MaterialLibraryAppBindSaveReqVO saveReqVO) {
        return success(materialLibraryTableColumnService.validateSwitchBind(saveReqVO));
    }
}