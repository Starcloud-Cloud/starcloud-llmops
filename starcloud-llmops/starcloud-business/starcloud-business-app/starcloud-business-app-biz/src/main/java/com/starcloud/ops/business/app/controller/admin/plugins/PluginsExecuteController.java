package com.starcloud.ops.business.app.controller.admin.plugins;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.app.handler.ImageOcr.HandlerResponse;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginExecuteRespVO;
import com.starcloud.ops.business.app.service.plugins.PluginsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/creative/plugins")
@Tag(name = "星河云海-执行创作插件", description = "执行创作插件")
public class PluginsExecuteController {

    @Resource
    private PluginsService pluginsService;

    @PostMapping(value = "/xhsOcr")
    @Operation(summary = "小红书ocr")
    public CommonResult<XhsNoteDTO> xhsOcr(@Valid @RequestBody XhsOcrReqVO reqVO) {
        return CommonResult.success(pluginsService.xhsOcr(reqVO));
    }

    @PostMapping(value = "/imageOcr")
    @Operation(summary = "图片ocr")
    public CommonResult<HandlerResponse> imageOcr(@Valid @RequestBody ImageOcrReqVO reqVO) {
        return CommonResult.success(pluginsService.imageOcr(reqVO));
    }

    @PostMapping(value = "/extraction")
    @Operation(summary = "文本智能提取")
    public CommonResult<JSONObject> intelligentTextExtraction(@Valid @RequestBody TextExtractionReqVO reqVO) {
        return CommonResult.success(pluginsService.intelligentTextExtraction(reqVO));
    }

    @PostMapping(value = "/sensitive")
    @Operation(summary = "敏感词检测")
    public CommonResult<JSONObject> sensitiveWord(@Valid @RequestBody RiskWordReqVO reqVO) {
        return CommonResult.success(pluginsService.sensitiveWord(reqVO));
    }

    @PostMapping(value = "/execute")
    @Operation(summary = "执行插件")
    public CommonResult<String> executePlugin(@Valid @RequestBody PluginExecuteReqVO reqVO) {
        return CommonResult.success(pluginsService.executePlugin(reqVO));
    }

    @PostMapping(value = "/executeResult")
    @Operation(summary = "查询插件执行结果")
    public CommonResult<PluginExecuteRespVO> getPluginResult(@Valid @RequestBody PluginResultReqVO reqVO) {
        return CommonResult.success(pluginsService.getPluginResult(reqVO));
    }

}
