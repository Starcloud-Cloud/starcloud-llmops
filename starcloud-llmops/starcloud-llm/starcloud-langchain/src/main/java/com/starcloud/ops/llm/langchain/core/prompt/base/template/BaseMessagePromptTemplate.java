package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author df007df
 */
@Data
public class BaseMessagePromptTemplate {

    private String role;

    private BasePromptTemplate promptTemplate;

    public BaseMessagePromptTemplate(BasePromptTemplate promptTemplate) {
        this.setPromptTemplate(promptTemplate);
    }

    public PromptValue formatPrompt(List<BaseVariable> variables) {

        return promptTemplate.formatPrompt(variables);
    }

    public BaseChatMessage formatMessages(List<BaseVariable> variables) {

        PromptValue promptValue = this.formatPrompt(variables);

        return BaseChatMessage.builder()
                .role(this.getRole())
                .content(promptValue.toStr())
                .build();
    }
}
