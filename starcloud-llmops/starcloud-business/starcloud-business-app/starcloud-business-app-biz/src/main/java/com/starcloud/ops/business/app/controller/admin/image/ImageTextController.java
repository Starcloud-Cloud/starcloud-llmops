package com.starcloud.ops.business.app.controller.admin.image;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ReplaceBackgroundClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.UpscaleClipDropRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.feign.response.VectorSearchResponse;
import com.starcloud.ops.business.app.service.Task.ThreadWithContext;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-07
 */
@Slf4j
@RestController
@RequestMapping("/llm/image/test")
@Tag(name = "星河云海-图片生成测试", description = "图片生成测试")
public class ImageTextController {

    @Resource
    private ClipDropImageService clipDropImageService;

    @Resource
    private ThreadWithContext threadWithContext;


    @PostMapping(value = "/upscale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "放大图片", description = "放大图片")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Object> upscale(@Validated UpscaleClipDropRequest request) {
        ClipDropImage response = clipDropImageService.upscale(request);

        log.info(JSONUtil.toJsonStr(response));
        return CommonResult.success(response);
    }

    @PostMapping(value = "/removeBackground", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "去除背景", description = "放大图片")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Object> removeBackground(@Validated ImageFileClipDropRequest request) {
        ClipDropImage response = clipDropImageService.removeBackground(request);

        log.info(JSONUtil.toJsonStr(response));
        return CommonResult.success(response);
    }

    @PostMapping(value = "/replaceBackground", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "替换背景", description = "放大图片")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Object> replaceBackground(@Validated ReplaceBackgroundClipDropRequest request) {
        ClipDropImage response = clipDropImageService.replaceBackground(request);

        log.info(JSONUtil.toJsonStr(response));
        return CommonResult.success(response);
    }

    @PostMapping(value = "/removeBackground2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "去除背景2", description = "放大图片")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public DeferredResult<Object> removeBackground2(@Validated ImageFileClipDropRequest request) {
        DeferredResult<Object> deferredResult = new DeferredResult<>(1000L);

        threadWithContext.asyncExecute(() -> {
            ClipDropImage response = clipDropImageService.removeBackground(request);
            deferredResult.setResult(response);
        });

        deferredResult.onTimeout(() -> {
            deferredResult.setResult(VectorSearchResponse.success("ddd"));

        });
        return deferredResult;

    }

}
