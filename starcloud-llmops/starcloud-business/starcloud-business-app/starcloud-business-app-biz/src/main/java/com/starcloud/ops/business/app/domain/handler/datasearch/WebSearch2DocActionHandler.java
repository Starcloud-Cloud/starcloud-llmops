package com.starcloud.ops.business.app.domain.handler.datasearch;

import cn.kstry.framework.core.annotation.*;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.context.AppContext;
import com.starcloud.ops.business.app.domain.entity.action.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.params.JsonParamsEntity;
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面内容搜索，生成总结并创建索引
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@TaskComponent(name = "WebSearch2DocActionHandler")
public class WebSearch2DocActionHandler extends FlowStepHandler {

    private String name = "WebSearch2DocActionHandler";

    private String description = "The output should be a json string with two keys: \"content\" and\" docKey\". The value of \"content\" is a summary of the content of the website, and the value of\" docKey\" is the tag of the website to point to.";

    private static RequestsGetTool requestsGetTool = new RequestsGetTool();

    @NoticeSta
    @TaskService(name = "WebSearch2DocActionHandler", invoke = @Invoke(timeout = 180000))
    @Override
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {

        ActionResponse appStepResponse = new ActionResponse();
        appStepResponse.setSuccess(false);

        if (context.getJsonParams() != null) {

            requestsGetTool.setCallbackManager(new CallbackManager());

            RequestsGetTool.Request request = context.getJsonParams().parse(RequestsGetTool.Request.class);

            String content = requestsGetTool.run(request);

            appStepResponse.setSuccess(true);
            appStepResponse.setAnswer(content);

            Response result = new Response();

            result.setContent("Today’s Doodle, illustrated by Frankfurt-based guest artist Cynthia Kittler, celebrates Jewish German poet and artist Else Lasker-Schüler, widely considered one of the greatest lyricists to write in the German language. On this day in 1937, a Swiss newspaper published her famous poem “Mein blaues Klavier” (“My Blue Piano”), which is referenced in today’s Doodle artwork by the piano keys depicted on the camel’s back, alongside other symbols of Lasker-Schüler’s life and work.");
            result.setDocKey("docKey12212121313");

            appStepResponse.setJsonParams(new JsonParamsEntity().setData(result));

        } else {

            String query = context.getContextVariablesValue("query", "apple");

            appStepResponse.setSuccess(true);
            appStepResponse.setAnswer(query);
        }

        return appStepResponse;
    }

    @Override
    public Class<?> getInputCls(AppContext context) {

        return Request.class;
    }

    @Override
    public JsonNode getInputSchemas(AppContext context) {

       // Map<String, Object> stepParams = context.getContextVariablesValues();

        // 根据步骤的参数 生成 jsonSchemas

        return OpenAIUtils.serializeJsonSchema(Request.class);
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("a website url")
        private String url;

    }


    @Data
    public static class Response {

        private String content;

        private String docKey;

    }

}
