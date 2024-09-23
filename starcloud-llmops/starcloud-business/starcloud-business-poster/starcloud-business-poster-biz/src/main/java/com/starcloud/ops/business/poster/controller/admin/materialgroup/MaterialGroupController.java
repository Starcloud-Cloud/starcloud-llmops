package com.starcloud.ops.business.poster.controller.admin.materialgroup;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupRespVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import com.starcloud.ops.business.poster.service.materialgroup.MaterialGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 海报素材分组")
@RestController
@RequestMapping("/poster/material-group")
@Validated
public class MaterialGroupController {

    @Resource
    private MaterialGroupService materialGroupService;

    @PostMapping("u/create")
    @Operation(summary = "创建海报素材分组")
    @PreAuthorize("@ss.hasPermission('poster:material-group:create')")
    public CommonResult<Long> createMaterialGroup(@Valid @RequestBody MaterialGroupSaveReqVO createReqVO) {
        return success(materialGroupService.createMaterialGroup(createReqVO));
    }

    @PutMapping("u/update")
    @Operation(summary = "更新海报素材分组")
    @PreAuthorize("@ss.hasPermission('poster:material-group:update')")
    public CommonResult<Boolean> updateMaterialGroup(@Valid @RequestBody MaterialGroupSaveReqVO updateReqVO) {
        materialGroupService.updateMaterialGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("u/delete")
    @Operation(summary = "删除海报素材分组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('poster:material-group:delete')")
    public CommonResult<Boolean> deleteMaterialGroup(@RequestParam("id") Long id) {
        materialGroupService.deleteMaterialGroup(id);
        return success(true);
    }

    @GetMapping("u/get")
    @Operation(summary = "获得海报素材分组")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('poster:material-group:query')")
    public CommonResult<MaterialGroupRespVO> getMaterialGroup(@RequestParam("id") Long id) {
        MaterialGroupDO materialGroup = materialGroupService.getMaterialGroup(id);
        return success(BeanUtils.toBean(materialGroup, MaterialGroupRespVO.class));
    }

    @GetMapping("u/page")
    @Operation(summary = "获得海报素材分组分页")
    @PreAuthorize("@ss.hasPermission('poster:material-group:query')")
    public CommonResult<PageResult<MaterialGroupRespVO>> getMaterialGroupPage(@Valid MaterialGroupPageReqVO pageReqVO) {
        PageResult<MaterialGroupDO> pageResult = materialGroupService.getMaterialGroupPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MaterialGroupRespVO.class));
    }


}