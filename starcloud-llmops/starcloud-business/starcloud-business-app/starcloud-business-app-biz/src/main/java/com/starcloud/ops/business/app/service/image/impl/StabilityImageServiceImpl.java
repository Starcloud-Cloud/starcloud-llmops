package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.convert.image.VSearchConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.StabilityImageClient;
import com.starcloud.ops.business.app.feign.request.StabilityImageRequest;
import com.starcloud.ops.business.app.feign.response.StabilityImage;
import com.starcloud.ops.business.app.feign.response.ImageResponse;
import com.starcloud.ops.business.app.service.image.StabilityImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 对接 VSearch 的图片服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Service
public class StabilityImageServiceImpl implements StabilityImageService {

    @Resource
    private StabilityImageClient vSearchClient;

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<StabilityImage> generate(StabilityImageRequest request) {
        // 生成图片
        ImageResponse<List<StabilityImage>> response = vSearchClient.generateImage(request);
        // 校验响应结果
        validateImagesResponse(response);
        // 返回结果
        return response.getResult();
    }

    /**
     * 文字生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<ImageDTO> textToImage(ImageRequest request) {
        // 生成图片
        StabilityImageRequest imageRequest = VSearchConvert.INSTANCE.convert(request);
        List<StabilityImage> imageList = this.generate(imageRequest);
        // 转换结果并且返回
        return VSearchConvert.INSTANCE.convert(imageList);
    }

    /**
     * 对生成图片的响应结果进行校验
     *
     * @param response 响应结果
     */
    private void validateImagesResponse(ImageResponse<List<StabilityImage>> response) {
        if (Objects.isNull(response)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GENERATE_IMAGE_FAIL);
        }
        // 如果成功，且有数据，则直接返回
        if (response.getSuccess() && CollectionUtil.isNotEmpty(response.getResult())) {
            return;
        }
        // 失败，抛出异常
        if (Objects.isNull(response.getCode()) && StringUtils.isNotBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(response.getCode(), response.getMessage()));
        }
        if (Objects.isNull(response.getCode()) && StringUtils.isNotBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), response.getMessage()));
        }
        if (Objects.nonNull(response.getCode()) && StringUtils.isBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(response.getCode(), ErrorCodeConstants.GENERATE_IMAGE_FAIL.getMsg()));
        }
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.GENERATE_IMAGE_FAIL);
    }
}
