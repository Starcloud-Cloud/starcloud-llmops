package com.starcloud.ops.llm.langchain.core.tools;

import com.starcloud.ops.llm.langchain.core.tools.base.BaseRequestsTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.utils.RequestsUtils;
import lombok.Data;


/**
 * http get 请求工具
 */
@Data
public class RequestsGetTool extends BaseTool implements BaseRequestsTool {

    private String name = "requests_get";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output will be the text response of the GET request.";

    @Override
    protected String _run(String input) {

        return RequestsUtils.get("url");
    }
}
