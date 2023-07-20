package com.starcloud.ops.business.app.workflow.app.interceptor;

import cn.kstry.framework.core.bus.ScopeDataOperator;
import cn.kstry.framework.core.engine.interceptor.Iter;
import cn.kstry.framework.core.engine.interceptor.IterData;
import cn.kstry.framework.core.engine.interceptor.TaskInterceptor;
import cn.kstry.framework.core.resource.service.ServiceNodeResource;
import cn.kstry.framework.core.role.Role;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.log.api.LogAppApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

@Slf4j
//@Component
public class AppServiceNodeInterceptor implements TaskInterceptor {

    @Autowired
    private LogAppApi logAppApi;


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

        return dataOperator.getReqScope() instanceof AppContext;
    }

    @Override
    public Object invoke(Iter iter) {
        ServiceNodeResource serviceNode = iter.getServiceNode();
        ScopeDataOperator dataOperator = iter.getDataOperator();
        Role role = iter.getRole();

//        AppContext appContext = dataOperator.getReqScope();
//
//        TimeInterval timer = DateUtil.timer();

        ActionResponse response = (ActionResponse) iter.next();

//        Long elapsed = timer.interval();
//
//        this.createAppMessage(appContext, response, elapsed);

        log.info("ComponentName: {}, ServiceName: {}, AbilityName: {}, data: {}", serviceNode.getComponentName(), serviceNode.getServiceName(), serviceNode.getAbilityName(), response);
        return response;
    }


}

