package com.starcloud.ops.business.app.domain.entity.skill;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.skill.accredit.BaseAccredit;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.business.app.service.chat.momory.MessageContentDocMemory;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 技能实体
 */
@Data
public abstract class BaseSkillEntity {

    private Boolean enabled;

    private String name;

    private String desc;

    private String icon;

    private String tips;

    private SkillTypeEnum type;

    private BaseAccredit accredit;

    /**
     * 技能执行结果文档话和历史记录实现
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private MessageContentDocMemory messageContentDocMemory;


    /**
     * 是否把执行的结果增加到文档上下文
     */
    private Boolean addHistory = true;

    /**
     * 是否把执行的结果保存到文档上下文
     */
    private Boolean saveHistory = false;


    /**
     * 批量设置上下文保存策略
     *
     * @param add
     * @param save
     */
    public void setHistoryStrategy(Boolean add, Boolean save) {

        this.setAddHistory(add);
        this.setSaveHistory(save);
    }

    /**
     * 获取技能显示的名称
     *
     * @return
     */
    public String getUserName() {
        return this.getName();
    }

    /**
     * 获取技能显示描述
     *
     * @return
     */
    public String getUserDesc() {
        return this.getDesc();
    }

    /**
     * 获取技标签
     *
     * @return
     */
    public List<String> getTags() {
        return new ArrayList<>();
    }


    /**
     * 获取技能图标
     *
     * @return
     */
    public String getIcon() {
        return "default";
    }


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
    protected FunTool createSkillFunTool(String name, String description, Class<?> inputCls, Function<Object, ToolResponse> function) {

        return new FunTool(name, description, inputCls, function);
    }


    protected FunTool createSkillFunTool(String name, String description, JsonNode jsonSchema, Function<Object, ToolResponse> function) {

        return new FunTool(name, description, jsonSchema, function);
    }
}
