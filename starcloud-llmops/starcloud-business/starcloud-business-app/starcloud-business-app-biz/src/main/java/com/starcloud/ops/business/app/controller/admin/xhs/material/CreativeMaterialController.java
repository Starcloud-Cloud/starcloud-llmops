package com.starcloud.ops.business.app.controller.admin.xhs.material;

import cn.hutool.json.JSON;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.CreativeMaterialGenerationDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.FilterMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.GeneralFieldCodeReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ModifyMaterialReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.MaterialRespVO;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialService;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/llm/creative/material")
@Tag(name = "星河云海-创作素材", description = "创作素材")
public class CreativeMaterialController {

    @Resource
    private CreativeMaterialService creativeMaterialService;

    @GetMapping("/metadata")
    @Operation(summary = "素材元数据", description = "素材元数据")
    public CommonResult<Map<String, Object>> metadata() {
        Map<String, Object> result = creativeMaterialService.metadata();
        return CommonResult.success(result);
    }

    @PostMapping("/create")
    @Operation(summary = "新增素材", description = "新增素材")
    public CommonResult<Boolean> creatMaterial(@RequestBody @Valid BaseMaterialVO reqVO) {
        creativeMaterialService.creatMaterial(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除素材", description = "删除素材")
    public CommonResult<Boolean> deleteMaterial(@PathVariable("uid") String uid) {
        creativeMaterialService.deleteMaterial(uid);
        return CommonResult.success(true);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改素材", description = "修改素材")
    public CommonResult<Boolean> modifyMaterial(@RequestBody @Valid ModifyMaterialReqVO reqVO) {
        creativeMaterialService.modifyMaterial(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/fieldCode")
    @Operation(summary = "生成字段code", description = "生成字段code")
    @OperateLog(enable = false)
    public CommonResult<List<MaterialFieldConfigDTO>> generalFieldCode(@RequestBody @Valid GeneralFieldCodeReqVO reqVO) {
        return CommonResult.success(creativeMaterialService.generalFieldCode(reqVO));
    }

    @PostMapping("/judge")
    @Operation(summary = "判断素材显示类型", description = "判断素材显示类型 true显示图片 false显示列表")
    @OperateLog(enable = false)
    public CommonResult<Boolean> judgePicture(@RequestParam("uid") String uid,
                                              @RequestParam("planSource") String planSource) {
        return CommonResult.success(creativeMaterialService.judgePicture(uid, planSource));
    }

    @PutMapping("/filter")
    @Operation(summary = "筛选素材", description = "筛选素材")
    public CommonResult<List<MaterialRespVO>> filterMaterial(@RequestBody @Valid FilterMaterialReqVO reqVO) {
        return CommonResult.success(creativeMaterialService.filterMaterial(reqVO));
    }

    @PostMapping("/materialGenerate")
    @Operation(summary = "素材生成", description = "素材生成")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<JSON> materialGenerate(@Validated @RequestBody CreativeMaterialGenerationDTO request) {
        return CommonResult.success(creativeMaterialService.materialGenerate(request));
    }

    @PostMapping(value = "/customMaterialGenerate")
    @Operation(summary = "素材生成")
    public CommonResult<JSON> customMaterialGenerate(@Validated @RequestBody CreativeMaterialGenerationDTO request) {
        return CommonResult.success(creativeMaterialService.customMaterialGenerate(request));
    }

    @GetMapping("/list")
    @Operation(summary = "素材列表", description = "素材列表")
    public CommonResult<List<Map<String, Object>>> listMaterial(@RequestParam("uid") String uid,
                                                                @RequestParam("source") String source) {
        return CommonResult.success(creativeMaterialService.listMaterial(uid, source));
    }

    @PostMapping(value = "/infoPageByMarketUid")
    @Operation(summary = "获得素材应用执行日志信息分页")
    public CommonResult<PageResult<AppLogMessageRespVO>> infoPageByMarketUid(@RequestBody AppLogConversationInfoPageUidReqVO query) {
        return CommonResult.success(creativeMaterialService.infoPageByMarketUid(query));
    }

}
