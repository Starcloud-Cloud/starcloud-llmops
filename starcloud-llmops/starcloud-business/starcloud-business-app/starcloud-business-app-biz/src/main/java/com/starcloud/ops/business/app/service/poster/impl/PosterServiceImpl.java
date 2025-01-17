package com.starcloud.ops.business.app.service.poster.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.PosterImageClient;
import com.starcloud.ops.business.app.feign.dto.PosterImage;
import com.starcloud.ops.business.app.feign.dto.PosterTemplate;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateType;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.feign.response.PosterResponse;
import com.starcloud.ops.business.app.service.poster.PosterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Service
public class PosterServiceImpl implements PosterService {

    @Resource
    private PosterImageClient posterImageClient;

    /**
     * 获取模板详情
     *
     * @param code 模板ID
     * @return 模板
     */
    @Override
    public PosterTemplate getTemplate(String code) {
        PosterResponse<PosterTemplate> response = posterImageClient.getTemplate(code);
        validateResponse(response, "获取海报模板失败");
        AppValidate.notNull(response.getData(), "海报模板获取失败！");
        return response.getData();
    }

    /**
     * 获取模板列表
     *
     * @return 模板列表
     */
    @Override
    public List<PosterTemplate> listTemplate() {
        PosterResponse<List<PosterTemplate>> response = posterImageClient.listTemplate();
        validateResponse(response, "获取海报模板列表失败");
        List<PosterTemplate> templates = response.getData();
        if (CollectionUtil.isEmpty(templates)) {
            return Collections.emptyList();
        }
        return templates;
    }

    /**
     * 根据类型分组获取模板列表
     *
     * @return 模板列表
     */
    @Override
    public List<PosterTemplateType> listPosterTemplateType() {
        PosterResponse<List<PosterTemplateType>> response = posterImageClient.listTemplateType();
        validateResponse(response, "获取海报模板类型列表失败");
        List<PosterTemplateType> templateTypes = response.getData();
        if (CollectionUtil.isEmpty(templateTypes)) {
            return Collections.emptyList();
        }
        return templateTypes;
    }

    /**
     * 生成海报
     *
     * @param request 请求
     * @return 海报URL
     */
    @Override
    public List<PosterImage> poster(PosterRequest request) {
        try {
            PosterResponse<List<PosterImage>> response = posterImageClient.poster(request);
            validateResponse(response, "海报生成失败");
            List<PosterImage> posterList = response.getData();
            if (CollectionUtil.isEmpty(posterList)) {
                throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(), "海报图片生成失败: 生成结果不存在！");
            }

            for (PosterImage posterImage : posterList) {
                if (StringUtils.isBlank(posterImage.getUrl())) {
                    throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(), "海报图片生成失败: 生成结果地址为空！");
                }
            }
            return posterList;
        } catch (ServiceException exception) {
            log.error("[Poster][ServiceException] 调用海报生成接口失败：错误信息: {}", exception.getMessage());
            throw exception;
        } catch (HttpMessageNotReadableException exception) {
            log.error("[Poster][HttpMessageNotReadableException] 调用海报生成接口失败：错误信息: {}", exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_POSTER_FAILURE, exception.getMessage(), exception);
        } catch (Exception exception) {
            log.error("[Poster][Exception] 调用海报生成接口失败：错误信息: {}", exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_POSTER_FAILURE, exception.getMessage(), exception);
        }
    }

    /**
     * 校验海报请求响应
     *
     * @param response 响应
     */
    private void validateResponse(PosterResponse<?> response, String errorMessage) {
        if (Objects.isNull(response)) {
            throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(), errorMessage);
        }
        if (!response.getSuccess()) {
            String message = StringUtils.isBlank(response.getMessage()) ? "海报请求响应失败" : response.getMessage();
            throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(), message);
        }
        if (Objects.isNull(response.getData())) {
            throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_POSTER_FAILURE.getCode(), errorMessage);
        }
    }
}
