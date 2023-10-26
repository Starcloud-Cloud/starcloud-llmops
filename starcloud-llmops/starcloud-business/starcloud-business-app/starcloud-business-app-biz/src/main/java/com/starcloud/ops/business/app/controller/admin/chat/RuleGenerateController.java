package com.starcloud.ops.business.app.controller.admin.chat;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author starcloud
 */
@Tag(name = "星河云海 - 对话规则")
@RestController
@RequestMapping("/llm/chat/rule")
public class RuleGenerateController {

//    @Resource
//    private RuleGenerateService ruleGenerateService;
//
//    @Operation(summary = "对话规则生成")
//    @PostMapping("/generate")
//    public CommonResult<RuleGenerateRespVO> conversation(@RequestBody @Valid RuleGenerateRequest request) {
//        return CommonResult.success(ruleGenerateService.generateRule(request));
//    }
}
