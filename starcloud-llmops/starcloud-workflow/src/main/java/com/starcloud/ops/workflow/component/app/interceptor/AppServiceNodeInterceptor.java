package com.starcloud.ops.workflow.component.app.interceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.IdUtil;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import cn.kstry.framework.core.engine.interceptor.Iter;
import cn.kstry.framework.core.engine.interceptor.IterData;
import cn.kstry.framework.core.engine.interceptor.TaskInterceptor;
import cn.kstry.framework.core.resource.service.ServiceNodeResource;
import cn.kstry.framework.core.role.Role;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppStepResponse;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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

        AppStepResponse response = (AppStepResponse) iter.next();

//        Long elapsed = timer.interval();
//
//        this.createAppMessage(appContext, response, elapsed);

        log.info("ComponentName: {}, ServiceName: {}, AbilityName: {}, data: {}", serviceNode.getComponentName(), serviceNode.getServiceName(), serviceNode.getAbilityName(), response);
        return response;
    }


}

