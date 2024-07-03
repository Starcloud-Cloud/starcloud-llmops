package com.starcloud.ops.business.app.controller.admin.plugins;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.ocr.OcrGeneralDTO;
import com.starcloud.ops.business.app.api.xhs.material.XhsNoteDTO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.ImageOcrReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.TextExtractionReqVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.XhsOcrReqVO;
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
@Tag(name = "星河云海-创作插件", description = "创作插件")
public class PluginsController {

    @Resource
    private PluginsService pluginsService;

    @PostMapping(value = "/xhsOcr")
    @Operation(summary = "小红书ocr")
    public CommonResult<XhsNoteDTO> xhsOcr(@Valid @RequestBody XhsOcrReqVO reqVO) {
        return CommonResult.success(pluginsService.xhsOcr(reqVO));
    }

    @PostMapping(value = "/imageOcr")
    @Operation(summary = "图片ocr")
    public CommonResult<OcrGeneralDTO> imageOcr(@Valid @RequestBody ImageOcrReqVO reqVO) {
        return CommonResult.success(pluginsService.imageOcr(reqVO));
    }

    @PostMapping(value = "/extraction")
    @Operation(summary = "文本智能提取")
    public CommonResult<JSONObject> intelligentTextExtraction(@Valid @RequestBody TextExtractionReqVO reqVO) {
        return CommonResult.success(pluginsService.intelligentTextExtraction(reqVO));
    }


}
