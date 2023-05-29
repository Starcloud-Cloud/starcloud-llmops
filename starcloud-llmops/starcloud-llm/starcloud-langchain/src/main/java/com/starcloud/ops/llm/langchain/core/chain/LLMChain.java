package com.starcloud.ops.llm.langchain.core.chain;

import com.starcloud.ops.llm.langchain.core.chain.base.BaseChain;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMModel;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BasePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class LLMChain<P, R> extends BaseChain<P, R> {

    private BasePromptTemplate promptTemplate;

    public LLMChain(BaseLLMModel<P, R> llm, BasePromptTemplate promptTemplate) {
        this.setLlm(llm);
        this.setPromptTemplate(promptTemplate);
    }

    @Override
    protected BaseLLMResult<R> apply(List<BaseVariable> baseVariables) {

        baseVariables = this.prepInputs(baseVariables);

        PromptValue promptValue = this.promptTemplate.formatPrompt(baseVariables);

        if (Boolean.TRUE.equals(this.getVerbose())) {
            log.info("formatPrompt: {}", promptValue);
        }
        BaseLLMResult<R> result = this.getLlm().generatePrompt(promptValue);
        result = this.prepOutputs(baseVariables, result);

        return result;
    }

    protected List<BaseVariable> prepInputs(List<BaseVariable> baseVariables) {

        if (this.getMemory() != null) {
            List<BaseVariable> variables = this.getMemory().loadMemoryVariables();
            List<BaseVariable> variableList = Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().map(BaseVariable::of).collect(Collectors.toList());
            variableList.addAll(variables);
            return variableList;
        }

        return baseVariables;
    }

    protected BaseLLMResult<R> prepOutputs(List<BaseVariable> baseVariables, BaseLLMResult<R> result) {

        if (this.getMemory() != null) {
            this.getMemory().saveContext(baseVariables, result);
        }
        return result;
    }


    public BaseLLMResult<R> run(String text) {

        BaseVariable variable = this.getPromptTemplate().getFirstVariable();
        return this.apply(Arrays.asList(BaseVariable.newString(variable.getField(), text)));
    }

    public BaseLLMResult<R> predict(List<BaseVariable> baseVariables) {

        return this.apply(baseVariables);
    }


}
