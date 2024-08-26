package com.starcloud.ops.business.mq.producer;

import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import com.starcloud.ops.business.mq.message.LibraryDeleteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class LibraryDeleteProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;


    public void send(String libraryUid) {
        log.info("删除素材库 {}", libraryUid);
        LibraryDeleteMessage libraryDeleteMessage = new LibraryDeleteMessage();
        libraryDeleteMessage.setLibraryUid(libraryUid);
        redisMQTemplate.send(libraryDeleteMessage);
    }
}
