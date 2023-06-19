package com.starcloud.ops.business.app.domain.handler.datasearch;

import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.AppStepWrapper;
import com.starcloud.ops.business.app.domain.entity2.action.ActionResponse;
import com.starcloud.ops.business.app.domain.handler.common.StepAndFunctionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Open AI Chat 步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "GoogleSearchActionHandler")
@Component
public class GoogleSearchActionHandler extends StepAndFunctionHandler {

    @NoticeSta
    @TaskService(name = "GoogleSearchActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        AppStepWrapper appStepWrapper = context.getCurrentAppStepWrapper();

        String prompt = appStepWrapper.getContextVariablesValue("prompt", "hi, what you name?");

        Map<String, Object> variablesMaps = appStepWrapper.getContextVariablesMaps();

        ActionResponse appStepResponse = new ActionResponse();
        appStepResponse.setSuccess(false);

        return appStepResponse;
    }
}
