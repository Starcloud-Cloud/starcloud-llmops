package com.starcloud.ops.business.app.domain.entity.skill;

import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.business.app.domain.handler.common.HandlerResponse;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 技能实体
 */
@Slf4j
@Data
public class ApiSkill extends BaseSkillEntity {

    private SkillTypeEnum type = SkillTypeEnum.API;

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


    /**
     * 获取当前聊天配置的 其他名称和描述
     *
     * @return
     */
    @Override
    public String getName() {

        return "api name";
    }

    @Override
    public String getDesc() {

        return "api desc";
    }

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
    public FunTool createFunTool(HandlerContext handlerContext) {

        Function<Object, ToolResponse> function = (input) -> {

            log.info("FunTool ApiSkill: {} {}", this.getName(), input);

            SkillCustomConfig skillCustomConfig = this.getSkillSettingInfo(handlerContext.getAppUid(), this.getName());


            return ToolResponse.buildObservation(this._execute(input));
        };

        return createSkillFunTool(this.getName(), this.getDesc(), this.getInputSchemas(), function);
    }


    protected String _execute(Object req) {

        this.getAccredit();
        log.info("_execute: {}", this.getQueryParams());

        //@todo  根据 不同位子的参数，在 req 中查找具体到值，只需要在第一层找到即可
        //@todo 最后拼装 http 请求的参数，获取最后结果，结构在 根据配置的 responseBody schemas 做个校验，并返回最后的内容
        return null;
    }


    /**
     * 获取在App 技能上设置的 每个API的独立交互配置信息
     *
     * @return
     * @todo 读配置表
     */
    protected SkillCustomConfig getSkillSettingInfo(String appUid, String apiName) {

        //当前应用下配置的 其他应用的技能配置

        return new SkillCustomConfig();
    }
}
