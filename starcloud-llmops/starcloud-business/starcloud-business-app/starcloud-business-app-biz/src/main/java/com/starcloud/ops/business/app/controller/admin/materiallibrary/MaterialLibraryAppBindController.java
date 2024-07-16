package com.starcloud.ops.business.app.controller.admin.materiallibrary;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindPageReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindRespVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind.MaterialLibraryAppBindSaveReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.MaterialLibraryRespVO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryAppBindDO;
import com.starcloud.ops.business.app.dal.databoject.materiallibrary.MaterialLibraryDO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryAppBindService;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 应用素材绑定")
@RestController
@RequestMapping("/llm/material-library-app-bind")
@Validated
public class MaterialLibraryAppBindController {

    @Resource
    @Lazy
    private MaterialLibraryService materialLibraryService;

    @Resource
    private MaterialLibraryAppBindService materialLibraryAppBindService;

    @PostMapping("/create")
    @Operation(summary = "创建应用素材绑定")
    public CommonResult<Long> createMaterialLibraryAppBind(@Valid @RequestBody MaterialLibraryAppBindSaveReqVO createReqVO) {
        return success(materialLibraryAppBindService.createMaterialLibraryAppBind(createReqVO));
    }

    @PostMapping("/update")
    @Operation(summary = "更新应用素材绑定")
    public CommonResult<Boolean> updateMaterialLibraryAppBind(@Valid @RequestBody MaterialLibraryAppBindSaveReqVO updateReqVO) {
        materialLibraryAppBindService.updateMaterialLibraryAppBind(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用素材绑定")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterialLibraryAppBind(@RequestParam("id") Long id) {
        materialLibraryAppBindService.deleteMaterialLibraryAppBind(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用素材绑定")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialLibraryAppBindRespVO> getMaterialLibraryAppBind(@RequestParam("id") Long id) {
        MaterialLibraryAppBindDO materialLibraryAppBind = materialLibraryAppBindService.getMaterialLibraryAppBind(id);
        return success(BeanUtils.toBean(materialLibraryAppBind, MaterialLibraryAppBindRespVO.class));
    }


    @PostMapping("/list")
    @Operation(summary = "获得应用素材绑定列表")
    public CommonResult<List<MaterialLibraryRespVO>> getMaterialLibraryAppBind(@RequestParam("appUid") String appUid) {
        List<MaterialLibraryAppBindDO> bindList = materialLibraryAppBindService.getBindList(appUid);

        if (bindList.isEmpty()) {
            return success(Collections.emptyList());
        }
        ArrayList<MaterialLibraryDO> list = new ArrayList<>();
        bindList.forEach(bind -> list.add(materialLibraryService.getMaterialLibrary(bind.getLibraryId())));

        return success(BeanUtils.toBean(list, MaterialLibraryRespVO.class));
    }


    @PostMapping("/page")
    @Operation(summary = "获得应用素材绑定分页")
    public CommonResult<PageResult<MaterialLibraryRespVO>> getMaterialLibraryAppBindPage(@Valid @RequestBody MaterialLibraryAppBindPageReqVO pageReqVO) {
        PageResult<MaterialLibraryAppBindDO> pageResult = materialLibraryAppBindService.getMaterialLibraryAppBindPage(pageReqVO);

        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }

        PageResult<MaterialLibraryRespVO>  libraryRespVOPageResult = new PageResult<>();
        ArrayList<MaterialLibraryDO> list = new ArrayList<>();
        pageResult.getList().forEach(bind -> list.add(materialLibraryService.getMaterialLibrary(bind.getLibraryId())));

        libraryRespVOPageResult.setList(BeanUtils.toBean(list, MaterialLibraryRespVO.class));
        libraryRespVOPageResult.setTotal( pageResult.getTotal() );

        return success(libraryRespVOPageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出应用素材绑定 Excel")
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