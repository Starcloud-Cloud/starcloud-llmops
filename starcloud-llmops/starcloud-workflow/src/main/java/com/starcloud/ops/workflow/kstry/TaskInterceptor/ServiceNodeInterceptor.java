package com.starcloud.ops.workflow.kstry.TaskInterceptor;

import cn.kstry.framework.core.bus.ScopeDataOperator;
import cn.kstry.framework.core.engine.interceptor.Iter;
import cn.kstry.framework.core.engine.interceptor.IterData;
import cn.kstry.framework.core.engine.interceptor.TaskInterceptor;
import cn.kstry.framework.core.resource.service.ServiceNodeResource;
import cn.kstry.framework.core.role.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceNodeInterceptor implements TaskInterceptor {

    /**
     * 接口有默认行为，可以不实现该方法
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 接口有默认行为，可以不实现该方法
     */
    @Override
    public boolean match(IterData iterData) {
        ScopeDataOperator dataOperator = iterData.getDataOperator();
        return true;
    }

    @Override
    public Object invoke(Iter iter) {
        ServiceNodeResource serviceNode = iter.getServiceNode();
        ScopeDataOperator dataOperator = iter.getDataOperator();
        Role role = iter.getRole();

        Object data = iter.next();
        log.info("ComponentName: {}, ServiceName: {}, AbilityName: {}, data: {}", serviceNode.getComponentName(), serviceNode.getServiceName(), serviceNode.getAbilityName(), data);
        return data;
    }
}