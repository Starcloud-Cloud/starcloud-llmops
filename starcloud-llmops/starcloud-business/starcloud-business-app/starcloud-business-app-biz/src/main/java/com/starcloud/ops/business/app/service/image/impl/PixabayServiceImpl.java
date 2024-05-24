package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.util.KeyUsageLimit;
import com.starcloud.ops.business.app.service.image.impl.dto.repose.PixabayImageResult;
import com.starcloud.ops.business.app.service.image.impl.dto.request.PixabayImageRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

/**
 * Pixabay
 */
@Slf4j
@Service
public class PixabayServiceImpl {

    @Resource
    private KeyUsageLimit keyUsageLimit;

    /**
     * 获取Pixabay 图片
     */
    public static final String PIXABAY_IMAGE_REQUEST_URL =
            "https://pixabay.com/api/";


    public PixabayImageResult getPixabayImage(PixabayImageRequestDTO requestDTO) {
        String stringResult = executePixabayRequest(requestDTO);
        return JSONUtil.toBean(stringResult, PixabayImageResult.class);
    }

    public String executePixabayRequest(PixabayImageRequestDTO requestDTO) {
        PixabayImageRequestDTO dto = new PixabayImageRequestDTO(requestDTO);
        String key = requestDTO.getKey();
        if (key == null || key.isEmpty()) {
            key = keyUsageLimit.getNextKey();
            if (key != null) {
                dto.setKey(key);
            } else {
                throw exception(PIXABAY_API_KEYS_LIMIT);
            }
        }

        String url = buildRequestUrl(dto);
        log.info("【Pixabay 请求地址为:{}】",url);

        try {
            HttpResponse execute = HttpRequest.post(url).timeout(10000).execute(); // 设置超时时间为10秒

            if (execute.isOk()) {
                return execute.body();
            } else if (execute.getStatus() == 429) {
                return handleRateLimit(requestDTO);
            } else {
                log.error("【Pixabay 获取图片请求异常，请检查网络配置】{}", execute);
                throw exception(PIXABAY_API_KEYS_REQUEST_ERROR);
            }
        } catch (Exception e) {
            // 处理网络请求过程中的异常，如超时、连接错误等
            log.error("【Pixabay 获取图片网络异常，请检查网络配置】{}", e.getMessage(), e);
            throw exception(PIXABAY_API_KEYS_NETWORK_ERROR);
        }
    }

    private  String buildRequestUrl(PixabayImageRequestDTO requestDTO) {
        Map<String, Object> paramMap = BeanUtil.beanToMap(requestDTO, false, true);
        return HttpUtil.urlWithForm(PIXABAY_IMAGE_REQUEST_URL, paramMap, Charset.defaultCharset(), false);
    }

    private  String handleRateLimit(PixabayImageRequestDTO requestDTO) {

        String newKey = keyUsageLimit.getNextKey();
        if (newKey != null) {
            requestDTO.setKey(newKey);
            return executePixabayRequest(requestDTO);
        } else {
            log.error("All Pixabay keys are currently restricted.");
            throw exception(PIXABAY_API_KEYS_LIMIT);
        }

    }


}
