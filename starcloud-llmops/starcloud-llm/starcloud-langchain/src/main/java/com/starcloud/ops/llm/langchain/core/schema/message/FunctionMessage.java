package com.starcloud.ops.llm.langchain.core.schema.message;

import java.util.Map;

public class FunctionMessage extends BaseMessage {

    public FunctionMessage(String content, Map<String, Object> additionalArgs) {
        super(content, additionalArgs);
    }

    @Override
    public String getType() {

        return "function";
    }
}
