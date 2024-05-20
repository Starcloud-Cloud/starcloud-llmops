package com.starcloud.ops.business.app.controller.admin.xhs.material;

import cn.hutool.json.JSON;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.xhs.material.dto.CreativeMaterialGenerationDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/llm/creative/material")
@Tag(name = "星河云海-创作素材", description = "创作素材")
public class CreativeMaterialController {

    @Resource
    private CreativeMaterialService materialService;

    @GetMapping("/metadata")
    @Operation(summary = "素材元数据", description = "素材元数据")
    public CommonResult<Map<String, Object>> metadata() {
        Map<String, Object> result = materialService.metadata();
        return CommonResult.success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "新增素材", description = "新增素材")
    public CommonResult<Boolean> creatMaterial(@RequestBody @Valid BaseMaterialVO reqVO) {
        materialService.creatMaterial(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除素材", description = "删除素材")
    public CommonResult<Boolean> deleteMaterial(@PathVariable("uid") String uid) {
        materialService.deleteMaterial(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改素材", description = "修改素材")
    public CommonResult<Boolean> modifyMaterial(@RequestBody @Valid ModifyMaterialReqVO reqVO) {
        materialService.modifyMaterial(reqVO);
        return CommonResult.success(true);
    }

    @PutMapping("/filter")
    @Operation(summary = "筛选素材", description = "筛选素材")
    public CommonResult<List<MaterialRespVO>> filterMaterial(@RequestBody @Valid FilterMaterialReqVO reqVO) {
        return CommonResult.success(materialService.filterMaterial(reqVO));
    }

    @PostMapping("/materialGenerate")
    @Operation(summary = "素材生成", description = "素材生成")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<JSON> materialGenerate(@Validated @RequestBody CreativeMaterialGenerationDTO request) {
        return CommonResult.success(materialService.materialGenerate(request));
    }

    @PostMapping(value = "/customMaterialGenerate")
    @Operation(summary = "素材生成")
    public CommonResult<JSON> customMaterialGenerate(@Validated @RequestBody CreativeMaterialGenerationDTO request) {
        return CommonResult.success(materialService.customMaterialGenerate(request));
    }
}
