package com.starcloud.ops.business.app.domain.entity.skill;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

/**
 * 技能实体
 */
@Slf4j
@Data
public class AppWorkflowSkill extends BaseSkillEntity {


    private String appUid;

    @JsonIgnore
    @Override
    public Class<?> getInputCls() {
        return null;
    }

    @JsonIgnore
    @Override
    public JsonNode getInputSchemas() {
        //根据 app配置参数，第一个step的入参 生成 schemas

        try {
            AppEntity app = AppFactory.factoryApp(this.getAppUid());

        } catch (Exception e) {

        }

        //@todo 查询信息
        this.setName("search workflow");
        this.setDesc("workflow run command");

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
    public FunTool createFunTool(HandlerContext handlerContext) {

        Function<Object, String> function = (input) -> {

            log.info("FunTool AppWorkflowSkill: {} {}", this.getName(), input);

            handlerContext.setRequest(input);

            return this._execute(handlerContext);
        };

        return new FunTool(this.getName(), this.getDesc(), this.getInputSchemas(), function);
    }


    protected String _execute(HandlerContext handlerContext) {

        log.info("_execute: {}", this.getAccredit());

        handlerContext.getRequest();


        AppEntity<AppExecuteReqVO, Object> app = AppFactory.factoryApp(this.getAppUid());

        AppExecuteReqVO appExecuteReqVO = new AppExecuteReqVO();

        //@todo 构造参数
        appExecuteReqVO.setAppReqVO(null);

        JsonData jsonParams = new JsonData();

        //设置json对象的参数，因为现在 workflow 的入参也只有一层 即 一个 Map
        jsonParams.setData(null);

        //appExecuteReqVO.setJsonParams(jsonParams);

        Object result = app.execute(appExecuteReqVO);

        return null;
    }
}
