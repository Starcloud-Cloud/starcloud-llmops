package com.starcloud.ops.llm.langchain.core.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseRequestsTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

@Data
public class FailTool extends BaseTool<Object, String> implements BaseRequestsTool {

    private String name = "{fail_tool}";

    private String description = "Calling the tool failed and returned nothing.";

    public FailTool(String name) {
        this.name = name;
    }

    @Override
    protected String _run(Object input) {
        return this.name + " call failed with no return.";
    }


    @Override
    public JsonNode getInputSchemas() {
        return null;
    }
}
