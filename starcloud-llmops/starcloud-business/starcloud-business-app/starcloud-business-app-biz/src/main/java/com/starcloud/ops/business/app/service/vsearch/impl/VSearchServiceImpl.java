package com.starcloud.ops.business.app.service.vsearch.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.VSearchClient;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchUpscaleImageRequest;
import com.starcloud.ops.business.app.feign.dto.VSearchImage;
import com.starcloud.ops.business.app.feign.response.VSearchResponse;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * 图片尺寸正则表达式
     */
    private static final Pattern PX_REGEX = Pattern.compile("\\d+(?:\\.\\d+)?\\d*x\\d+(?:\\.\\d+)?\\d*");

    /**
     * 图尺寸比较正则表达式
     */
    private static final Pattern BIG_REGEX = Pattern.compile("\\d+(?:\\.\\d+)?\\d*>\\d+(?:\\.\\d+)?\\d*");

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<VSearchImage> generateImage(VSearchImageRequest request) {
        log.info(JSONUtil.toJsonPrettyStr(request));
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
        log.info(JSONUtil.toJsonPrettyStr(request));
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
            throw ServiceExceptionUtil.exception(failureErrorCode(response.getCode(), response.getMessage()));
        }

        // 错误码为空，错误信息不为空
        if (Objects.isNull(response.getCode()) && StringUtils.isNotBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(failureErrorCode(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getCode(), response.getMessage()));
        }

        // 错误码不为空，错误信息为空
        if (Objects.nonNull(response.getCode()) && StringUtils.isBlank(response.getMessage())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(response.getCode(), ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getMsg()));
        }

        // 其余情况，抛出默认错误码
        throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE);
    }

    /**
     * 处理失败的错误码
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 错误码
     */
    private static ErrorCode failureErrorCode(Integer code, String message) {
        // 如果是图片尺寸不是64的倍数
        if (StringUtils.contains(message, "image dimensions must be multiples of 64")) {
            Matcher matcher = PX_REGEX.matcher(message);
            if (matcher.find()) {
                return new ErrorCode(310400600, "图片尺寸必须是64的倍数(" + matcher.group() + ")。");
            } else {
                return new ErrorCode(310400600, "图片尺寸必须是64的倍数。");
            }
        }
        // 图片过大
        if (StringUtils.contains(message, "image too large")) {
            Matcher matcher = BIG_REGEX.matcher(message);
            if (matcher.find()) {
                return new ErrorCode(310400601, "图片尺寸过大(" + matcher.group() + ")，图片尺寸不能超过1024x1024。");
            } else {
                return new ErrorCode(310400601, "图片尺寸过大，图片尺寸不能超过1024x1024。");
            }
        }
        // 原始图片尺寸过大
        if (StringUtils.contains(message, "Input image size is too large")) {
            Matcher matcher = BIG_REGEX.matcher(message);
            if (matcher.find()) {
                return new ErrorCode(310400602, "原始图片尺寸过大(" + matcher.group() + ")。");
            } else {
                return new ErrorCode(310400602, "原始图片尺寸过大。");
            }
        }
        if (StringUtils.contains(message, "Requested image size is too large")) {
            Matcher matcher = BIG_REGEX.matcher(message);
            if (matcher.find()) {
                return new ErrorCode(310400603, "请求图片尺寸过大(" + matcher.group() + ")。");
            } else {
                return new ErrorCode(310400603, "请求图片尺寸过大。");
            }
        }
        if (StringUtils.contains(message, "Invalid prompts detected")) {
            return new ErrorCode(310400604, "无效的提示信息(提示词中可能包含敏感词)。");
        }

        if (StringUtils.contains(message, "Time-out")) {
            return new ErrorCode(310400701, "请求超时。请稍候再试！");
        }
        return new ErrorCode(code, message);
    }

}
