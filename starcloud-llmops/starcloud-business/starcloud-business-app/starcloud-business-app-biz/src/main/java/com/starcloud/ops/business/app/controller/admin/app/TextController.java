package com.starcloud.ops.business.app.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.translator.Translator;
import com.starcloud.ops.business.app.translator.TranslatorContext;
import com.starcloud.ops.business.app.translator.request.TranslateRequest;
import com.starcloud.ops.business.app.translator.response.TranslateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用管理接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/text/mt")
@Tag(name = "星河云海-mt", description = "mt")
public class TextController {

    @Resource
    private TranslatorContext translatorContext;

    @PostMapping("/mt")
    @Operation(summary = "mt", description = "mt")
    @ApiOperationSupport(order = 6, author = "nacoyer")
    public CommonResult<TranslateResponse> categories(@RequestBody TranslateRequest request) {
        Translator translator = translatorContext.getTranslator(1);
        TranslateResponse translate = translator.translate(request);
        return CommonResult.success(translate);
    }


}
