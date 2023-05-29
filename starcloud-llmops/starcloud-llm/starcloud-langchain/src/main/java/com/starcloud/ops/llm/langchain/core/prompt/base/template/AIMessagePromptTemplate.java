package com.starcloud.ops.llm.langchain.core.prompt.base.template;

public class AIMessagePromptTemplate extends BaseMessagePromptTemplate {

    public AIMessagePromptTemplate(BasePromptTemplate promptTemplate) {
        super(promptTemplate);
        this.setRole("assistant");
    }
}
