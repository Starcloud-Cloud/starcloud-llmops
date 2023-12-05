package com.starcloud.ops.business.app.service.poster.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.feign.PosterImageClient;
import com.starcloud.ops.business.app.feign.dto.PosterDTO;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateDTO;
import com.starcloud.ops.business.app.feign.request.poster.PosterRequest;
import com.starcloud.ops.business.app.feign.request.poster.PosterTemplateQuery;
import com.starcloud.ops.business.app.feign.response.PosterResponse;
import com.starcloud.ops.business.app.service.poster.PosterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * 获取模板列表
     *
     * @return 模板列表
     */
    @Override
    public List<PosterTemplateDTO> templates() {
        PosterTemplateQuery query = new PosterTemplateQuery();
        PosterResponse<List<PosterTemplateDTO>> response = posterImageClient.templates(query);
        validateResponse(response, "获取海报模板列表失败");
        List<PosterTemplateDTO> templates = response.getData();
        if (CollectionUtil.isEmpty(templates)) {
            return Collections.emptyList();
        }
        return templates;
    }

    /**
     * 生成海报
     *
     * @param request 请求
     * @return 海报URL
     */
    @Override
    public String poster(PosterRequest request) {
        log.info("[Poster] 调用海报生成接口开始......");
        PosterResponse<PosterDTO> response = posterImageClient.poster(request);
        validateResponse(response, "海报生成失败");
        PosterDTO poster = response.getData();
        String url = poster.getUrl();
        if (StringUtils.isBlank(url)) {
            log.error("[Poster] 调用海报生成接口失败：错误信息: {}", CreativeErrorCodeConstants.POSTER_URL_IS_BLANK.getMsg());
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_URL_IS_BLANK);
        }
        log.info("[Poster] 调用海报生成接口完成：海报URL：{}", url);
        return url;
    }

    /**
     * 校验海报请求响应
     *
     * @param response 响应
     */
    private void validateResponse(PosterResponse<?> response, String errorMessage) {
        if (Objects.isNull(response)) {
            log.error("[poster] 海报请求响应为空");
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_RESPONSE_IS_NULL, errorMessage);
        }
        if (!response.getSuccess()) {
            String message = StringUtils.isBlank(response.getMessage()) ? "海报请求响应失败" : response.getMessage();
            log.error("[poster] 海报请求响应失败， code: {}, message:{}", response.getCode(), message);
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_RESPONSE_IS_NOT_SUCCESS, errorMessage, message);
        }
        if (Objects.isNull(response.getData())) {
            log.error("[poster] 海报请求响应数据为空");
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_RESPONSE_DATA_IS_NULL, errorMessage);
        }
    }
}
