package com.starcloud.ops.llm.langchain.core.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseRequestsTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import lombok.Data;

@Data
public class InvalidTool extends BaseTool<Object> implements BaseRequestsTool {

    private String name = "{invalid_tool}";

    private String description = "Called when tool name is invalid.";

    public InvalidTool(String name) {
        this.name = name;
    }

    @Override
    protected ToolResponse _run(Object input) {
        return ToolResponse.buildObservation(this.name + " is not a valid tool, try another one.");
    }


    @Override
    public JsonNode getInputSchemas() {
        return null;
    }
}
