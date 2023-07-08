package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.List;


public class MessagesPlaceholder extends BaseMessagePromptTemplate {

    private String variableName;

    public MessagesPlaceholder(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public List<BaseMessage> formatMessages(List<BaseVariable> variables) {
        return null;
    }
}
