package com.starcloud.ops.llm.langchain.core.tools.base;

import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForToolRun;
import kotlin.jvm.Transient;
import lombok.Data;
import java.util.Map;

@Data
public abstract class BaseTool<Q, R> {

    private String name;

    private String description;

    private Boolean verbose;

    private Boolean returnDirect = false;

    @Transient
    private BaseCallbackManager callbackManager;

    protected abstract R _run(Object input);

    public R run(Object input, Boolean verbose, Map<String, Object> toolRunKwargs) {

        CallbackManagerForToolRun toolRun = this.callbackManager.onToolStart(this.name, this.description, input, verbose);

        R result = null;

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

    public Object getArgsSchema() {
        return null;
    }

    public Object getArgsSchemaRequired() {
        return null;
    }
}
