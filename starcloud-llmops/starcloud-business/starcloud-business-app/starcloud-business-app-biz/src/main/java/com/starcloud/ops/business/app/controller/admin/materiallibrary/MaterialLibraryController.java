package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.*;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryTableColumnDO;
import com.starcloud.ops.business.app.enums.materiallibrary.MaterialFormatTypeEnum;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
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
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_ID_EMPTY;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MATERIAL_LIBRARY_NOT_EXISTS;


@Tag(name = "管理后台 - 素材知识库")
@RestController
@RequestMapping("/llm/material-library")
@Validated
public class MaterialLibraryController {

    @Resource
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @Resource
    private MaterialLibraryTableColumnService materialLibraryTableColumnService;

    @Resource
    private MaterialLibrarySliceService materialLibrarySliceService;

    @Resource
    private AdminUserApi adminUserApi;

    @PostMapping("/page")
    @Operation(summary = "获得素材知识库分页")
    public CommonResult<PageResult<MaterialLibraryPageRespVO>> getMaterialLibraryPage(@Valid @RequestBody MaterialLibraryPageReqVO pageReqVO) {
        if (Objects.nonNull(pageReqVO.getCreateName())) {
            AdminUserRespDTO user = adminUserApi.getUserByUsername(pageReqVO.getCreateName());
            pageReqVO.setCreator(user.getId());
        }

        PageResult<MaterialLibraryPageRespVO> pageResult = materialLibraryService.getMaterialLibraryPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }


        return success(pageResult);
    }

    @PostMapping("/create")
    @Operation(summary = "创建素材知识库")
    public CommonResult<Long> createMaterialLibrary(@Valid @RequestBody MaterialLibrarySaveReqVO createReqVO) {
        return success(materialLibraryService.createSystemMaterialLibrary(createReqVO));
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

        if (materialLibrary == null) {
            throw exception(MATERIAL_LIBRARY_NOT_EXISTS);
        }
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
        if (Objects.isNull(uid)) {
            throw exception(MATERIAL_LIBRARY_ID_EMPTY);
        }

        return success(materialLibraryService.getMaterialLibraryByUid(uid));
    }


    @PostMapping("/get-app-uid")
    @Operation(summary = "通过应用UID获得素材知识库")
    @Parameter(name = "appUid", description = "应用编号", required = true, example = "1024")
    public CommonResult<MaterialLibraryRespVO> getMaterialLibraryByAppUid(@RequestParam("appUid") String appUid) {
        return success(materialLibraryService.getMaterialLibraryByApp(new MaterialLibraryAppReqVO().setAppUid(appUid)));
    }

    @PostMapping("/import")
    @Operation(summary = "导入数据")
    @OperateLog(enable = false)
    public CommonResult<Boolean> importMaterialData(@Valid MaterialLibraryImportReqVO importRespVO) {
        materialLibraryService.importMaterialData(importRespVO);
        return success(true);
    }


    @GetMapping("/export-template")
    @Operation(summary = "导出模板")
    @OperateLog(enable = false)
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public void exportTemplate(@RequestParam("id") Long id, HttpServletResponse response) {
        materialLibraryService.exportTemplate(id, response);
    }


    @PostMapping("/update-plugin-Config")
    @Operation(summary = "更新素材库插件配置")
    public CommonResult<Boolean> updatePluginConfig(@Valid @RequestBody MaterialLibrarySavePlugInConfigReqVO plugInConfigReqVO) {
        materialLibraryService.updatePluginConfig(getLoginUserId(), plugInConfigReqVO);
        return success(true);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制素材库")
    public CommonResult<Boolean> materialLibraryCopy(@Valid @RequestBody MaterialLibraryCopyReqVO copyReqVO) {
        materialLibraryService.materialLibraryCopy(copyReqVO);
        return success(true);
    }


}