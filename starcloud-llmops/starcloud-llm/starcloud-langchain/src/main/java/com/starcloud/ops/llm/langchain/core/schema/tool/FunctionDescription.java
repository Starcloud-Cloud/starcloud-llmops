package com.starcloud.ops.llm.langchain.core.schema.tool;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class FunctionDescription<Q> {

    String name;

    String description;

    Class parameters;

    public FunctionDescription(String name, String description, Class<Q> qcls) {
        this.name = name;
        this.description = description;
        this.parameters = qcls;
    }

    public FunctionDescription(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public static <Q, R> FunctionDescription<Q> convert(BaseTool<Q, R> baseTool) {

        Type query = TypeUtil.getTypeArgument(baseTool.getClass());

        Class cc =  (Class<Q>) query;

        return new FunctionDescription<Q>(baseTool.getName(), baseTool.getDescription(), cc);
    }
}
