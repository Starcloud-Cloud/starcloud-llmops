package com.starcloud.ops.business.app.controller.admin.xhs.material;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ParseXhsReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import com.starcloud.ops.business.app.service.xhs.material.ParseMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/llm/material")
@Tag(name = "星河云海-创作素材导入解析", description = "创作素材导入解析")
public class MaterialImportController {

    @Resource
    private ParseMaterialService parseMaterialService;

    @GetMapping("/template/{type}")
    @Operation(summary = "模板详情", description = "模板详情")
    public CommonResult<Map<String, Object>> template(@PathVariable("type") String type) throws IOException {
        return CommonResult.success(parseMaterialService.template(type));
    }

    @Operation(summary = "下载素材模板")
    @GetMapping("/download/template")
    @OperateLog(enable = false)
    public void downloadTemplate(@RequestParam("uid") String uid,
                                 @RequestParam("planSource") String planSource,
                                 HttpServletResponse response) {
        parseMaterialService.downloadTemplate(uid, planSource, response);
    }

    @PostMapping("/import")
    @Operation(summary = "导入素材", description = "导入素材")
    @OperateLog(enable = false)
    public CommonResult<String> importMaterial(@RequestParam("file") MultipartFile file) {
        return CommonResult.success(parseMaterialService.parseToRedis(file));
    }

    @PostMapping("/parse")
    @Operation(summary = "批量解析小红书内容", description = "批量解析小红书内容")
    @OperateLog(enable = false)
    public CommonResult<List<AbstractCreativeMaterialDTO>> parseXhs(@Valid @RequestBody ParseXhsReqVO reqVO) {
        return CommonResult.success(parseMaterialService.parseXhs(reqVO));
    }

    @GetMapping("/result/{parseUid}")
    @Operation(summary = "导入结果", description = "导入结果")
    public CommonResult<ParseResult> importMission(@PathVariable("parseUid") String parseUid) {
        return CommonResult.success(parseMaterialService.parseResult(parseUid));
    }
}
