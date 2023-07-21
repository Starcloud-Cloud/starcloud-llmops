package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;

/**
 * 技能实体
 */
@Data
public class GptPluginSkill extends BaseSkillEntity {


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
    protected Object _execute(Object req) {
        return null;
    }
}
