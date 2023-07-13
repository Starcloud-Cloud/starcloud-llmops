package com.starcloud.ops.llm.langchain.core.chain.base;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForChainRun;
import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import lombok.Data;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author df007df
 */
@Data
public abstract class Chain<R> {

    private BaseLanguageModel<R> llm;

    private BaseMemory memory;

    private BaseCallbackManager callbackManager = new CallbackManager();

    public BaseCallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Chain.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }

    protected abstract BaseLLMResult<R> _call(List<BaseVariable> baseVariables, CallbackManagerForChainRun chainRun);


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

        baseVariables = this.prepInputs(baseVariables);

        CallbackManagerForChainRun chainRun =  this.getCallbackManager().onChainStart(this.getClass(), baseVariables, this.verbose);

        BaseLLMResult<R> baseLLMResult = null;

        try {

            baseLLMResult = this._call(baseVariables, chainRun);
        } catch (Exception e) {

            chainRun.onChainError(e.getMessage(), e);
        }

        chainRun.onChainEnd(this.getClass(), baseLLMResult);

        this.prepOutputs(baseVariables, baseLLMResult);

        return baseLLMResult;
    }


    public BaseLLMResult<R> call(Map<String, Object> maps) {

        List<BaseVariable> variables = new ArrayList<>();
        maps.forEach((key, value) -> {
            variables.add(BaseVariable.builder()
                    .field(key)
                    .value(value)
                    .build());
        });

        return this.call(variables);
    }


    public String run(List<BaseVariable> baseVariables) {

        return this.call(baseVariables).getText();
    }

    public String run(String text) {

        return this.call(Arrays.asList(BaseVariable.newString(text))).getText();
    }

    public void save() {
        return;
    }

}
