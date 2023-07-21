package com.starcloud.ops.business.app.domain.handler.datasearch;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 页面内容爬取，并创建对应索引和总结
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
public class WebSearch2DocHandler extends BaseHandler<WebSearch2DocHandler.Request, WebSearch2DocHandler.Response> {

    private String name = "WebSearch2DocHandler";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output should be a json string with two keys: \"content\" and\" docKey\". The value of \"content\" is a summary of the content of the website, and the value of\" docKey\" is the tag of the website to point to.";

    private static RequestsGetTool requestsGetTool = new RequestsGetTool();


    @Override
    protected HandlerResponse<Response> _execute(HandlerContext<Request> context) {

        HandlerResponse<Response> handlerResponse = new HandlerResponse();
        handlerResponse.setSuccess(false);

        String url = context.getRequest().getUrl();
        Response result = new Response();

        result.setSummary("Today’s Doodle, illustrated by Frankfurt-based guest artist Cynthia Kittler, celebrates Jewish German poet and artist Else Lasker-Schüler, widely considered one of the greatest lyricists to write in the German language. On this day in 1937, a Swiss newspaper published her famous poem “Mein blaues Klavier” (“My Blue Piano”), which is referenced in today’s Doodle artwork by the piano keys depicted on the camel’s back, alongside other symbols of Lasker-Schüler’s life and work.");
        result.setDocKey("docKey12212121313");

        handlerResponse.setSuccess(true);

        handlerResponse.setMessage(url);
        handlerResponse.setAnswer(result.getSummary());

        handlerResponse.setOutput(result);


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

        private String summary;

        private String docKey;

    }

}
