package com.starcloud.ops.llm.langchain.core.chain.base;


import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMModel;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.util.List;


/**
 * @author df007df
 */
@Data
public abstract class BaseChain<P, R> {

    private Boolean verbose = false;

    private BaseLLMModel<P, R> llm;

    private BaseMemory memory;

    protected abstract BaseLLMResult<R> apply(List<BaseVariable> baseVariables);

}
