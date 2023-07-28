package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.skill.accredit.BaseAccredit;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    /**
     * 执行应用
     */
    protected abstract Object _execute(Object req);


    public Object execute(Object req) {

        Object result = this._execute(req);

        return result;
    }


    public Object execute(Object req, SseEmitter emitter) {

        Object result = this._execute(req);

        return result;
    }
}
