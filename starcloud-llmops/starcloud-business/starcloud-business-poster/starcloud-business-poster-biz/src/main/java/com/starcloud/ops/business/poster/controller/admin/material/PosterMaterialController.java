package com.starcloud.ops.business.poster.controller.admin.material;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialRespVO;
import com.starcloud.ops.business.poster.controller.admin.material.vo.PosterMaterialSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.PosterMaterialDO;
import com.starcloud.ops.business.poster.service.material.PosterMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 海报素材")
@RestController
@RequestMapping("/llm/poster/material")
@Validated
public class PosterMaterialController {

    @Resource
    private PosterMaterialService posterMaterialService;

    // @PostMapping("/create")
    // @Operation(summary = "创建海报素材")
    // @PreAuthorize("@ss.hasPermission('poster:material:create')")
    // public CommonResult<Long> createMaterial(@Valid @RequestBody PosterMaterialSaveReqVO createReqVO) {
    //     return success(posterMaterialService.createMaterial(createReqVO));
    // }

    @PutMapping("/update")
    @Operation(summary = "更新海报素材")
    @PreAuthorize("@ss.hasPermission('poster:material:update')")
    public CommonResult<Boolean> updateMaterial(@Valid @RequestBody PosterMaterialSaveReqVO updateReqVO) {
        posterMaterialService.updateMaterial(updateReqVO);
        return success(true);
    }



// ======================Member======================

    @PostMapping("u/create")
    @Operation(summary = "创建海报素材")
    public CommonResult<Long> createMaterial(@Valid @RequestBody PosterMaterialSaveReqVO createReqVO) {
        return success(posterMaterialService.createMaterial(createReqVO));
    }


    @GetMapping("u/get")
    @Operation(summary = "获得海报素材")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<PosterMaterialRespVO> getMaterial(@RequestParam("id") Long id) {
        PosterMaterialDO material = posterMaterialService.getMaterial(id);
        return success(BeanUtils.toBean(material, PosterMaterialRespVO.class));
    }

    @GetMapping("u/page")
    @Operation(summary = "获得海报素材分页")
    public CommonResult<PageResult<PosterMaterialRespVO>> getMaterialPage(@Valid PosterMaterialPageReqVO pageReqVO) {
        PageResult<PosterMaterialDO> pageResult = posterMaterialService.getPosterMaterialPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PosterMaterialRespVO.class));
    }
    @DeleteMapping("u/delete")
    @Operation(summary = "删除海报素材")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        posterMaterialService.deleteMaterial(id);
        return success(true);
    }


}