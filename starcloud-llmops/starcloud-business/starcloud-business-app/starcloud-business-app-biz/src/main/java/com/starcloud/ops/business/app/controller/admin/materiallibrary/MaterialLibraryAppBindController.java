package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 应用素材绑定")
@RestController
@RequestMapping("/llm/material-library-app-bind")
@Validated
public class MaterialLibraryAppBindController {

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @PostMapping("/create")
    @Operation(summary = "创建应用素材绑定")
    @PreAuthorize("@ss.hasPermission('llm:material-library-app-bind:create')")
    public CommonResult<Long> createMaterialLibraryAppBind(@Valid @RequestBody MaterialLibraryAppBindSaveReqVO createReqVO) {
        return success(materialLibraryAppBindService.createMaterialLibraryAppBind(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用素材绑定")
    @PreAuthorize("@ss.hasPermission('llm:material-library-app-bind:update')")
    public CommonResult<Boolean> updateMaterialLibraryAppBind(@Valid @RequestBody MaterialLibraryAppBindSaveReqVO updateReqVO) {
        materialLibraryAppBindService.updateMaterialLibraryAppBind(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用素材绑定")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('llm:material-library-app-bind:delete')")
    public CommonResult<Boolean> deleteMaterialLibraryAppBind(@RequestParam("id") Long id) {
        materialLibraryAppBindService.deleteMaterialLibraryAppBind(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用素材绑定")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('llm:material-library-app-bind:query')")
    public CommonResult<MaterialLibraryAppBindRespVO> getMaterialLibraryAppBind(@RequestParam("id") Long id) {
        MaterialLibraryAppBindDO materialLibraryAppBind = materialLibraryAppBindService.getMaterialLibraryAppBind(id);
        return success(BeanUtils.toBean(materialLibraryAppBind, MaterialLibraryAppBindRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用素材绑定分页")
    @PreAuthorize("@ss.hasPermission('llm:material-library-app-bind:query')")
    public CommonResult<PageResult<MaterialLibraryAppBindRespVO>> getMaterialLibraryAppBindPage(@Valid MaterialLibraryAppBindPageReqVO pageReqVO) {
        PageResult<MaterialLibraryAppBindDO> pageResult = materialLibraryAppBindService.getMaterialLibraryAppBindPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialLibraryAppBindRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用素材绑定 Excel")
    @PreAuthorize("@ss.hasPermission('llm:material-library-app-bind:export')")
    @OperateLog(type = EXPORT)
    public void exportMaterialLibraryAppBindExcel(@Valid MaterialLibraryAppBindPageReqVO pageReqVO,
                                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MaterialLibraryAppBindDO> list = materialLibraryAppBindService.getMaterialLibraryAppBindPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "应用素材绑定.xls", "数据", MaterialLibraryAppBindRespVO.class,
                BeanUtils.toBean(list, MaterialLibraryAppBindRespVO.class));
    }

}