package com.starcloud.ops.business.poster.controller.admin.material;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialRespVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSaveReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialSimpleRespVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.service.material.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "根据Uid获取海报素材")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialSimpleRespVO> getMaterial(@RequestParam("uid") String uid) {
        MaterialDO material = materialService.getMaterialByUId(uid);
        return success(BeanUtils.toBean(material, MaterialSimpleRespVO.class));
    }

    @GetMapping("u/page")
    @Operation(summary = "获得海报素材分页")
    @PreAuthorize("@ss.hasPermission('poster:material:query')")
    public CommonResult<PageResult<MaterialRespVO>> getMaterialPage(@Valid MaterialPageReqVO pageReqVO) {
        PageResult<MaterialDO> pageResult = materialService.getMaterialPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialRespVO.class));
    }

    @DataPermission(enable = false)
    @GetMapping("u/posterTemplate")
    @Operation(summary = "根据分组获取海报列表")
    public CommonResult<PosterTemplateDTO> listByGroup(@RequestParam("templateId") String templateId) {
        return success(materialService.posterTemplate(templateId));
    }

    @DataPermission(enable = false)
    @GetMapping("u/listPosterTemplateByGroup")
    @Operation(summary = "根据分组获取海报列表")
    public CommonResult<List<PosterTemplateDTO>> listPosterTemplateByGroup(@RequestParam("group") Long group) {
        return success(materialService.listPosterTemplateByGroup(group));
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