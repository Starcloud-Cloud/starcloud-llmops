package com.starcloud.ops.llm.langchain.core.model.llm.qwen;

import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseOpenAI;

import java.util.List;

/**
 * 通义千问
 *
 * @author df007df
 */
public class Qwen extends BaseLLM<Void> {


    @Override
    protected BaseLLMResult<Void> _generate(List<String> texts, CallbackManagerForLLMRun callbackManager) {
        return null;
    }

    @Override
    public String getModelType() {
        return null;
    }
}
