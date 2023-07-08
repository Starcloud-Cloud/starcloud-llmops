package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseStringMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;

import java.util.List;

public class SystemMessagePromptTemplate extends BaseStringMessagePromptTemplate {


    @Override
    public BaseMessage format(List<BaseVariable> variables) {

        String text = this.getPromptTemplate().format(variables);
        return new SystemMessage(text);
    }
}
