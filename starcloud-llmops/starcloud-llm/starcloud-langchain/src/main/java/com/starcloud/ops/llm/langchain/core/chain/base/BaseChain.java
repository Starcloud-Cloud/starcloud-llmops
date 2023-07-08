package com.starcloud.ops.llm.langchain.core.chain.base;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import lombok.Data;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author df007df
 */
@Data
public abstract class BaseChain<R> {

    private BaseLanguageModel<R, LLMCallbackManager> llm;

    private BaseMemory memory;

    private LLMCallbackManager callbackManager = new LLMCallbackManager();

    public LLMCallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(LLMCallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(BaseChain.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }

    protected abstract BaseLLMResult<R> _call(List<BaseVariable> baseVariables);


    public void _validateInputs(List<BaseVariable> baseVariables) {

    }

    public void _validateOutputs(BaseLLMResult<R> result) {

    }


    protected List<BaseVariable> prepInputs(List<BaseVariable> baseVariables) {

        if (this.getMemory() != null) {
            List<BaseVariable> variables = this.getMemory().loadMemoryVariables();
            List<BaseVariable> variableList = Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().map(BaseVariable::copy).collect(Collectors.toList());
            variableList.addAll(variables);
            baseVariables = variableList;
        }

        this._validateInputs(baseVariables);

        return baseVariables;
    }

    protected BaseLLMResult<R> prepOutputs(List<BaseVariable> baseVariables, BaseLLMResult<R> result) {

        this._validateOutputs(result);

        if (this.getMemory() != null) {
            this.getMemory().saveContext(baseVariables, result);
        }
        return result;
    }


    public BaseLLMResult<R> call(List<BaseVariable> baseVariables) {

        this.prepInputs(baseVariables);

        this.getCallbackManager().onChainStart(this.getClass(), baseVariables, this.verbose);

        BaseLLMResult<R> baseLLMResult = null;

        try {

            baseLLMResult = this._call(baseVariables);
        } catch (Exception e) {

            this.getCallbackManager().onChainError(e.getMessage(), e);
        }

        this.getCallbackManager().onChainEnd(this.getClass(), baseVariables, this.verbose);

        this.prepOutputs(baseVariables, baseLLMResult);

        return baseLLMResult;
    }


    public BaseLLMResult<R> run(List<BaseVariable> baseVariables) {

        return this._call(baseVariables);
    }


    public String run(Map<String, Object> maps) {

        List<BaseVariable> variables = new ArrayList<>();
        maps.forEach((key, value) -> {
            variables.add(BaseVariable.builder()
                    .field(key)
                    .value(value)
                    .build());
        });

        return this._call(variables).getText();
    }

    public void save() {
        return;
    }

}
