package com.starcloud.ops.business.mq.producer;

import cn.iocoder.yudao.framework.mq.core.RedisMQTemplate;
import com.starcloud.ops.business.mq.message.AppDeleteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class AppDeleteProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    public void send(String appUid) {
        AppDeleteMessage appDeleteMessage = new AppDeleteMessage();
        appDeleteMessage.setAppUid(appUid);
        redisMQTemplate.send(appDeleteMessage);
    }
}
