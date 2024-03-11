package com.starcloud.ops.business.app.controller.admin.xhs.material;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response.ParseResult;
import com.starcloud.ops.business.app.service.xhs.material.ParseMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
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

    @PostMapping("/import")
    @Operation(summary = "导入素材", description = "导入素材")
    public CommonResult<String> importMaterial(@RequestParam("file") MultipartFile file){
        return CommonResult.success(parseMaterialService.parseToRedis(file));
    }

    @GetMapping("/result/{parseUid}")
    @Operation(summary = "导入结果", description = "导入结果")
    public CommonResult<ParseResult> importMission(@PathVariable("parseUid") String parseUid) {
        return CommonResult.success(parseMaterialService.parseResult(parseUid));
    }
}
