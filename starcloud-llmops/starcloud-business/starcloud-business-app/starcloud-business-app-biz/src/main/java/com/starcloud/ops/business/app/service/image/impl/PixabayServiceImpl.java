package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.starcloud.ops.business.app.service.image.impl.dto.request.PixabayImageRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.*;

/**
 * Pixabay
 */
@Slf4j
@Service
public class PixabayServiceImpl {


    private static final List<String> PIXABAY_API_KEYS = initPixabayApiKeys();

    private static final BlockingQueue<String> PIXABAY_API_KEYS_QUEUE = new LinkedBlockingQueue<>(PIXABAY_API_KEYS);

    /**
     * Pixabay API key 限制请求数
     */
    private static final long KEY_BLOCKING_TIME = 1000; // 单位：毫秒

    /**
     * 获取Pixabay 图片
     */
    public static final String PIXABAY_IMAGE_REQUEST_URL =
            "https://pixabay.com/api/";


    public static String getPixabayImageResult(PixabayImageRequestDTO requestDTO) throws InterruptedException {

        PixabayImageRequestDTO dto = new PixabayImageRequestDTO(requestDTO);

        String key = dto.getKey();
        if (key == null || key.isEmpty()) {
            key = PIXABAY_API_KEYS_QUEUE.poll(KEY_BLOCKING_TIME, TimeUnit.MILLISECONDS);
            if (key != null) dto.setKey(key);
            throw exception(PIXABAY_API_KEYS_LIMIT);
        }

        String url = buildRequestUrl(dto);

        try {
            HttpResponse execute = HttpRequest.post(url).timeout(10000).execute(); // 设置超时时间为10秒

            if (execute.isOk()) {
                return execute.body();
            } else if (execute.getStatus() == 429) {
                return handleRateLimit(dto);
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

    private static String buildRequestUrl(PixabayImageRequestDTO requestDTO) {
        Map<String, Object> paramMap = BeanUtil.beanToMap(requestDTO, false, true);
        return HttpUtil.urlWithForm(PIXABAY_IMAGE_REQUEST_URL, paramMap, Charset.defaultCharset(), true);
    }

    private static String handleRateLimit(PixabayImageRequestDTO requestDTO) {

        String newKey;

        try {
            newKey = PIXABAY_API_KEYS_QUEUE.poll(KEY_BLOCKING_TIME, TimeUnit.MILLISECONDS);
            if (newKey != null) {
                requestDTO.setKey(newKey);
                return getPixabayImageResult(requestDTO);
            } else {
                log.error("All Pixabay keys are currently restricted.");
                throw exception(PIXABAY_API_KEYS_LIMIT);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Key selection interrupted", e);
            throw exception(PIXABAY_API_KEYS_LIMIT);
        }
    }

    private static List<String> initPixabayApiKeys() {
        // Pixabay API key  一分钟限制请求 100 次 当前请求超出限制,更换下一个 key
        String[] keys = new String[]{
                "43908057-6d3be95c754e179c69a583867", // Cusack Alan
                "43908057-6d3be95c754e179c69a583861",
                "43908057-6d3be95c754e179c69a583862",
        };
        return Arrays.asList(keys);
    }


}
