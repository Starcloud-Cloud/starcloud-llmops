package com.starcloud.ops.business.app.controller.admin.xhs.upgrade;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.convert.market.AppMarketConvert;
import com.starcloud.ops.business.app.convert.xhs.batch.CreativePlanBatchConvert;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.convert.xhs.plan.CreativePlanConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.plan.CreativePlanDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.batch.CreativePlanBatchMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.plan.CreativePlanMapper;
import com.starcloud.ops.business.app.domain.entity.chat.ModelProviderEnum;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.CustomActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteParam;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.recommend.RecommendVariableFactory;
import com.starcloud.ops.business.app.recommend.RecommendVariableItemFactory;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import dm.jdbc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Slf4j
@Service
public class UpgradeDataService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private CreativePlanMapper creativePlanMapper;

    @Resource
    private CreativePlanBatchMapper creativePlanBatchMapper;

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Transactional(rollbackFor = Exception.class)
    public void upgradeDataApp() {
        LambdaQueryWrapper<AppDO> appQuery = Wrappers.lambdaQuery(AppDO.class);
        // appQuery.eq(AppDO::getId, 839);
        // appQuery.eq(AppDO::getType, AppTypeEnum.MEDIA_MATRIX.name());
        // appQuery.eq(AppDO::getUid, "90aee659ce524150904ff0c0ec00e7a1");
        List<AppDO> appList = appMapper.selectList(appQuery);

        for (AppDO app : appList) {
            AppRespVO response = AppConvert.INSTANCE.convertResponse(app);

            WorkflowConfigRespVO workflowConfig = response.getWorkflowConfig();
            if (workflowConfig == null) {
                continue;
            }
            // 处理配置
            handlerConfig(workflowConfig);

            // 更新配置
            app.setConfig(JsonUtils.toJsonString(workflowConfig));
            appMapper.updateById(app);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void upgradeDataAppMarket() {
        LambdaQueryWrapper<AppMarketDO> appQuery = Wrappers.lambdaQuery(AppMarketDO.class);
        // appQuery.eq(AppMarketDO::getId, 432);
        // appQuery.eq(AppMarketDO::getType, AppTypeEnum.MEDIA_MATRIX.name());
        appQuery.eq(AppMarketDO::getDeleted, Boolean.FALSE);
        List<AppMarketDO> appList = appMarketMapper.selectList(appQuery);

        for (AppMarketDO app : appList) {
            AppMarketRespVO response = AppMarketConvert.INSTANCE.convertResponse(app);

            WorkflowConfigRespVO workflowConfig = response.getWorkflowConfig();

            // 为空不处理
            if (workflowConfig == null) {
                continue;
            }

            handlerConfig(workflowConfig);

            app.setConfig(JsonUtils.toJsonString(workflowConfig));

            appMarketMapper.updateById(app);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void upgradeDataCreativePlan() {
        LambdaQueryWrapper<CreativePlanDO> queryWrapper = Wrappers.lambdaQuery(CreativePlanDO.class);
        // queryWrapper.eq(CreativePlanDO::getUid, "c21251a6de664498914d4f6548258a52");
        List<CreativePlanDO> creativePlanList = creativePlanMapper.selectList(queryWrapper);
        for (CreativePlanDO plan : creativePlanList) {
            CreativePlanRespVO response = CreativePlanConvert.INSTANCE.convertResponse(plan);

            CreativePlanConfigurationDTO configuration = response.getConfiguration();
            AppMarketRespVO appInformation = configuration.getAppInformation();

            handlerAppMarket(appInformation);
            configuration.setAppInformation(appInformation);

            plan.setConfiguration(JsonUtils.toJsonString(configuration));
            creativePlanMapper.updateById(plan);

        }

    }

    public void upgradeDataCreativePlanBatch() {
        for (int i = 0; i < 20; i++) {
            LambdaQueryWrapper<CreativePlanBatchDO> queryWrapper = Wrappers.lambdaQuery(CreativePlanBatchDO.class);
            // queryWrapper.eq(CreativePlanBatchDO::getUid, "7f77a92ff0474f868e5424a1d0483a1a");
            Page<CreativePlanBatchDO> page = new Page<>(i + 1, 100);
            Page<CreativePlanBatchDO> page1 = creativePlanBatchMapper.selectPage(page, queryWrapper);
            List<CreativePlanBatchDO> creativePlanBatchList = CollectionUtil.emptyIfNull(page1.getRecords());
            if (CollectionUtil.isEmpty(creativePlanBatchList)) {
                continue;
            }
            for (CreativePlanBatchDO batch : creativePlanBatchList) {

                CreativePlanBatchRespVO response = CreativePlanBatchConvert.INSTANCE.convert(batch);
                CreativePlanConfigurationDTO configuration = response.getConfiguration();
                AppMarketRespVO appInformation = configuration.getAppInformation();

                handlerAppMarket(appInformation);
                configuration.setAppInformation(appInformation);

                batch.setConfiguration(JsonUtils.toJsonString(configuration));
                //creativePlanBatchMapper.updateById(batch);
            }

            creativePlanBatchMapper.updateBatch(creativePlanBatchList);
            log.info("upPlanBatch i = {}, {}, \n\n\n\n\n\n", i, creativePlanBatchList.size());

        }

    }

    public void upgradeDataCreativeContent() {
        for (int i = 0; i < 40; i++) {
            LambdaQueryWrapper<CreativeContentDO> queryWrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
            Page<CreativeContentDO> page = new Page<>(i + 1, 100);

            // queryWrapper.eq(CreativeContentDO::getUid, "334d8322f2e046e19a49837b3de29634");
            Page<CreativeContentDO> page1 = creativeContentMapper.selectPage(page, queryWrapper);
            List<CreativeContentDO> creativePlanList = CollectionUtil.emptyIfNull(page1.getRecords());
            if (CollectionUtil.isEmpty(creativePlanList)) {
                continue;
            }
            for (CreativeContentDO content : creativePlanList) {
                CreativeContentRespVO response = CreativeContentConvert.INSTANCE.convert(content);

                CreativeContentExecuteParam executeParam = response.getExecuteParam();
                AppMarketRespVO appInformation = executeParam.getAppInformation();
                // 处理
                handlerAppMarket(appInformation);
                executeParam.setAppInformation(appInformation);

                // 更新
                content.setExecuteParam(JsonUtils.toJsonString(executeParam));
                //creativeContentMapper.updateById(content);
            }
            creativeContentMapper.updateBatch(creativePlanList);
            log.info("upgradeDataCreativeContent i = {}, {} \n\n\n\n\n\n", i, creativePlanList.size());
        }
    }

    private void handlerAppMarket(AppMarketRespVO app) {
        WorkflowConfigRespVO workflowConfig = app.getWorkflowConfig();
        if (workflowConfig == null) {
            return;
        }
        handlerConfig(workflowConfig);
    }

    private void handlerConfig(WorkflowConfigRespVO workflowConfig) {
        if (workflowConfig == null) {
            return;
        }
        List<WorkflowStepWrapperRespVO> stepList = CollectionUtil.emptyIfNull(workflowConfig.getSteps());

        for (WorkflowStepWrapperRespVO stepWrapper : stepList) {
            if (stepWrapper == null) {
                continue;
            }

            if (CustomActionHandler.class.getSimpleName().equals(stepWrapper.getFlowStep().getHandler())) {
                handlerCustomStep(stepWrapper);
            }
//            String handler = stepWrapper.getFlowStep().getHandler();
//            handlerStep(stepWrapper);
        }

        workflowConfig.setSteps(stepList);
    }

    private void handlerStep(WorkflowStepWrapperRespVO customHandler) {
        // 响应结果
        WorkflowStepRespVO flowStep = customHandler.getFlowStep();

        // 模型参数
        VariableRespVO modelVariableResponse = flowStep.getVariable();
        List<VariableItemRespVO> modelVariables = CollectionUtil.emptyIfNull(modelVariableResponse.getVariables());

        List<VariableItemRespVO> modelVariableList = new ArrayList<>();

        String handler = customHandler.getFlowStep().getHandler();
        if (MaterialActionHandler.class.getSimpleName().equals(handler)
                || VariableActionHandler.class.getSimpleName().equals(handler)
                || AssembleActionHandler.class.getSimpleName().equals(handler)
                || "XhsParseActionHandler".equals(handler)) {
            flowStep.setVariable(RecommendVariableFactory.defGlobalVariableVariable());
            customHandler.setFlowStep(flowStep);
            return;
        }

        // 模型参数 prompt 修改
        for (VariableItemRespVO variable : modelVariables) {
            if ("model".equalsIgnoreCase(variable.getField())) {
                variable.setOrder(1);
                variable.setOptions(AppUtils.llmModelTypeList());
                String model = String.valueOf(variable.getValue());
                if (ModelTypeEnum.GPT_3_5_TURBO.getName().equals(model) || ModelProviderEnum.GPT35.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT35.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT35.name());

                } else if (ModelTypeEnum.GPT_3_5_TURBO_16K.getName().equals(model) || ModelProviderEnum.GPT35.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT35.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT35.name());

                } else if (ModelTypeEnum.GPT_4.getName().equals(model) || ModelProviderEnum.GPT4.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT4.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT4.name());

                } else if (ModelTypeEnum.GPT_4_TURBO.getName().equals(model) || ModelProviderEnum.GPT4.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT4.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT4.name());

                } else if (ModelTypeEnum.GPT_4_32K.getName().equals(model) || ModelProviderEnum.GPT4.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT4.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT4.name());

                } else if (ModelTypeEnum.GPT_4_O.getName().equals(model) || ModelProviderEnum.GPT4.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT4.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT4.name());

                } else if (ModelTypeEnum.QWEN.getName().equals(model) || ModelProviderEnum.QWEN.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.QWEN.name());
                    variable.setDefaultValue(ModelProviderEnum.QWEN.name());

                } else if (ModelTypeEnum.QWEN_MAX.getName().equals(model) || ModelProviderEnum.QWEN_MAX.name().equals(model)) {
                    variable.setValue(ModelProviderEnum.QWEN_MAX.name());
                    variable.setDefaultValue(ModelProviderEnum.QWEN_MAX.name());

                } else if ("gpt-3.5-turbo-1106".equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT35.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT35.name());
                } else if ("gpt-4-1106-preview".equals(model)) {
                    variable.setValue(ModelProviderEnum.GPT4.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT4.name());
                } else {
                    variable.setValue(ModelProviderEnum.GPT35.name());
                    variable.setDefaultValue(ModelProviderEnum.GPT35.name());
                }
                if (CustomActionHandler.class.getSimpleName().equals(customHandler.getFlowStep().getHandler())) {
                    variable.setValue(ModelProviderEnum.QWEN.name());
                    variable.setDefaultValue(ModelProviderEnum.QWEN.name());
                }
            }
            if ("max_tokens".equalsIgnoreCase(variable.getField())) {
                variable.setOrder(2);
            }
            if ("temperature".equalsIgnoreCase(variable.getField())) {
                variable.setOrder(3);
            }
            if ("prompt".equalsIgnoreCase(variable.getField())) {
                variable.setOrder(4);
            }
            modelVariableList.add(variable);
        }

        List<VariableItemRespVO> collect = modelVariableList.stream()
                .filter(item -> "model".equalsIgnoreCase(item.getField()))
                .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(collect)) {
            VariableItemRespVO variableItemRespVO = RecommendVariableItemFactory.defModelVariable();
            variableItemRespVO.setOrder(1);
            modelVariableList.add(variableItemRespVO);
        }
        modelVariableList = modelVariableList.stream().sorted(Comparator.comparingInt(VariableItemRespVO::getOrder)).collect(Collectors.toList());
        modelVariableResponse.setVariables(modelVariableList);
        flowStep.setVariable(modelVariableResponse);

        customHandler.setFlowStep(flowStep);
    }

    private void handlerCustomStep(WorkflowStepWrapperRespVO customHandler) {
        if (!CustomActionHandler.class.getSimpleName().equals(customHandler.getFlowStep().getHandler())) {
            return;
        }
        // 增加变量
        VariableRespVO variableResponse = customHandler.getVariable();
        List<VariableItemRespVO> variables = CollectionUtil.emptyIfNull(variableResponse.getVariables());

        VariableItemRespVO model = variables.stream().filter(item -> CreativeConstants.GENERATE_MODE.equals(item.getField()))
                .findFirst()
                .orElse(RecommendVariableItemFactory.defMediaMatrixGenerateVariable());

        VariableItemRespVO requirement = variables.stream()
                .filter(item -> CreativeConstants.REQUIREMENT.equals(item.getField()))
                .findFirst()
                .orElse(RecommendVariableItemFactory.defMediaMatrixRequirement());

        String generateMode = Optional.ofNullable(model.getValue())
                .map(Object::toString).orElse(CreativeContentGenerateModelEnum.AI_CUSTOM.name());

        String requirementValue = Optional.ofNullable(requirement.getValue())
                .map(Object::toString).orElse(StringUtil.EMPTY);

        VariableItemRespVO customRequirement = RecommendVariableItemFactory.defCustomRequirement();
        customRequirement.setOrder(5);
        customRequirement.setIsShow(Boolean.TRUE);

        // 系统提示
        VariableItemRespVO parodyRequirement = RecommendVariableItemFactory.defParodyRequirement();
        parodyRequirement.setOrder(6);
        parodyRequirement.setIsShow(Boolean.TRUE);

        if (CreativeContentGenerateModelEnum.AI_CUSTOM.name().equals(generateMode)) {
            customRequirement.setValue(requirementValue);
        } else if (CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generateMode)) {
            parodyRequirement.setValue(requirementValue);
        }

        variables = variables.stream()
                .filter(item -> !CreativeConstants.REQUIREMENT.equals(item.getField()))
                .filter(item -> !CreativeConstants.GENERATE_MODE.equals(item.getField()))
                .filter(item -> !CreativeConstants.CUSTOM_REQUIREMENT.equals(item.getField()))
                .filter(item -> !CreativeConstants.PARODY_REQUIREMENT.equals(item.getField()))
                .collect(Collectors.toList());

        variables.add(model);
        variables.add(customRequirement);
        variables.add(parodyRequirement);

        variables = variables.stream()
                .filter(Objects::nonNull)
                .sorted(
                        Comparator.comparing(VariableItemRespVO::getOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                )
                .collect(Collectors.toList());

        variableResponse.setVariables(variables);
        customHandler.setVariable(variableResponse);
    }
}
