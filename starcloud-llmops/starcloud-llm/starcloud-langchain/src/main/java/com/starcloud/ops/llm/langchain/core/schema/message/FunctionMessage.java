package com.starcloud.ops.llm.langchain.core.schema.message;

import java.util.Map;

public class FunctionMessage extends BaseMessage {

    public FunctionMessage(String content, Map<String, Object> additionalArgs) {
        super(content, additionalArgs);
    }

    public FunctionMessage(String content) {
        super(content);
    }

    @Override
    public String getType() {

        return "function";
    }
}
