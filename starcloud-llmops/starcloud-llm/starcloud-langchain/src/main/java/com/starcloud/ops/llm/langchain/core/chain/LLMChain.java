package com.starcloud.ops.llm.langchain.core.chain;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.starcloud.ops.llm.langchain.core.chain.base.BaseChain;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BasePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.BaseCallbackHandler;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class LLMChain<R> extends BaseChain<R> {

    private static final Logger logger = LoggerFactory.getLogger(LLMChain.class);

    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(LLMChain.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }


    private BasePromptTemplate promptTemplate;

    public LLMChain(BaseLanguageModel<R> llm, BasePromptTemplate promptTemplate) {
        this.setLlm(llm);
        this.setPromptTemplate(promptTemplate);
    }

    @Override
    protected BaseLLMResult<R> apply(List<BaseVariable> baseVariables) {

        baseVariables = this.prepInputs(baseVariables);

        PromptValue promptValue = this.promptTemplate.formatPrompt(baseVariables);

        this.getLlm().setVerbose(this.getVerbose());
        BaseLLMResult<R> result = this.getLlm().generatePrompt(Arrays.asList(promptValue));
        result = this.prepOutputs(baseVariables, result);

        return result;
    }

    protected List<BaseVariable> prepInputs(List<BaseVariable> baseVariables) {

        if (this.getMemory() != null) {
            List<BaseVariable> variables = this.getMemory().loadMemoryVariables();
            List<BaseVariable> variableList = Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().map(BaseVariable::copy).collect(Collectors.toList());
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


    public BaseLLMResult<R> run(List<BaseVariable> baseVariables) {

        return this.apply(baseVariables);
    }

    public BaseLLMResult<R> run(Map<String, Object> maps) {

        List<BaseVariable> variables = new ArrayList<>();
        maps.forEach((key, value) -> {
            variables.add(BaseVariable.builder()
                    .field(key)
                    .value(value)
                    .build());
        });

        return this.apply(variables);
    }


    @Deprecated
    public BaseLLMResult<R> predict(List<BaseVariable> baseVariables) {

        return this.apply(baseVariables);
    }


}
