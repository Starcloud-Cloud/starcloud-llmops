package com.starcloud.ops.business.mq.consumer;

import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginConfigRespVO;
import com.starcloud.ops.business.app.service.plugins.PluginConfigService;
import com.starcloud.ops.business.mq.message.LibraryDeleteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class LibraryDeleteConsumer extends AbstractRedisStreamMessageListener<LibraryDeleteMessage> {

    @Resource
    private PluginConfigService pluginConfigService;

    @Override
    public void onMessage(LibraryDeleteMessage message) {
        log.info("删除素材库消息 [消息内容({})]", message);
        List<PluginConfigRespVO> configList = pluginConfigService.configList(message.getLibraryUid());
        configList.forEach(config -> {
            pluginConfigService.delete(config.getUid(), true);
        });
    }
}
