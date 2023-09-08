package com.starcloud.ops.business.app.service.image.impl;

import com.starcloud.ops.business.app.feign.ClipDropImageClient;
import com.starcloud.ops.business.app.feign.request.clipdrop.CleanupClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ReplaceBackgroundClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.SketchToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.TextToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.UpscaleClipDropRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.feign.response.ImageResponse;
import com.starcloud.ops.business.app.service.image.ClipDropImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ClipDrop AI 生图服务 实现
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-07
 */
@Slf4j
@Service
public class ClipDropImageServiceImpl implements ClipDropImageService {

    @Resource
    private ClipDropImageClient clipDropImageClient;


    /**
     * 放大图片
     *
     * @param request 请求内容
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> upscale(UpscaleClipDropRequest request) {
        return null;
    }

    /**
     * 图片修复
     *
     * @param request 图片修复请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> cleanup(CleanupClipDropRequest request) {
        return null;
    }

    /**
     * 纵向深度估算
     *
     * @param request 纵向深度估算请求数据
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> portraitDepthEstimation(ImageFileClipDropRequest request) {
        return null;
    }

    /**
     * 纵向曲面法线
     *
     * @param request 要处理的原始图像请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> portraitSurfaceNormals(ImageFileClipDropRequest request) {
        return null;
    }

    /**
     * 重新构想
     *
     * @param request 要处理的原始图像请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> reimagine(ImageFileClipDropRequest request) {
        return null;
    }

    /**
     * 删除图片背景，智能抠图
     *
     * @param request 要处理的图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> removeBackground(ImageFileClipDropRequest request) {
        return null;
    }

    /**
     * 删除图片文字
     *
     * @param request 要处理的图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> removeText(ImageFileClipDropRequest request) {
        return null;
    }

    /**
     * 替换背景
     *
     * @param request 替换背景请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> replaceBackground(ReplaceBackgroundClipDropRequest request) {
        return null;
    }

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> sketchToImage(SketchToImageClipDropRequest request) {
        return null;
    }

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> textToImage(TextToImageClipDropRequest request) {
        return null;
    }
}
