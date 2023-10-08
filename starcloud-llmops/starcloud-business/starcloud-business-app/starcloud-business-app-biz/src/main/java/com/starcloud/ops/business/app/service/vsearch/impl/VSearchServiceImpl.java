package com.starcloud.ops.business.app.service.vsearch.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.VSearchClient;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchUpscaleImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import com.starcloud.ops.business.app.feign.response.VSearchResponse;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
@Slf4j
@Service
public class VSearchServiceImpl implements VSearchService {

    @Resource
    private VSearchClient vSearchClient;

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<VSearchImage> generateImage(VSearchImageRequest request) {
        // 生成图片
        VSearchResponse<List<VSearchImage>> response = vSearchClient.generateImage(request);
        // 校验响应结果
        validateImagesResponse(response);
        // 返回结果
        return response.getResult();
    }

    /**
     * 图片放大
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<VSearchImage> upscaleImage(VSearchUpscaleImageRequest request) {
        // 生成图片
        VSearchResponse<List<VSearchImage>> response = vSearchClient.upscaleImage(request);
        // 校验响应结果
        validateImagesResponse(response);
        // 返回结果
        return response.getResult();
    }

    /**
     * 对生成图片的响应结果进行校验
     *
     * @param response 响应结果
     */
    private void validateImagesResponse(VSearchResponse<List<VSearchImage>> response) {
        // 响应结果为空
        if (Objects.isNull(response)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GENERATE_IMAGE_EMPTY);
        }

        // 如果成功，且有数据，则直接返回
        if (response.getSuccess() && CollectionUtil.isNotEmpty(response.getResult())) {
            return;
        }

        // 如果成功，但是没有数据
        if (response.getSuccess() && CollectionUtil.isEmpty(response.getResult())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GENERATE_IMAGE_EMPTY);
        }

        // 错误码和错误信息都不为空
        if (Objects.nonNull(response.getCode()) && StringUtils.isNotBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(response.getCode(), response.getMessage()));
        }

        // 错误码为空，错误信息不为空
        if (Objects.isNull(response.getCode()) && StringUtils.isNotBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getCode(), response.getMessage()));
        }

        // 错误码不为空，错误信息为空
        if (Objects.nonNull(response.getCode()) && StringUtils.isBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(response.getCode(), ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getMsg()));
        }

        // 其余情况，抛出默认错误码
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE);
    }

}
