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
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
public class GoogleSearchActionHandler extends FlowStepHandler {

    private static SerpAPITool serpAPITool = new SerpAPITool("");

    @NoticeSta
    @TaskService(name = "GoogleSearchActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {


        String query = context.getContextVariablesValue("query", "apple");

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ(query);

        String content = serpAPITool.run(request, false, null);

        ActionResponse appStepResponse = new ActionResponse();
        appStepResponse.setSuccess(true);
        appStepResponse.setAnswer(content);

        return appStepResponse;
    }

    @Override
    public Class<?> getInputCls(AppContext context) {

        return Request.class;
    }

    @Override
    public JsonNode getInputSchemas(AppContext context) {

        Map<String, Object> stepParams = context.getContextVariablesValues();

        // 根据步骤的参数 生成 jsonSchemas

        return null;
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String q;

    }


}
