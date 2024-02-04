package com.starcloud.ops.business.user.service.notify;

import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.business.user.service.notify.adapter.NotifyMediaAdapter;
import com.starcloud.ops.business.user.service.notify.params.NotifyParamsAbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

@Slf4j
@Component
public class NotifyFactory implements InitializingBean {

    private Map<Integer, NotifyMediaAdapter> notifyMediaMap;

    private Map<String, NotifyParamsAbstractService> notifyDataPrepareMap;

    @Override
    public void afterPropertiesSet() {
        Map<String, NotifyMediaAdapter> notifyMediaServiceMap = SpringUtil.getApplicationContext().getBeansOfType(NotifyMediaAdapter.class);
        notifyMediaMap = notifyMediaServiceMap.values().stream().collect(Collectors.toMap(notifyMediaService -> notifyMediaService.supportType().getType(), Function.identity()));

        Map<String, NotifyParamsAbstractService> notifyDataPrepareServiceMap = SpringUtil.getApplicationContext().getBeansOfType(NotifyParamsAbstractService.class);
        notifyDataPrepareMap = notifyDataPrepareServiceMap.values().stream().collect(Collectors.toMap(NotifyParamsAbstractService::support, Function.identity()));
    }

    public NotifyMediaAdapter getNotifyMedia(Integer notifyMediaType) {
        NotifyMediaAdapter service = notifyMediaMap.get(notifyMediaType);
        if (Objects.isNull(service)) {
            throw exception(NOT_SUPPORTED_NOTIFY_MEDIA, notifyMediaType);
        }
        return service;
    }

    public NotifyParamsAbstractService getDataService(String templateCode) {
        NotifyParamsAbstractService prepareService = notifyDataPrepareMap.get(templateCode);
        if (Objects.isNull(prepareService)) {
            throw exception(NOT_SUPPORTED_TEMPLATE_CODE, templateCode);
        }
        return prepareService;
    }
}
