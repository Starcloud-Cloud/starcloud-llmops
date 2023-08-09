package com.starcloud.ops.business.app.domain.llm;


import cn.hutool.core.util.TypeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class OpenAIToolFactory {


    public static FunTool createAppTool(String appUid) {

        //  FunTool funTool = new FunTool(name, description, schema, function);

        return createAppTool(AppFactory.factory(appUid));
    }

    public static FunTool createAppTool(AppEntity appEntity) {

        Map<String, Object> variablesDes = appEntity.getWorkflowConfig().getFirstStep().getContextVariablesValues("");

        log.info("variablesDes: {}", variablesDes);
        // FunTool funTool = new FunTool(appEntity.getName(), appEntity.getDescription(), schema, function);

        //转换为 schema

        createFunTool(appEntity.getName(), appEntity.getDescription(), null, (params) -> {

            log.info("123");
            return "";
        });

        return null;
    }


    @Deprecated
    public static FunTool createHandlerTool(BaseHandler handler, Function<Object, String> function) {

        Type query = TypeUtil.getTypeArgument(handler.getClass());
        Class<?> cc = (Class<?>) query;
        return createFunTool(handler.getName(), handler.getDescription(), cc, function);

    }


    @Deprecated
    private static FunTool createFunTool(String name, String description, Class<?> inputCls, Function<Object, String> function) {

        FunTool funTool = new FunTool(name, description, inputCls, function);

        return funTool;
    }


}
