package com.starcloud.ops.business.app.domain.entity.skill;

public enum SkillTypeEnum {

    API("api"),

    WORKFLOW("workflow");

    private String desc;

    SkillTypeEnum(String desc) {
        this.desc = desc;
    }
}
