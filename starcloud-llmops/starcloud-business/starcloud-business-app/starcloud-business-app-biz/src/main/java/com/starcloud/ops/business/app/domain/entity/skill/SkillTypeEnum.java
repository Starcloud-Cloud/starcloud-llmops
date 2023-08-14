package com.starcloud.ops.business.app.domain.entity.skill;

public enum SkillTypeEnum {

    API("api"),

    WORKFLOW("workflow"),

    GPT_PLUGIN("gpt_plugin");

    private String desc;

    SkillTypeEnum(String desc) {
        this.desc = desc;
    }
}
