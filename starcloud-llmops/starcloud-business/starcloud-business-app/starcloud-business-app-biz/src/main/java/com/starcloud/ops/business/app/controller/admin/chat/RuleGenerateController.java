package com.starcloud.ops.business.app.controller.admin.chat;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.api.app.vo.response.RuleGenerateRespVO;
import com.starcloud.ops.business.app.api.chat.RuleGenerateRequest;
import com.starcloud.ops.business.app.service.chat.RuleGenerateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

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
