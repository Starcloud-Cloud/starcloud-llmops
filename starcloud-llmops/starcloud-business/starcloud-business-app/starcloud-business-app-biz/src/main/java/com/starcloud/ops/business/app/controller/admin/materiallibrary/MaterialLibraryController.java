package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryImportReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibrarySaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 素材知识库")
@RestController
@RequestMapping("/llm/material-library")
@Validated
public class MaterialLibraryController {

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @GetMapping("/page")
    @Operation(summary = "获得素材知识库分页")
    public CommonResult<PageResult<MaterialLibraryRespVO>> getMaterialLibraryPage(@Valid MaterialLibraryPageReqVO pageReqVO) {
        PageResult<MaterialLibraryDO> pageResult = materialLibraryService.getMaterialLibraryPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialLibraryRespVO.class));
    }

    @PostMapping("/create")
    @Operation(summary = "创建素材知识库")
    public CommonResult<Long> createMaterialLibrary(@Valid @RequestBody MaterialLibrarySaveReqVO createReqVO) {
        return success(materialLibraryService.createMaterialLibrary(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新素材知识库")
    public CommonResult<Boolean> updateMaterialLibrary(@Valid @RequestBody MaterialLibrarySaveReqVO updateReqVO) {
        materialLibraryService.updateMaterialLibrary(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除素材知识库")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterialLibrary(@RequestParam("id") Long id) {
        materialLibraryService.deleteMaterialLibrary(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得素材知识库")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialLibraryRespVO> getMaterialLibrary(@RequestParam("id") Long id) {
        MaterialLibraryDO materialLibrary = materialLibraryService.getMaterialLibrary(id);
        // 数据转换
        MaterialLibraryRespVO bean = BeanUtils.toBean(materialLibrary, MaterialLibraryRespVO.class);

        if (MaterialFormatTypeEnum.isExcel(materialLibrary.getFormatType())) {
            List<MaterialLibraryTableColumnDO> tableColumnDOList = materialLibraryTableColumnService.getMaterialLibraryTableColumnByLibrary(id);
            bean.setTableMeta(BeanUtils.toBean(tableColumnDOList, MaterialLibraryTableColumnRespVO.class));
        }
        return success(bean);
    }


    @PostMapping("/import")
    @Operation(summary = "导入数据")
    @OperateLog(enable = false)
    @PermitAll
    public CommonResult<Boolean> importMaterialData(@Valid MaterialLibraryImportReqVO importRespVO) {
        materialLibraryService.importMaterialData(importRespVO);
        return success(true);
    }


}