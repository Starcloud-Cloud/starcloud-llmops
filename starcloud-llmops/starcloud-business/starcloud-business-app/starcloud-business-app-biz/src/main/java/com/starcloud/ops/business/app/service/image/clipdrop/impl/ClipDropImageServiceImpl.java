package com.starcloud.ops.business.app.service.image.clipdrop.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.ClipDropImageClient;
import com.starcloud.ops.business.app.feign.request.clipdrop.CleanupClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.ReplaceBackgroundClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.SketchToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.TextToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.request.clipdrop.UpscaleClipDropRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.feign.response.ImageResponse;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import com.starcloud.ops.business.core.config.oss.AliyunOssClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

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

    @Resource
    private AliyunOssClient aliyunOssClient;

    /**
     * 放大图片
     *
     * @param request 请求内容
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> upscale(UpscaleClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.upscale(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop upscale image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop upscale image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 图片修复
     *
     * @param request 图片修复请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> cleanup(CleanupClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.cleanup(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop cleanup image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop cleanup image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 纵向深度估算
     *
     * @param request 纵向深度估算请求数据
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> portraitDepthEstimation(ImageFileClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.portraitDepthEstimation(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop portrait depth estimation image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop portrait depth estimation image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 纵向曲面法线
     *
     * @param request 要处理的原始图像请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> portraitSurfaceNormals(ImageFileClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.portraitSurfaceNormals(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop portrait surface normals image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop portrait surface normals image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 重新构想
     *
     * @param request 要处理的原始图像请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> reimagine(ImageFileClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.reimagine(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop reimagine image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop reimagine image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 删除图片背景，智能抠图
     *
     * @param request 要处理的图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> removeBackground(ImageFileClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.removeBackground(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop remove background image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop remove background image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 删除图片文字
     *
     * @param request 要处理的图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> removeText(ImageFileClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.removeText(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop remove text image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop remove text background image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 替换背景
     *
     * @param request 替换背景请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> replaceBackground(ReplaceBackgroundClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.replaceBackground(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop replace background image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop replace background background image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> sketchToImage(SketchToImageClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.sketchToImage(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop sketch to image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop sketch to image background image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 草稿图生成图片
     *
     * @param request 草稿图生成图片请求
     * @return 图片响应实体
     */
    @Override
    public ImageResponse<ClipDropImage> textToImage(TextToImageClipDropRequest request) {
        try {
            ResponseEntity<byte[]> responseEntity = clipDropImageClient.textToImage(request);
            return transformResponse(responseEntity);
        } catch (FeignException exception) {
            log.error("ClipDrop text to image failure: ErrorCode: {}, ErrorMessage: {}", exception.status(), exception.getMessage(), exception);
            return ImageResponse.failure(exception.status(), exception.getMessage());
        } catch (Exception exception) {
            log.error("ClipDrop text to image background image failure: ErrorMessage: {}", exception.getMessage(), exception);
            return ImageResponse.failure(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 构建返回响应
     *
     * @param responseEntity 响应体
     * @return 响应信息
     */
    private ImageResponse<ClipDropImage> transformResponse(ResponseEntity<byte[]> responseEntity) {
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (!HttpStatus.OK.equals(statusCode)) {
            log.error("ClipDrop 生成图片失败，错误码：{}", responseEntity.getStatusCodeValue());
            return ImageResponse.failure(responseEntity.getStatusCodeValue(), "请求失败，请稍候重试！");
        }
        HttpHeaders headers = responseEntity.getHeaders();
        MediaType contentType = Optional.ofNullable(headers.getContentType()).orElse(MediaType.IMAGE_PNG);
        ClipDropImage image = new ClipDropImage();
        String uuid = IdUtil.fastSimpleUUID();
        byte[] binary = responseEntity.getBody();
        String mediaType = contentType.toString();
        image.setUuid(uuid);
        image.setBinary(binary);
        image.setMediaType(mediaType);
        // 上传到 OSS
//        String url = aliyunOssClient.putAiGenerateImage(uuid, mediaType, binary);
//        image.setUrl(url);
        return ImageResponse.success(image);
    }


}
