package com.starcloud.ops.llm.langchain.core.tools.base;


import lombok.Data;

import java.util.function.Function;

/**
 * 回调类型工具
 */
@Data
public class FunTool extends BaseTool<String, String> {

    private final Function<String, String> function;

    public FunTool(String name, String description, Function<String, String> function) {
        this.function = function;
        this.setName(name);
        this.setDescription(description);
    }

    @Override
    protected String _run(Object input) {
        return function.apply(input.toString());
    }
}
