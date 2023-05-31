package com.starcloud.ops.business.app.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/llm/app")
@Tag(name = "应用管理", description = "应用管理")
public class BusinessAppController {


//    @Resource
//    private LlmBusinessAppMapper llmBusinessAppMapper;
//
//    @PostMapping("/create")
//    @Operation(summary = "新增模板")
//    //@PreAuthorize("@ss.hasPermission('business:app:create')")
//    @PermitAll
//    public TemplateResult<Integer> createTemplate(@RequestBody CreateTemplateRequestParams params) {
//
//        return TemplateResult.success("123");
//    }
//
//
//    @PostMapping("/install")
//    @Operation(summary = "安装模板")
//    public TemplateResult<String> installTemplate(@RequestBody TemplateRequestParams params) {
//
//        return TemplateResult.success("456");
//    }
//
//    @PostMapping("/checkDownloadMarketTemplate")
//    @Operation(summary = "检查用户是否已经安装模版")
//    public TemplateResult<Boolean> checkDownloadMarketTemplate(@RequestBody TemplateRequestParams params) {
//        return TemplateResult.success(true);
//    }
//

}
