package com.starcloud.ops.business.app.service.image.clipdrop;

import com.starcloud.ops.business.app.feign.request.clipdrop.CleanupClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ReplaceBackgroundClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.SketchToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.TextToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.UpscaleClipDropRequest;
import com.starcloud.ops.business.app.feign.dto.ClipDropImage;

/**
 * ClipDrop AI 生图服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-07
 */
@SuppressWarnings("unused")
public interface ClipDropImageService {

    /**
     * 放大图片
     *
     * @param request 请求内容
     * @return 图片响应实体
     */
    ClipDropImage upscale(UpscaleClipDropRequest request);

    /**
     * 图片修复
     *
     * @param request 图片修复请求
     * @return 图片响应实体
     */
    ClipDropImage cleanup(CleanupClipDropRequest request);

    /**
     * 纵向深度估算
     *
     * @param request 纵向深度估算请求数据
     * @return 图片响应实体
     */
    ClipDropImage portraitDepthEstimation(ImageFileClipDropRequest request);

    /**
     * 纵向曲面法线
     *
     * @param request 要处理的原始图像请求
     * @return 图片响应实体
     */
    ClipDropImage portraitSurfaceNormals(ImageFileClipDropRequest request);

    /**
     * 重新构想
     *
     * @param request 要处理的原始图像请求
     * @return 图片响应实体
     */
    ClipDropImage reimagine(ImageFileClipDropRequest request);

    /**
     * 删除图片背景，智能抠图
     *
     * @param request 要处理的图片请求
     * @return 图片响应实体
     */
    ClipDropImage removeBackground(ImageFileClipDropRequest request);

    /**
     * 删除图片文字
     *
     * @param request 要处理的图片请求
     * @return 图片响应实体
     */
    ClipDropImage removeText(ImageFileClipDropRequest request);

    /**
     * 替换背景
     *
     * @param request 替换背景请求
     * @return 图片响应实体
     */
    ClipDropImage replaceBackground(ReplaceBackgroundClipDropRequest request);

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 图片响应实体
     */
    ClipDropImage sketchToImage(SketchToImageClipDropRequest request);

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 图片响应实体
     */
    ClipDropImage textToImage(TextToImageClipDropRequest request);
}
