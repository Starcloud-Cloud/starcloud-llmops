package com.starcloud.ops.business.app.domain.handler.datasearch;

import cn.kstry.framework.core.annotation.Invoke;
import cn.kstry.framework.core.annotation.NoticeSta;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.action.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 适配 py代码逻辑
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@TaskComponent(name = "PyAdapterActionHandler")
public class PyAdapterActionHandler extends FlowStepHandler {

    @NoticeSta
    @TaskService(name = "PyAdapterActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        WorkflowStepWrapper appStepWrapper = context.getCurrentStepWrapper();

        String prompt = appStepWrapper.getContextVariablesValue("prompt", "hi, what you name?");


        ActionResponse appStepResponse = new ActionResponse();
        appStepResponse.setSuccess(false);

        return appStepResponse;
    }


    @Override
    public Class<?> getInputCls(AppContext context) {
        return Request.class;
    }

    @Override
    public JsonNode getInputSchemas(AppContext context) {
        return null;
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("python code snippets that need to be executed.")
        private String py;

    }
}
