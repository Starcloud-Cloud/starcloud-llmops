package com.starcloud.ops.business.app.domain.handler.textgeneration;

import cn.kstry.framework.core.annotation.NoticeSta;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppStepResponse;
import com.starcloud.ops.business.app.domain.handler.BaseStepHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "OpenAIChatStepHandler")
@Component
public class OpenAIChatStepHandler extends BaseStepHandler {

    @NoticeSta
    @TaskService(name = "OpenAIChatStepHandler")
    @Override
    public AppStepResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        log.info("handler123: {}, {}", scopeDataOperator.getRequestId(),  context.getApp().getConfig());



        AppStepResponse appStepResponse = new AppStepResponse();
        appStepResponse.setData(12377655);
        appStepResponse.setSuccess(true);

        return appStepResponse;
    }
}
