package com.starcloud.ops.business.app.controller.admin;

import com.starcloud.ops.business.app.api.dto.CreateTemplateRequestParams;
import com.starcloud.ops.business.app.api.dto.TemplateRequestParams;
import com.starcloud.ops.business.app.api.dto.TemplateResult;
import com.starcloud.ops.business.app.dal.mysql.LlmBusinessAppMapper;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/llm/app")
@Api(value = "应用管理", tags = "应用管理")
public class BusinessAppController {


    @Resource
    private LlmBusinessAppMapper llmBusinessAppMapper;

    @PostMapping("/create")
    @Operation(summary = "新增模板")
    //@PreAuthorize("@ss.hasPermission('business:app:create')")
    @PermitAll
    public TemplateResult<Integer> createTemplate(@RequestBody CreateTemplateRequestParams params) {

        return TemplateResult.success("123");
    }


    @PostMapping("/install")
    @Operation(summary = "安装模板")
    public TemplateResult<String> installTemplate(@RequestBody TemplateRequestParams params) {

        return TemplateResult.success("456");
    }

    @PostMapping("/checkDownloadMarketTemplate")
    @Operation(summary = "检查用户是否已经安装模版")
    public TemplateResult<Boolean> checkDownloadMarketTemplate(@RequestBody TemplateRequestParams params) {
        return TemplateResult.success(true);
    }


}
