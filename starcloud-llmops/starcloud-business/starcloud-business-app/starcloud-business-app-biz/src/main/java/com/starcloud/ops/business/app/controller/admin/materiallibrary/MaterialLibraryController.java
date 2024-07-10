package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.*;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceAppReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice.MaterialLibrarySliceUseRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibrarySliceService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryTableColumnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;


@Tag(name = "管理后台 - 素材知识库")
@RestController
@RequestMapping("/llm/material-library")
@Validated
public class MaterialLibraryController {

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Resource
    private AdminUserApi adminUserApi;

    @GetMapping("/page")
    @Operation(summary = "获得素材知识库分页")
    public CommonResult<PageResult<MaterialLibraryPageRespVO>> getMaterialLibraryPage(@Valid MaterialLibraryPageReqVO pageReqVO) {
        PageResult<MaterialLibraryDO> pageResult = materialLibraryService.getMaterialLibraryPage(pageReqVO);
        PageResult<MaterialLibraryPageRespVO> bean = BeanUtils.toBean(pageResult, MaterialLibraryPageRespVO.class);
        String nickname = adminUserApi.getUser(getLoginUserId()).getNickname();
        bean.getList().forEach(reqVO -> {
            reqVO.setFileCount(materialLibrarySliceService.getSliceDataCountByLibraryId(reqVO.getId()));
            reqVO.setCreateName(nickname);
        });

        return success(bean);
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

    @GetMapping("/get-uid")
    @Operation(summary = "通过 UID获得素材知识库")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialLibraryRespVO> getMaterialLibraryByUid(@RequestParam("uid") String uid) {
        return success(materialLibraryService.getMaterialLibraryByUid(uid));
    }


    @PostMapping("/import")
    @Operation(summary = "导入数据")
    @OperateLog(enable = false)
    public CommonResult<Boolean> importMaterialData(@Valid MaterialLibraryImportReqVO importRespVO) {
        materialLibraryService.importMaterialData(importRespVO);
        return success(true);
    }


    @PostMapping("/export-template")
    @Operation(summary = "导出模板")
    @OperateLog(enable = false)
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public void exportTemplate(@RequestParam("id") Long id, HttpServletResponse response) {
        materialLibraryService.exportTemplate(id, response);
    }


    @PostMapping("/test")
    @Operation(summary = "测试")
    @OperateLog(enable = false)
    public void exportTemplate(@Valid @RequestBody MaterialLibraryTestReqVO testReqVO) {
        materialLibraryService.materialLibraryDataMigration(testReqVO.getName(), testReqVO.getSaveReqVOS(), testReqVO.getMaterialList());
    }


    @PostMapping("/test2")
    @Operation(summary = "测试")
    @OperateLog(enable = false)
    public CommonResult<List<MaterialLibrarySliceUseRespVO>> exportTemplate2(@Valid @RequestBody List<MaterialLibrarySliceAppReqVO> appReqVO) {
        return success(materialLibraryService.getMaterialLibrarySliceList(appReqVO));
    }


}