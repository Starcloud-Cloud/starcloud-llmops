package com.starcloud.ops.business.app.domain.handler.datasearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * google 搜索内容，只返回 主要的结果摘要
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@Component
public class GoogleSearchHandler extends BaseHandler<GoogleSearchHandler.Request, GoogleSearchHandler.Response> {

    private String userName = "互联网搜索";

    private String userDescription = "可以自动联网查询实时信息，保证内容实时准确";

    private String name = "GoogleSearchHandler";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";

    private static SerpAPITool serpAPITool = new SerpAPITool();

    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String query = context.getRequest().getQ();

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ(query);

        String content = serpAPITool.run(request, false, null);

        HandlerResponse<Response> handlerResponse = new HandlerResponse();

        handlerResponse.setSuccess(true);
        handlerResponse.setOutput(new Response(content));

        return handlerResponse;
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String q;

    }


    @Data
    public static class Response {

        private String content;

        public Response(String content) {
            this.content = content;
        }
    }

}
