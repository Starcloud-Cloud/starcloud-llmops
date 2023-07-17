package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.params.ParamsEntity;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能实体
 */
@Slf4j
@Data
public class AppWorkflowSkillEntity extends SkillEntity {


    private String appUid;


    @Override
    public Class<?> getInputCls() {
        return null;
    }

    @Override
    public JsonNode getInputSchemas() {
        //根据 app配置参数 生成 schemas

        this.getAppUid();

        //@todo 查询信息
        String name = "search workflow";
        String description = "workflow run command";

        HashMap schemas = new HashMap() {
            {
                put("type", "object");
                put("properties",
                        new HashMap() {
                            {
                                put("query",
                                        new HashMap() {
                                            {
                                                put("type", "string");
                                                put("description", "Parameter defines the query you want to run workflow.");
                                            }
                                        });
                            }
                        });
                put("required", Arrays.asList("query"));
            }
        };

        return OpenAIUtils.valueToTree(schemas);
    }

    @Override
    protected Object _execute(Object req) {

        log.info("_execute: {}", this.getAccredit());

        return null;
    }
}
