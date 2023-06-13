package com.starcloud.ops.business.app.domain.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import com.starcloud.ops.business.app.api.app.dto.*;
import com.starcloud.ops.business.app.domain.entity.*;
import java.util.stream.Collectors;

/**
 * 获取步骤处理器工厂类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public class AppFactory {


    public static AppEntity factory(String appId) {

        return new AppEntity();
    }


    public static AppEntity factory(String appId, AppDTO template) {

        AppEntity app = transform(template);
        Assert.notNull(app, "app fire is fail, app[{0}] not found", appId);

        return app;
    }


    public static AppEntity factory(String appId, AppDTO template, String requestId) {
        return transform(template);
    }


    /**
     * 转换模版DTO为AppEntity
     *
     * @param template 模版DTO
     * @return AppEntity
     */
    public static AppEntity transform(AppDTO template) {
        AppEntity appEntity = new AppEntity();
        appEntity.setUid(template.getUid());
        appEntity.setName(template.getName());
        appEntity.setVersion(template.getVersion());
        appEntity.setType(template.getType());
        appEntity.setLogotype(template.getLogotype());
        appEntity.setSourceType(template.getSourceType());
        appEntity.setTags(template.getTags());
        appEntity.setCategories(template.getCategories());
        appEntity.setScenes(template.getScenes());
        appEntity.setConfig(transformConfig(template.getConfig()));
        return appEntity;
    }

    /**
     * 转换 TemplateConfigDTO 为 AppConfigEntity
     *
     * @param config 模版配置DTO
     * @return AppStepEntity
     */
    public static AppConfigEntity transformConfig(AppConfigDTO config) {
        AppConfigEntity appConfigEntity = new AppConfigEntity();
        appConfigEntity.setName(config.getName());
        appConfigEntity.setVersion(config.getVersion());
        appConfigEntity.setType(config.getType());
        appConfigEntity.setLogotype(config.getLogotype());
        appConfigEntity.setSourceType(config.getSourceType());
        appConfigEntity.setTags(config.getTags());
        appConfigEntity.setCategories(config.getCategories());
        appConfigEntity.setScenes(config.getScenes());
        appConfigEntity.setSteps(CollectionUtil.emptyIfNull(config.getSteps()).stream().map(AppFactory::transformStepWrapper).collect(Collectors.toList()));
        appConfigEntity.setVariables(CollectionUtil.emptyIfNull(config.getVariables()).stream().map(AppFactory::transformVariable).collect(Collectors.toList()));
        return appConfigEntity;
    }

    /**
     * 转换 StepWrapperDTO 为 AppStepWrapper
     *
     * @param step 步骤包装DTO
     * @return AppStepWrapper
     */
    public static AppStepWrapper transformStepWrapper(StepWrapperDTO step) {
        AppStepWrapper appStepWrapper = new AppStepWrapper();
        appStepWrapper.setName(step.getName());
        appStepWrapper.setField(step.getField());
        appStepWrapper.setStep(transformStep(step.getStep()));
        appStepWrapper.setVariables(CollectionUtil.emptyIfNull(step.getVariables()).stream().map(AppFactory::transformVariable).collect(Collectors.toList()));
        return appStepWrapper;
    }

    /**
     * 转换 StepDTO 为 AppStepEntity
     *
     * @param step 步骤DTO
     * @return AppStepEntity
     */
    public static AppStepEntity transformStep(StepDTO step) {
        AppStepEntity appStepEntity = new AppStepEntity();
        appStepEntity.setName(step.getName());
        appStepEntity.setTags(step.getTags());
        appStepEntity.setIsAuto(step.getIsAuto());
        appStepEntity.setScenes(step.getScenes());
        appStepEntity.setType(step.getType());
        appStepEntity.setVersion(step.getVersion());
        appStepEntity.setVariables(CollectionUtil.emptyIfNull(step.getVariables()).stream().map(AppFactory::transformVariable).collect(Collectors.toList()));
        appStepEntity.setResponse(transformResponse(step.getResponse()));
        return appStepEntity;
    }

    /**
     * 转换 StepResponseDTO 为 AppStepResponse
     *
     * @param response 响应DTO
     * @return AppStepResponse
     */
    public static AppStepResponse transformResponse(StepResponse response) {
        AppStepResponse appStepResponse = new AppStepResponse();
        appStepResponse.setSuccess(response.getSuccess());
        appStepResponse.setErrorCode(response.getErrorCode());
        appStepResponse.setMessage(response.getMessage());
        appStepResponse.setType(response.getType());
        appStepResponse.setStyle(response.getStyle());
        appStepResponse.setIsShow(response.getIsShow());
        appStepResponse.setAnswer(String.valueOf(response.getData()));
        return appStepResponse;
    }

    /**
     * 转换变量
     *
     * @param variable 变量
     * @return AppVariableEntity
     */
    public static AppVariableEntity transformVariable(VariableDTO variable) {
        AppVariableEntity appVariableEntity = new AppVariableEntity();
        appVariableEntity.setLabel(variable.getLabel());
        appVariableEntity.setField(variable.getField());
        appVariableEntity.setDefaultValue(variable.getDefaultValue());
        appVariableEntity.setValue(variable.getValue());
        appVariableEntity.setType(variable.getType());
        appVariableEntity.setOrder(variable.getOrder());
        appVariableEntity.setIsShow(variable.getIsShow());
        appVariableEntity.setIsPoint(variable.getIsPoint());
        appVariableEntity.setStyle(variable.getStyle());
        appVariableEntity.setGroup(variable.getGroup());
        appVariableEntity.setOptions(variable.getOptions());
        return appVariableEntity;
    }
}
