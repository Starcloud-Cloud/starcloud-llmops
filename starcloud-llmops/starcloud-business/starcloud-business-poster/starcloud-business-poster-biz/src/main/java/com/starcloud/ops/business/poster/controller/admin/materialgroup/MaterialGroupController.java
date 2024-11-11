package com.starcloud.ops.business.poster.controller.admin.materialgroup;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.starcloud.ops.business.poster.controller.admin.material.vo.MaterialRespVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPageReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupPublishReqVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupRespVO;
import com.starcloud.ops.business.poster.controller.admin.materialgroup.vo.MaterialGroupSaveReqVO;
import com.starcloud.ops.business.poster.dal.dataobject.material.MaterialDO;
import com.starcloud.ops.business.poster.dal.dataobject.materialgroup.MaterialGroupDO;
import com.starcloud.ops.business.poster.service.material.MaterialService;
import com.starcloud.ops.business.poster.service.materialgroup.MaterialGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static com.starcloud.ops.business.poster.enums.ErrorCodeConstants.MATERIAL_GROUP_NOT_EXISTS;


@Tag(name = "管理后台 - 海报素材分组")
@RestController
@RequestMapping("/poster/material-group")
@Validated
public class MaterialGroupController {

    @Resource
    private MaterialGroupService materialGroupService;

    @Resource
    private MaterialService materialService;

    @PostMapping("u/create")
    @Operation(summary = "创建海报素材分组")
    public CommonResult<String> createMaterialGroup(@Valid @RequestBody MaterialGroupSaveReqVO createReqVO) {
        return success(materialGroupService.createMaterialGroup(createReqVO));
    }

    @PutMapping("u/update")
    @Operation(summary = "更新海报素材分组")
    public CommonResult<Boolean> updateMaterialGroup(@Valid @RequestBody MaterialGroupSaveReqVO updateReqVO) {
        materialGroupService.updateMaterialGroupByUid(updateReqVO);
        return success(true);
    }

    @DeleteMapping("u/delete")
    @Operation(summary = "删除海报素材分组")
    @Parameter(name = "uid", description = "编号", required = true)
    public CommonResult<Boolean> deleteMaterialGroup(@RequestParam("uid") String uid) {
        materialGroupService.deleteMaterialGroupByUid(uid);
        return success(true);
    }

    @GetMapping("u/get")
    @Operation(summary = "获得海报素材分组")
    @Parameter(name = "uid", description = "编号", required = true, example = "1024")
    public CommonResult<MaterialGroupRespVO> getMaterialGroup(@RequestParam("uid") String uid) {
        //  忽略用户权限获取


        MaterialGroupDO materialGroup = materialGroupService.getMaterialGroupByUid(uid);
        if (materialGroup == null) {
            throw exception(MATERIAL_GROUP_NOT_EXISTS);
        }
        List<MaterialDO> materialByGroup = materialService.getMaterialByGroup(materialGroup.getId());
        MaterialGroupRespVO bean = BeanUtils.toBean(materialGroup, MaterialGroupRespVO.class);
        bean.setMaterialRespVOS(BeanUtils.toBean(materialByGroup, MaterialRespVO.class));

        return success(bean);
    }

    @GetMapping("u/page")
    @Operation(summary = "获得海报素材分组分页")
    public CommonResult<PageResult<MaterialGroupRespVO>> getMaterialGroupPage(@Valid MaterialGroupPageReqVO pageReqVO) {
        pageReqVO.setPageSize(10000);
        PageResult<MaterialGroupRespVO> pageResult = materialGroupService.getMaterialGroupPage(pageReqVO);
        return success(pageResult);
    }


    @GetMapping("u/copy")
    @Operation(summary = "复制分组")
    public CommonResult<Boolean> copy(@RequestParam("uid") String uid) {
        materialGroupService.copyGroup(uid);
        return success(true);
    }


    @PostMapping("u/publish")
    @Operation(summary = "发布海报素材到市场")
    public CommonResult<Boolean> publish(@Valid @RequestBody  MaterialGroupPublishReqVO publishReqVO) {
        materialGroupService.handlePublish(publishReqVO);
        return success(true);
    }


    // @PostMapping("u/publish")
    // @Operation(summary = "发布海报素材到市场")
    // public CommonResult<Boolean> publish(@RequestParam("uid") String uid) {
    //     materialGroupService.publish(uid);
    //     return success(true);
    // }


    @PostMapping("u/mergeGroup")
    @Operation(summary = "合并分组")
    public CommonResult<Boolean> mergeGroup(@RequestParam("uid") String sourceGroupUid,@RequestParam("uid") String targetGroupUid) {
        materialGroupService.mergeGroup(sourceGroupUid, targetGroupUid);
        return success(true);
    }




}