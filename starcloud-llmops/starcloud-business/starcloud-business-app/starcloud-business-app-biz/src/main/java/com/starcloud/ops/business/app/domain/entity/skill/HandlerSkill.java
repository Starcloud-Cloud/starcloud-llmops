package com.starcloud.ops.business.app.domain.entity.skill;


import cn.hutool.core.util.TypeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * handler 技能包装类
 */
@Slf4j
@Data
public class HandlerSkill extends BaseSkillEntity {

    private SkillTypeEnum type = SkillTypeEnum.HANDLER;


    private BaseHandler handler;

    public HandlerSkill(BaseHandler baseHandler) {
        this.handler = baseHandler;
    }


    @Override
    public String getName() {
        return this.handler.getName();
    }

    @Override
    public String getDesc() {
        return this.handler.getDescription();
    }

    @Override
    public String getUserName() {
        return this.handler.getUserName();
    }

    @Override
    public String getUserDesc() {
        return this.handler.getUserDescription();
    }


    @Override
    public JsonNode getInputSchemas() {

        return null;
    }


    @Override
    public Class<?> getInputCls() {
        return null;
    }


    @Override
    public FunTool createFunTool(HandlerContext handlerContext) {

        Type query = TypeUtil.getTypeArgument(this.getHandler().getClass());
        Class<?> cc = (Class<?>) query;

        Function<Object, String> function = (input) -> {

            log.info("FunTool HandlerSkill: {} {}", this.getHandler().getName(), input);

            //转换入参

            handlerContext.setRequest(input);

            HandlerResponse handlerResponse = this.handler.execute(handlerContext);

            //@todo 这里可增加 扣权益记录

            return handlerResponse.toJsonOutput();
        };

        return createFunTool(handler.getName(), handler.getDescription(), cc, function);
    }


}
