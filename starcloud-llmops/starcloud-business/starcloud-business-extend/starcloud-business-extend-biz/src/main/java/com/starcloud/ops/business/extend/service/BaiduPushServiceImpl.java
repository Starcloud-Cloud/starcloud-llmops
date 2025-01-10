package com.starcloud.ops.business.extend.service;

import cn.hutool.core.date.DateUtil;
import com.lark.oapi.core.utils.Jsons;
import com.starcloud.ops.business.extend.framework.baidu.core.BaiduPushClient;
import com.starcloud.ops.business.extend.framework.baidu.core.dto.BaiduPushRespDTO;
import com.starcloud.ops.business.extend.framework.feishu.config.FeiShuProperties;
import com.starcloud.ops.business.extend.framework.feishu.core.client.FeiShuClientFactory;
import com.starcloud.ops.business.extend.service.dto.baidu.PushResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BaiduPushServiceImpl {

    @Resource
    private FeiShuClientFactory feiShuClientFactory;
    @Resource
    private FeiShuProperties feiShuProperties;

    @Resource
    private BaiduPushClient baiduPushClient;


    public void push(List<PushResourceDTO> pushResourceDTOS) {
        List<String> urls = pushResourceDTOS.stream().map(PushResourceDTO::getUrl).collect(Collectors.toList());

        BaiduPushRespDTO baiduPushRespDTO = baiduPushClient.pushResource(urls);
        String status;
        if (Objects.isNull(baiduPushRespDTO)) {
            status = "失败";
        } else {
            status = "成功";
        }

        log.info("推送结果:{}", Jsons.DEFAULT.toJson(baiduPushRespDTO));
        List<Map<String, Object>> fields = pushResourceDTOS.stream()
                .map(dto -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("标题", dto.getTitle());
                    map.put("链接", dto.getUrl());
                    map.put("状态", status);
                    map.put("时间", DateUtil.now());
                    return map;
                })
                .collect(Collectors.toList());

        feiShuClientFactory.addAppRecords(fields);
    }
}

