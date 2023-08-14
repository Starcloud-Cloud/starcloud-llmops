package com.starcloud.ops.business.app.domain.entity.skill;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.chat.Interactive.InteractiveInfo;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.domain.handler.common.HandlerContext;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 技能实体
 */
@Slf4j
@Data
public class AppWorkflowSkill extends BaseSkillEntity {


    /**
     * 默认的 应用技能 描述 prompt
     */
    private String defaultPromptDesc = ". Note: Prompt or guide the user to fill in the parameters for this function if needed!";


    private String skillAppUid;

    @JsonIgnore
    @Override
    public Class<?> getInputCls() {
        return null;
    }


    /**
     * 流程现在默认入参 结构都是 Map
     *
     * @return
     */
    @JsonIgnore
    @Override
    public JsonNode getInputSchemas() {
        //根据 app配置参数，第一个step的入参 生成 schemas

        //我的应用
        AppEntity app = this.getMyApp(this.getSkillAppUid());
        Map<String, VariableItemEntity> variableItemEntityMap = app.getWorkflowConfig().getFirstStep().getContextVariableItems();


        Map<String, Map> properties = new HashMap<>();
        List<String> requireds = new ArrayList<>();

        Optional.ofNullable(variableItemEntityMap).map(Map::values).orElse(new ArrayList<>()).forEach(variableItemEntity -> {

            //参数定义
            Map<String, Object> params = new HashMap<>();
            Object val = variableItemEntity.getValue();

            //@todo 是否支持其他类型？
            if (val instanceof Number) {
                params.put("type", "number");
            } else {
                params.put("type", "string");
            }

            //处理枚举
            if (CollectionUtil.isNotEmpty(variableItemEntity.getOptions())) {

                List<Object> optionValues = Optional.ofNullable(variableItemEntity.getOptions()).orElse(new ArrayList<>()).stream().map(Option::getValue).collect(Collectors.toList());
                params.put("enum", optionValues);
            }

            params.put("description", variableItemEntity.getDescription());
            params.put("title", variableItemEntity.getLabel());

            properties.put(variableItemEntity.getField(), params);

            requireds.add(variableItemEntity.getField());
        });

        HashMap schemas = new HashMap() {
            {
                put("type", "object");
                put("properties", properties);
                //现在step参数默认都必填
                put("required", requireds);
            }
        };

        return OpenAIUtils.valueToTree(schemas);
    }


    /**
     * 获取在App 技能上设置的 每个应用的独立配置信息
     *
     * @return
     * @todo 读配置表
     */
    protected SkillCustomConfig getAppSkillSettingInfo(String appUid, String skillAppUid) {

        //当前应用下配置的 其他应用的技能配置

        return new SkillCustomConfig();
    }

    @Override
    public FunTool createFunTool(HandlerContext handlerContext) {

        try {
            //我的应用
            AppEntity app = this.getMyApp(this.getSkillAppUid());

            SkillCustomConfig skillCustomConfig = this.getAppSkillSettingInfo(handlerContext.getAppUid(), this.getSkillAppUid());

            //走配置，获取用户配置的内容
            String appName = Optional.ofNullable(skillCustomConfig).map(SkillCustomConfig::getName).orElse(app.getName());
            String appDesc = Optional.ofNullable(skillCustomConfig).map(SkillCustomConfig::getDescription).orElse(app.getDescription());
            appDesc = appDesc + this.defaultPromptDesc;

            //处理 step 中的变量
            //app.getWorkflowConfig().getFirstStep().getContextVariableItems();

            JsonNode schemas = null;

            Function<Object, String> function = (input) -> {

                log.info("FunTool AppWorkflowSkill: {} {}", this.getName(), input);

                handlerContext.setRequest(input);

                handlerContext.sendCallbackInteractiveStart(InteractiveInfo.buildText("AI应用执行中:(" + appName + ")"));

                Object result = this._execute(app, handlerContext);

                handlerContext.sendCallbackInteractiveEnd(InteractiveInfo.buildText("AI应用执行完成:(" + appName + ")"));

                return String.valueOf(result);
            };

            return new FunTool(appName, appDesc, schemas, function);

        } catch (Exception e) {

            log.error("AppWorkflowSkill createFunTool is fail: {}", e.getMessage(), e);
        }

        return null;
    }


    protected Object _execute(AppEntity app, HandlerContext handlerContext) {

        log.info("_execute: {}", this.getAccredit());

        handlerContext.getRequest();

        AppExecuteReqVO appExecuteReqVO = new AppExecuteReqVO();

        //@todo 构造参数
        appExecuteReqVO.setAppReqVO(null);

        JsonData jsonParams = new JsonData();

        //设置json对象的参数，因为现在 workflow 的入参也只有一层 即 一个 Map
        jsonParams.setData(null);

        //appExecuteReqVO.setJsonParams(jsonParams);

        return app.execute(appExecuteReqVO);
    }

    /**
     * 获取我的应用
     *
     * @return
     */
    private AppEntity getMyApp(String appUid) {
        AppEntity app = AppFactory.factoryApp(appUid);

        return app;
    }

}
