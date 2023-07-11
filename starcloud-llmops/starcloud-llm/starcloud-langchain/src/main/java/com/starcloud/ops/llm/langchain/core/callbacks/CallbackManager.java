package com.starcloud.ops.llm.langchain.core.callbacks;

import java.util.Arrays;
import java.util.List;

public class CallbackManager extends BaseCallbackManager {


    @Override
    public List<CallbackManagerForLLMRun> onLLMStart(Object... objects) {

        CallbackManagerForLLMRun llmRun = new CallbackManagerForLLMRun();

        llmRun.setHandlers(this.getHandlers());

        return Arrays.asList(llmRun);
    }

    @Override
    public List<CallbackManagerForLLMRun> onChatModelStart(Object... objects) {


        CallbackManagerForLLMRun llmRun = new CallbackManagerForLLMRun();

        llmRun.setHandlers(this.getHandlers());

        return Arrays.asList(llmRun);

    }

    @Override
    public CallbackManagerForChainRun onChainStart(Object... objects) {

        CallbackManagerForChainRun chainRun = new CallbackManagerForChainRun();

        chainRun.setHandlers(this.getHandlers());

        return chainRun;

    }

    @Override
    public CallbackManagerForToolRun onToolStart(Object... objects) {

        CallbackManagerForToolRun toolRun = new CallbackManagerForToolRun();

        toolRun.setHandlers(this.getHandlers());

        return toolRun;
    }

}
