package com.starcloud.ops.llm.langchain.core.memory;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseMemory {

    public abstract List<BaseVariable> loadMemoryVariables();

    public abstract void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result);
}
