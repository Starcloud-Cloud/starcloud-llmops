package com.starcloud.ops.llm.langchain.core.tools.utils;

import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

public class ConvertToOpenaiUtils {


    public static FunctionDescription convert(BaseTool baseTool) {

        return new FunctionDescription(baseTool.getName(), baseTool.getDescription(), null);
    }


    @Data
    public static class FunctionDescription {

        String name;

        String description;

        Object parameters;

        public FunctionDescription(String name, String description, Object parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }
    }


}

