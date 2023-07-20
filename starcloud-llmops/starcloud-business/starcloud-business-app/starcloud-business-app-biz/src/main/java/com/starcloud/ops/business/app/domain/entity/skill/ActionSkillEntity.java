package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.handler.common.FlowStepHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * action 技能
 */
@Slf4j
@Data
public class ActionSkillEntity extends SkillEntity {

    private Class<? extends FlowStepHandler> actionCls;

    @Override
    public Class<?> getInputCls() {
        return null;
    }

    @Override
    public JsonNode getInputSchemas() {
        return null;
    }

    @Override
    protected Object _execute(Object req) {

        log.info("_execute: {}", this.getActionCls());

        return null;
    }
}
