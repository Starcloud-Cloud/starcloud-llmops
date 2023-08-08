package com.starcloud.ops.business.user.service.factory;

import com.starcloud.ops.business.user.service.CommunicationService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommunicationToolsFactroy implements ApplicationContextAware {

    private static Map<Integer, CommunicationService> communicationServiceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, CommunicationService> chatServiceMap = applicationContext.getBeansOfType(CommunicationService.class);
        communicationServiceMap = new HashMap<>(chatServiceMap.size());
        for (String key : chatServiceMap.keySet()) {
            CommunicationService service = chatServiceMap.get(key);
            communicationServiceMap.put(service.getTypeCode(), service);
        }
    }

    public static CommunicationService getService(Integer code) {
        return communicationServiceMap.get(code);
    }
}
