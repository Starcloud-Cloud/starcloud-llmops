package com.starcloud.ops.llm.langchain.core.chain.base;


import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import lombok.Data;

import java.util.List;


/**
 * @author df007df
 */
@Data
public abstract class BaseChain<R> {

    private BaseLanguageModel<R> llm;

    private BaseMemory memory;

    protected abstract BaseLLMResult<R> apply(List<BaseVariable> baseVariables);

}
