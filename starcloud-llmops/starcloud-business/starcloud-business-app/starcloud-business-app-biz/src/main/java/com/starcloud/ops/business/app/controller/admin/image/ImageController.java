package com.starcloud.ops.business.app.controller.admin.image;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.OptimizePromptReqVO;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 图片生成接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/image")
@Tag(name = "星河云海-图片生成", description = "星河云海图片生成管理")
public class ImageController {

    @Resource
    private ImageService imageService;

    @GetMapping("/meta")
    @Operation(summary = "生成图片元数据", description = "生成图片元数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Map<String, List<ImageMetaDTO>>> meta() {
        return CommonResult.success(imageService.meta());
    }

    @GetMapping("/history")
    @Operation(summary = "查询历史图片列表", description = "查询历史图片列表")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<ImageRespVO> historyGenerateImages() {
        return CommonResult.success(imageService.historyGenerateImages());
    }

    @PostMapping("/text-to-image")
    @Operation(summary = "文本生成图片", description = "文本生成图片")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<ImageMessageRespVO> textToImage(@Validated @RequestBody ImageReqVO request) {
        return CommonResult.success(imageService.generateImage(request));
    }

    @GetMapping("/optimizePromptAppList")
    @Operation(summary = "获取优化提示应用列表", description = "获取优化提示应用列表")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<List<Option>> getOptimizePromptAppList() {
        return CommonResult.success(imageService.getOptimizePromptAppList());
    }

    @PostMapping("/optimizePrompt")
    @Operation(summary = "优化 prompt", description = "优化 prompt")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public SseEmitter optimizePrompt(@RequestBody OptimizePromptReqVO optimizePromptReqVO, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");
        // 设置 SSE
        SseEmitter emitter = new SseEmitter(60000L);
        optimizePromptReqVO.setSseEmitter(emitter);
        imageService.optimizePrompt(optimizePromptReqVO);
        return emitter;
    }
}
