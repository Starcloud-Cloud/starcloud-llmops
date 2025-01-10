package com.starcloud.ops.business.extend.framework.baidu.core.impl;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.extend.framework.baidu.core.BaiduPushClient;
import com.starcloud.ops.business.extend.framework.baidu.core.dto.BaiduPushRespDTO;
import com.starcloud.ops.business.extend.framework.feishu.core.client.FeiShuClientFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@AllArgsConstructor
@Service
public class BaiduPushClientImpl implements BaiduPushClient {

    @Resource
    private FeiShuClientFactory feiShuClientFactory;


    private static final int BATCH_SIZE = 10;
    private static final int API_TOKEN = 100;

    private static final String URL = "";


    /**
     * 数据推送
     *
     * @param urls 推送的链接
     */
    @Override
    public BaiduPushRespDTO pushResource(List<String> urls) {
        return httpRequest(urls, BaiduPushRespDTO.class);
    }


    /**
     * 快递鸟 API 请求
     *
     * @param <Resp> 每个请求的响应结构 Resp DTO
     */
    private <Resp> Resp httpRequest(List<String> urls, Class<Resp> respClass) {

        try {
            // 将URL列表分割成多个较小的列表
            List<List<String>> batches = splitList(urls);

            // 遍历每个较小的列表，发送推送请求
            for (List<String> batch : batches) {
                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set("User-Agent", "curl/7.12.1");
                headers.set("Host", "data.zz.baidu.com");

                RestTemplate restTemplate = new RestTemplate();
                // 将URL列表转换为字符串
                String urlListString = String.join("\n", batch);
                // 创建请求体
                HttpEntity<String> requestEntity = new HttpEntity<>(urlListString, headers);

                // 发送POST请求
                ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);

                log.info("[httpRequest]的响应结果({})", responseEntity);
                // 处理响应
                if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                    return null;
                }
                return JsonUtils.parseObject(responseEntity.getBody(), respClass);
            }
        } catch (Exception e) {
            return null;
        }
        return null;

    }


    // 将列表分割成多个较小的列表
    private <T> List<List<T>> splitList(List<T> list) {
        List<List<T>> batches = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i += BaiduPushClientImpl.BATCH_SIZE) {
            int end = Math.min(size, i + BaiduPushClientImpl.BATCH_SIZE);
            batches.add(list.subList(i, end));
        }
        return batches;
    }
}
