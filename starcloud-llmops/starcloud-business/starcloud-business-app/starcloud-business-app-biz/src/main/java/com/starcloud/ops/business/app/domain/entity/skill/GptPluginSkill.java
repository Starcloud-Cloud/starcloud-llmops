package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

/**
 * 技能实体
 */
@Slf4j
@Data
public class GptPluginSkill extends BaseSkillEntity {

    private SkillTypeEnum type = SkillTypeEnum.GPT_PLUGIN;


    private String pluginUrl;


    private List<ApiSkill> apiSkillEntityList;


    @Override
    public Class<?> getInputCls() {
        return null;
    }

    @Override
    public JsonNode getInputSchemas() {
        return null;
    }


    @Override
    public FunTool createFunTool(HandlerContext handlerContext) {

        Function<Object, String> function = (input) -> {

            log.info("FunTool ApiSkill: {} {}", this.getName(), input);


            return "";
        };

        return new FunTool(this.getName(), this.getDesc(), this.getInputSchemas(), function);
    }


}
