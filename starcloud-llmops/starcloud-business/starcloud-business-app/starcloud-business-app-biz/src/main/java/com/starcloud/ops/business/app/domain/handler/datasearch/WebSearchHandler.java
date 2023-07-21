package com.starcloud.ops.business.app.domain.handler.datasearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 页面内容爬取
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public class WebSearchHandler extends BaseHandler<WebSearchHandler.Request, String> {

    private String name = "WebSearchHandler";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output will be the text response of the GET request.";

    private static RequestsGetTool requestsGetTool = new RequestsGetTool();


    @Override
    protected HandlerResponse<String> _execute(HandlerContext<Request> context) {


        RequestsGetTool.Request request = new RequestsGetTool.Request(context.getRequest().getUrl());

        HandlerResponse<String> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);

        requestsGetTool.setCallbackManager(new CallbackManager());
        String content = requestsGetTool.run(request);

        //@todo 简单过滤html，或格式化md 格式

        handlerResponse.setOutput(content);

        return handlerResponse;

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
