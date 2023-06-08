package com.starcloud.ops.llm.langchain.core.prompt.base.template;

public class SystemMessagePromptTemplate extends BaseMessagePromptTemplate {

    public SystemMessagePromptTemplate(BasePromptTemplate promptTemplate) {
        super(promptTemplate);
        this.setRole("system");
    }

    public static SystemMessagePromptTemplate fromTemplate(BasePromptTemplate promptTemplate) {
        return new SystemMessagePromptTemplate(promptTemplate);
    }
}
