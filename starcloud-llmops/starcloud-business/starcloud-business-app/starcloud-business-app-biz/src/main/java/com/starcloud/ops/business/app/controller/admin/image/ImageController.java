package com.starcloud.ops.business.app.controller.admin.image;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageReqVO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageRespVO;
import com.starcloud.ops.business.app.service.image.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    public CommonResult<ImageRespVO> textToImage(@Validated @RequestBody ImageReqVO request) {
        ImageRequest imageRequest = request.getImageRequest();
        imageRequest.setCfgScale(0.7);
        imageRequest.setSteps(50);
        imageRequest.setEngine("stable-diffusion-512-v2-0");

        return CommonResult.success(imageService.textToImage(request));
    }
}
