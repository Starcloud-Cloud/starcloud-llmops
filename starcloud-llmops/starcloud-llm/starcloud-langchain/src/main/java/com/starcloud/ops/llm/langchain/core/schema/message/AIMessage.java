package com.starcloud.ops.llm.langchain.core.schema.message;

public class AIMessage extends BaseMessage {

    public AIMessage(String content) {
        super(content);
    }

    //@todo
    @Override
    public String getType() {

        return "assistant";
    }


}
