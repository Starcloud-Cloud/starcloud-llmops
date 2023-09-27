package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.config.ClipDropFeignConfiguration;
import com.starcloud.ops.business.app.feign.request.clipdrop.CleanupClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ReplaceBackgroundClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.SketchToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.TextToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.UpscaleClipDropRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ClipDrop AI 生成图片 <br>
 * <a href="https://clipdrop.co/apis/docs/image-upscaling">https://clipdrop.co/apis/docs/image-upscaling</a>
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-06
 */
@FeignClient(name = "${feign.remote.clip-drop.name}", url = "${feign.remote.clip-drop.url}", configuration = ClipDropFeignConfiguration.class)
public interface ClipDropImageClient {

    /**
     * 放大图片
     *
     * @param request 请求内容
     * @return 放大的图片的二进制图片响应实体
     */
    @PostMapping(value = "/image-upscaling/v1/upscale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> upscale(@Validated UpscaleClipDropRequest request);

    /**
     * 图片修复
     *
     * @param request 图片修复请求
     * @return 修复厚的图片的二进制图片响应实体
     */
    @PostMapping(value = "/cleanup/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> cleanup(@Validated CleanupClipDropRequest request);

    /**
     * 纵向深度估算
     *
     * @param request 纵向深度估算请求数据
     * @return 处理后的图片二进制数据流
     */
    @PostMapping(value = "/portrait-depth-estimation/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> portraitDepthEstimation(@Validated ImageFileClipDropRequest request);

    /**
     * 纵向曲面法线
     *
     * @param request 要处理的原始图像请求
     * @return 处理后的图片二进制数据流
     */
    @PostMapping(value = "/portrait-surface-normals/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> portraitSurfaceNormals(@Validated ImageFileClipDropRequest request);

    /**
     * 重新构想
     *
     * @param request 要处理的原始图像请求
     * @return 处理后的图片二进制数据流
     */
    @PostMapping(value = "/reimagine/v1/reimagine", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> reimagine(@Validated ImageFileClipDropRequest request);

    /**
     * 删除图片背景，智能抠图
     *
     * @param request 要处理的图片请求
     * @return 已经处理的二进制图片响应实体
     */
    @PostMapping(value = "/remove-background/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> removeBackground(@Validated ImageFileClipDropRequest request);

    /**
     * 删除图片文字
     *
     * @param request 要处理的图片请求
     * @return 已经处理的二进制图片响应实体
     */
    @PostMapping(value = "/remove-text/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> removeText(@Validated ImageFileClipDropRequest request);

    /**
     * 替换背景
     *
     * @param request 替换背景请求
     * @return 处理后二进制图片响应实体
     */
    @PostMapping(value = "/replace-background/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> replaceBackground(@Validated ReplaceBackgroundClipDropRequest request);

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 处理后二进制图片响应实体
     */
    @PostMapping(value = "/sketch-to-image/v1/sketch-to-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> sketchToImage(@Validated SketchToImageClipDropRequest request);


    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 处理后二进制图片响应实体
     */
    @PostMapping(value = "/text-to-image/v1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<byte[]> textToImage(@Validated TextToImageClipDropRequest request);
}
