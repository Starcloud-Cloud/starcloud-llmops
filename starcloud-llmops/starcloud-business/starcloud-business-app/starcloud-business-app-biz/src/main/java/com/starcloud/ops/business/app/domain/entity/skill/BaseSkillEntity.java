package com.starcloud.ops.business.app.domain.entity.skill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.skill.accredit.BaseAccredit;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import lombok.Data;
import java.util.function.Function;

/**
 * 技能实体
 */
@Data
public abstract class BaseSkillEntity {

    private String name;

    private String desc;

    private SkillTypeEnum type;

    //private String handler;

    private BaseAccredit accredit;

    public String getCode() {
        return this.getClass().getSimpleName();
    }

    @JsonIgnore
    @Deprecated
    public abstract Class<?> getInputCls();

    @JsonIgnore
    public abstract JsonNode getInputSchemas();


    public abstract FunTool createFunTool(HandlerContext handlerContext);

    /**
     * 包装为 GPT FunTool
     *
     * @param name
     * @param description
     * @param inputCls
     * @param function
     * @return
     */
    protected FunTool createFunTool(String name, String description, Class<?> inputCls, Function<Object, String> function) {

        FunTool funTool = new FunTool(name, description, inputCls, function);

        return funTool;
    }
}
