package com.starcloud.ops.business.mq.consumer;

import cn.iocoder.yudao.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import com.starcloud.ops.business.app.service.chat.ChatExpandConfigService;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.dataset.service.datasetsourcedata.DatasetSourceDataService;
import com.starcloud.ops.business.mq.message.AppDeleteMessage;
import com.starcloud.ops.business.share.service.ConversationShareService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class AppDeleteConsumer extends AbstractRedisStreamMessageListener<AppDeleteMessage> {

    @Resource
    private DatasetSourceDataService datasetSourceDataService;

    @Resource
    private ChatExpandConfigService chatExpandConfigService;

    @Resource
    private ConversationShareService conversationShareService;

    @Resource
    private CreativeMaterialManager creativeMaterialManager;

    @Override
    public void onMessage(AppDeleteMessage message) {
        log.info("删除app消息 [消息内容({})]", message);
        if (StringUtils.isBlank(message.getAppUid())) {
            return;
        }
        // 技能
        chatExpandConfigService.deleteByAppUid(message.getAppUid());
        // 分享链接
        conversationShareService.deleteShare(message.getAppUid());
        // 数据集
        datasetSourceDataService.deleteAllDataByAppId(message.getAppUid());
        // 删除素材库
        creativeMaterialManager.deleteMaterial(message.getAppUid());

    }
}
