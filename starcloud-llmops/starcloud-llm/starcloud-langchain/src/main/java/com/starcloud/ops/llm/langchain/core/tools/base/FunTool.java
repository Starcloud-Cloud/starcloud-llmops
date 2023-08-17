package com.starcloud.ops.llm.langchain.core.tools.base;


import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;

import java.util.function.Function;

/**
 * 回调类型工具
 *
 * @author df007df
 */
@Data
public class FunTool extends BaseTool<Object, Object> {

    /**
     * 自定义的 schema
     */
    private JsonNode jsonSchema;

    private Function<Object, String> function;

    private Class<?> inputCls;

    private Class<?> outputCls;


    /**
     * 直接传入json schema
     *
     * @param name
     * @param description
     * @param jsonSchema
     * @param function
     */
    //无Java DTO作为参数的，都是动态的入参。如API，workflow，gptPlugins
    public FunTool(String name, String description, JsonNode jsonSchema, Function<Object, String> function) {
        this.setFunction(function);
        this.setName(name);
        this.setDescription(description);
        this.setJsonSchema(jsonSchema);
    }


    /**
     * 传入 Java class
     *
     * @param name
     * @param description
     * @param schemaCls
     * @param function
     */
    public FunTool(String name, String description, Class<?> schemaCls, Function<Object, String> function) {

        this.setFunction(function);
        this.setName(name);
        this.setDescription(description);
        this.setInputCls(schemaCls);
        this.setJsonSchema(OpenAIUtils.serializeJsonSchema(schemaCls));

    }

    @Override
    protected String _run(Object input) {

        //@todo 转换入参 为 jsonSchema
        Class<?> cc = this.getInputCls();
        input = JSONUtil.toBean(input.toString(), cc);

        return function.apply(input);
    }


    @Override
    public JsonNode getInputSchemas() {

        return this.jsonSchema;
    }


}
