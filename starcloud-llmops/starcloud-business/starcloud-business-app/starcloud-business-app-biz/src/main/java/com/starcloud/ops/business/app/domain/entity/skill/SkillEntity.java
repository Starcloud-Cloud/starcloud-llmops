package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.skill.accredit.BaseAccredit;
import lombok.Data;

/**
 * 技能实体
 */
@Data
public abstract class SkillEntity {

    private String name;

    private String desc;

    private SkillTypeEnum type;

    //private String handler;

    private BaseAccredit accredit;

    public String getCode() {
        return this.getClass().getSimpleName();
    }

    @Deprecated
    public abstract Class<?> getInputCls();

    public abstract JsonNode getInputSchemas();

    /**
     * 执行应用
     */
    protected abstract Object _execute(Object req);


    public Object execute(Object req) {

        Object result = this._execute(req);

        return result;
    }

}
