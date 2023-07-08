package com.starcloud.ops.llm.langchain.core.tools;

import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class LoadTools {


    private final List<BaseTool> BASE_TOOLS = Arrays.asList(new RequestsGetTool());

    private final List<BaseTool> LLM_TOOLS = Arrays.asList(new RequestsGetTool());

    private final List<BaseTool> EXTRA_LLM_TOOLS = Arrays.asList(new RequestsGetTool());

    private final List<BaseTool> EXTRA_OPTIONAL_TOOLS = Arrays.asList(new RequestsGetTool());

    public static List<BaseTool> loadSystemTools() {

        return null;
    }

}
