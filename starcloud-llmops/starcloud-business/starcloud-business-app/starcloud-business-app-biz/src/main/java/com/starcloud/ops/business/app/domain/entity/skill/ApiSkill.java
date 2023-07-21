package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能实体
 */
@Slf4j
@Data
public class ApiSkill extends BaseSkillEntity {


    private String url;


    private String method;

    private Boolean needConfirmation;

    private List<Map<String, String>> headers;


    private Object queryParams;

    private Object requestBody;

    private Object responseBody;

    private Boolean validated;

    private String tips;

    private String mediaType;

    private Map<String, String> mediaFormatMaps;


    @Override
    public Class<?> getInputCls() {
        return null;
    }

    @Override
    public JsonNode getInputSchemas() {
        //根据 前端配置的参数 生成 schemas。前端上传的就已经是 schemas

        this.getQueryParams();
        this.getRequestBody();
        String name = this.getName();
        String description = this.getDesc();

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
                                                put("description", "Parameter defines the query you want to search.");
                                            }
                                        });
                            }
                        });
                //put("required", Arrays.asList("query"));
            }
        };

        return OpenAIUtils.valueToTree(schemas);

    }

    @Override
    protected Object _execute(Object req) {

        this.getAccredit();
        log.info("_execute: {}", this.getQueryParams());

        //@todo  根据 不同位子的参数，在 req 中查找具体到值，只需要在第一层找到即可
        //@todo 最后拼装 http 请求的参数，获取最后结果，结构在 根据配置的 responseBody schemas 做个校验，并返回最后的内容
        return null;
    }
}
