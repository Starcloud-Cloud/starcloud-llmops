package com.starcloud.ops.llm.langchain.core.tools.base;

import com.starcloud.ops.llm.langchain.core.schema.callbacks.LLMCallbackManager;
import lombok.Data;

@Data
public abstract class BaseTool {

    private String name;

    private String description;

    private Boolean verbose;

    private LLMCallbackManager callbackManager = new LLMCallbackManager();

    protected abstract String _run(String input);

    public String run(String input, Boolean verbose) {

        this.callbackManager.onToolStart(this.name, this.description, input, verbose);

        String result = null;
        try {
            result = this._run(input);
        } catch (Exception e) {
            this.callbackManager.onToolError(e.getMessage(), e);
            throw e;
        }

        this.callbackManager.onToolEnd(this.name, this.description, input, verbose);

        return result;
    }

}
