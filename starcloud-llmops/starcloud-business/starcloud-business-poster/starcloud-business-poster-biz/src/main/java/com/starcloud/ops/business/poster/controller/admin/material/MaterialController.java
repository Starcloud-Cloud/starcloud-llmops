package com.starcloud.ops.business.poster.controller.admin.material;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialRespVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.service.material.MaterialService;
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


@Tag(name = "管理后台 - 海报素材")
@RestController
@RequestMapping("/llm/poster/material")
@Validated
public class MaterialController {

    @Resource
    private MaterialService materialService;


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

// ======================Member======================

    @PostMapping("u/create")
    @Operation(summary = "创建海报素材")
    public CommonResult<Long> createMaterial(@Valid @RequestBody MaterialSaveReqVO createReqVO) {
        return success(materialService.createMaterial(createReqVO));
    }
    @PutMapping("u/update")
    @Operation(summary = "更新海报素材")
    @PreAuthorize("@ss.hasPermission('poster:material:update')")
    public CommonResult<Boolean> updateMaterial(@Valid @RequestBody MaterialSaveReqVO updateReqVO) {
        materialService.updateMaterial(updateReqVO);
        return success(true);
    }
    @GetMapping("u/get")
    @Operation(summary = "获得海报素材")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialRespVO> getMaterial(@RequestParam("id") Long id) {
        MaterialDO material = materialService.getMaterial(id);
        return success(BeanUtils.toBean(material, MaterialRespVO.class));
    }

    @GetMapping("u/page")
    @Operation(summary = "获得海报素材分页")
    @PreAuthorize("@ss.hasPermission('poster:material:query')")
    public CommonResult<PageResult<MaterialRespVO>> getMaterialPage(@Valid MaterialPageReqVO pageReqVO) {
        PageResult<MaterialDO> pageResult = materialService.getMaterialPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialRespVO.class));
    }

    @DeleteMapping("u/delete")
    @Operation(summary = "删除海报素材")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('poster:material:delete')")
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        materialService.deleteMaterial(id);
        return success(true);
    }

}