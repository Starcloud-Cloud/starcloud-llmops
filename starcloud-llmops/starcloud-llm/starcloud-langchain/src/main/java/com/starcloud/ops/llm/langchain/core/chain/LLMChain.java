package com.starcloud.ops.llm.langchain.core.chain;

import com.starcloud.ops.llm.langchain.core.chain.base.BaseChain;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class LLMChain<R> extends BaseChain<R> {

    private static final Logger logger = LoggerFactory.getLogger(LLMChain.class);

    private BasePromptTemplate promptTemplate;

    public LLMChain(BaseLanguageModel<R, LLMCallbackManager> llm, BasePromptTemplate promptTemplate) {
        this.setLlm(llm);
        this.setPromptTemplate(promptTemplate);
    }

    @Override
    protected BaseLLMResult<R> _call(List<BaseVariable> baseVariables) {
        PromptValue promptValue = this.promptTemplate.formatPrompt(baseVariables);

        this.getLlm().setVerbose(this.getVerbose());
        return this.getLlm().generatePrompt(Arrays.asList(promptValue));
    }


    @Deprecated
    public BaseLLMResult<R> predict(List<BaseVariable> baseVariables) {

        return this._call(baseVariables);
    }


}
