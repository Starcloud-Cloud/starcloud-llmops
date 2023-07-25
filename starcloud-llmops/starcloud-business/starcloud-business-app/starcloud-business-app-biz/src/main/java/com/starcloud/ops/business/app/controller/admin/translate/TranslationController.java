package com.starcloud.ops.business.app.controller.admin.translate;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.translator.LanguageEnum;
import com.starcloud.ops.business.app.translator.Translator;
import com.starcloud.ops.business.app.translator.TranslatorContext;
import com.starcloud.ops.business.app.translator.TranslatorTypeEnum;
import com.starcloud.ops.business.app.translator.request.TranslateRequest;
import com.starcloud.ops.business.app.translator.response.TranslateResponse;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-25
 */
@RestController
@RequestMapping("/llm/mt")
@Tag(name = "星河云海-翻译管理", description = "星河云海翻译管理")
public class TranslationController {

    @Resource
    private TranslatorContext translatorContext;

    @GetMapping("/languages")
    @Operation(summary = "查询应用语言列表", description = "查询语言列表")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    public CommonResult<List<Option>> languages() {
        List<Option> collect = Arrays.stream(LanguageEnum.values()).map(item -> {
            Option option = new Option();
            option.setValue(item.getCode());
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINA.toString().equals(locale.toString())) {
                option.setLabel(item.getLabel());
            } else {
                option.setLabel(item.getLabelEn());
            }
            return option;
        }).collect(Collectors.toList());
        return CommonResult.success(collect);
    }

    @PostMapping("/translate")
    @Operation(summary = "翻译文本", description = "翻译文本")
    @ApiOperationSupport(order = 2, author = "nacoyer")
    public CommonResult<TranslateResponse> categories(@RequestBody TranslateRequest request) {
        Translator translator = translatorContext.getTranslator(TranslatorTypeEnum.ALI_YUN.getCode());
        TranslateResponse translate = translator.translate(request);
        return CommonResult.success(translate);
    }
}
