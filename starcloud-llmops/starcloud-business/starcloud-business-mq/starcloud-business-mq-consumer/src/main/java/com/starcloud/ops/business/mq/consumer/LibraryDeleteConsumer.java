package com.starcloud.ops.business.mq.consumer;

import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import com.starcloud.ops.business.mq.message.LibraryDeleteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Slf4j
@Component
public class LibraryDeleteConsumer extends AbstractRedisStreamMessageListener<LibraryDeleteMessage> {

    @Resource
    private BusinessJobService businessJobService;

    @Override
    public void onMessage(LibraryDeleteMessage message) {
        log.info("删除素材库消息 [消息内容({})]", message);
        BusinessJobRespVO jobRespVO = businessJobService.getByForeignKey(message.getLibraryUid());
        if (Objects.nonNull(jobRespVO)) {
            businessJobService.delete(jobRespVO.getUid());
        }
    }
}
