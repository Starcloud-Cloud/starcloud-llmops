package cn.iocoder.yudao.module.system.mq.producer.permission;

import cn.iocoder.yudao.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.yudao.module.system.mq.message.permission.MenuRefreshMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Menu 菜单相关消息的 Producer
 */
@Component
@Deprecated
public class MenuProducer {

    @Resource
    private RedisMQTemplate redisMQTemplate;

    /**
     * 发送 {@link MenuRefreshMessage} 消息
     */
    public void sendMenuRefreshMessage() {
        MenuRefreshMessage message = new MenuRefreshMessage();
//        redisMQTemplate.send(message);
    }

}
