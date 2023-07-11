package com.starcloud.ops.llm.langchain.core.tools.base;

import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForToolRun;
import lombok.Data;

@Data
public abstract class BaseTool {

    private String name;

    private String description;

    private Boolean verbose;

    private BaseCallbackManager callbackManager = new CallbackManager();

    protected abstract String _run(String input);

    public String run(String input, Boolean verbose) {

        CallbackManagerForToolRun toolRun = this.callbackManager.onToolStart(this.name, this.description, input, verbose);

        String result = null;

        try {

            result = this._run(input);

            toolRun.onToolEnd(this.getClass().getSimpleName(), result);

        } catch (Exception e) {

            toolRun.onToolError(e.getMessage(), e);

            throw e;
        }

        toolRun.onToolEnd(this.name, this.description, input, verbose);

        return result;
    }

}
