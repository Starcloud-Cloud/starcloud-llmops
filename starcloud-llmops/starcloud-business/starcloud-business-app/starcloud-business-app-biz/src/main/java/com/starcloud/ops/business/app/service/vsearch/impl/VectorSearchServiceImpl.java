package com.starcloud.ops.business.app.service.vsearch.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.VectorSearchClient;
import com.starcloud.ops.business.app.feign.request.VectorSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VectorSearchImage;
import com.starcloud.ops.business.app.feign.response.VectorSearchResponse;
import com.starcloud.ops.business.app.service.vsearch.VectorSearchService;
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
public class VectorSearchServiceImpl implements VectorSearchService {

    @Resource
    private VectorSearchClient vectorSearchClient;

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<VectorSearchImage> generateImage(VectorSearchImageRequest request) {
        // 生成图片
        VectorSearchResponse<List<VectorSearchImage>> response = vectorSearchClient.generateImage(request);
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
    private void validateImagesResponse(VectorSearchResponse<List<VectorSearchImage>> response) {
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
