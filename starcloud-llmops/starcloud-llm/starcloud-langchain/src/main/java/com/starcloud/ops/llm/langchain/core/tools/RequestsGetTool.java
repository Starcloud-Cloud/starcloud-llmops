package com.starcloud.ops.llm.langchain.core.tools;

import cn.hutool.Hutool;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseRequestsTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.utils.RequestsUtils;
import lombok.Data;


/**
 * http get 请求工具
 */
@Data
public class RequestsGetTool extends BaseTool<RequestsGetTool.Request, String> {

    private int timeOut = 1000 * 10;

    private String name = "requests_get";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output will be the text response of the GET request.";

    @Override
    protected String _run(Object input) {

        Request request = JSONUtil.toBean(input.toString(), Request.class);

        return HttpUtil.get(request.getUrl(), this.timeOut);
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("a  url")
        private String url;

    }
}
