package com.starcloud.ops.business.app.service.plugins.handler;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.response.PluginRespVO;
import com.starcloud.ops.business.app.enums.plugin.PlatformEnum;
import com.starcloud.ops.business.app.service.plugins.impl.PluginsDefinitionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLATFORM_NOT_SUPPORT;

@Slf4j
@Component
public class PluginExecuteFactory implements ApplicationContextAware {

    private static Map<String, PluginExecuteHandler> HANDLER_MAP;

    @Resource
    private PluginsDefinitionServiceImpl pluginsDefinitionService;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Collection<PluginExecuteHandler> values = applicationContext.getBeansOfType(PluginExecuteHandler.class).values();
        HANDLER_MAP = values.stream().collect(Collectors.toMap(PluginExecuteHandler::supportPlatform, Function.identity()));
    }

    public static PluginExecuteHandler getHandler(String platformCode) {
        PluginExecuteHandler pluginExecuteHandler = HANDLER_MAP.get(platformCode);
        if (Objects.isNull(pluginExecuteHandler)) {
            throw exception(PLATFORM_NOT_SUPPORT, platformCode);
        }
        return pluginExecuteHandler;
    }

    public PluginExecuteHandler getHandlerByUid(String pluginUid) {
        PluginRespVO detail = pluginsDefinitionService.detail(pluginUid);
        return getHandler(detail.getType());
    }
}
