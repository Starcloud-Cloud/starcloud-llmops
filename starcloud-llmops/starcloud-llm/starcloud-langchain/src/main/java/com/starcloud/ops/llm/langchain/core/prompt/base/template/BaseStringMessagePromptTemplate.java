package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import cn.hutool.core.util.ReflectUtil;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@NoArgsConstructor
@Data
@Slf4j
public abstract class BaseStringMessagePromptTemplate extends BaseMessagePromptTemplate {

    private StringPromptTemplate promptTemplate;

    private Object additionalKwargs;

    public BaseStringMessagePromptTemplate(StringPromptTemplate promptTemplate) {
        super();
        this.promptTemplate = promptTemplate;
    }

    public abstract BaseMessage format(List<BaseVariable> variables);

    @Override
    public List<BaseMessage> formatMessages(List<BaseVariable> variables) {
        return Arrays.asList(this.format(variables));
    }

    public static BaseStringMessagePromptTemplate fromTemplate(String text) {

        PromptTemplate promptTemplate = PromptTemplate.fromTemplate(text);
        return fromTemplate(promptTemplate);
    }

    public static BaseMessagePromptTemplate fromTemplate(String text, String... params) {

        List<BaseVariable> variables = Arrays.stream(Optional.ofNullable(params).orElse(new String[]{})).map(BaseVariable::newString).collect(Collectors.toList());
        StringPromptTemplate promptTemplate = new PromptTemplate(text, variables);
        return fromTemplate(promptTemplate);
    }

    @Deprecated
    private static BaseStringMessagePromptTemplate fromTemplate(StringPromptTemplate promptTemplate) {

        try {
            String clazzName = new Object() {
                public String getClassName() {
                    String clazzName = this.getClass().getName();
                    return clazzName.substring(1, clazzName.lastIndexOf('$'));
                }
            }.getClassName();

            return ReflectUtil.newInstance((Class<BaseStringMessagePromptTemplate>) Class.forName(clazzName), promptTemplate);
        } catch (Exception e) {

            log.error("fromTemplate is fail: {}", e.getMessage(), e);
        }

        return null;
    }


}
