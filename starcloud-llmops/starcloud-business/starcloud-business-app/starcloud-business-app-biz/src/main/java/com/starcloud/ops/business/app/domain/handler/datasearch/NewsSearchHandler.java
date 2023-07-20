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

/**
 * 通过 Google搜索新闻，并只返回 URL 列表
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public class NewsSearchHandler extends BaseHandler<NewsSearchHandler.Request, NewsSearchHandler.Response> {

    private String name = "NewsSearchHandler";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";

    private static SerpAPITool serpAPITool = new SerpAPITool();

    @Override
    public BenefitsTypeEnums getBenefitsType() {
        return null;
    }


    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        String query = context.getRequest().getQ();

        SerpAPITool.Request request = new SerpAPITool.Request();
        request.setQ(query);

        String content = serpAPITool.run(request, false, null);

        HandlerResponse<Response> handlerResponse = new HandlerResponse<Response>();

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
