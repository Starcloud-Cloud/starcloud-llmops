package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.CreativeSchemeConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.VariableSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.ParagraphSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.convert.xhs.scheme.CreativeSchemeStepConvert;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ParagraphActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterTitleModeEnum;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.step.BaseSchemeStepEntity;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@SuppressWarnings("all")
public class CreativeUtils {

    /**
     * 获取海报方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 海报方案步骤
     */
    public static PosterSchemeStepDTO getPosterSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (PosterSchemeStepDTO) schemeStepList.stream()
                .filter(item -> PosterActionHandler.class.getSimpleName().equals(item.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取段落方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 段落方案步骤
     */
    public static ParagraphSchemeStepDTO getParagraphSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (ParagraphSchemeStepDTO) schemeStepList.stream()
                .filter(item -> ParagraphActionHandler.class.getSimpleName().equals(item.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取段落方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 段落方案步骤
     */
    public static VariableSchemeStepDTO getVariableSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (VariableSchemeStepDTO) schemeStepList.stream()
                .filter(item -> VariableActionHandler.class.getSimpleName().equals(item.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 将变量合并到方案全局变量步骤中
     *
     * @param schemeStepList   方案步骤列表
     * @param planVariableList 计划变量列表
     */
    public static List<BaseSchemeStepDTO> mergeSchemeStepVariable(List<BaseSchemeStepDTO> schemeStepList, List<VariableItemRespVO> planVariableList) {
        List<BaseSchemeStepDTO> list = new ArrayList<>();
        for (BaseSchemeStepDTO schemeStep : schemeStepList) {
            // 如果不是全局变量步骤，则直接添加返回步骤中
            if (!VariableActionHandler.class.getSimpleName().equals(schemeStep.getCode())) {
                list.add(schemeStep);
                continue;
            }
            // 如果是全局变量步骤，则合并变量
            List<VariableItemRespVO> variableItemList = new ArrayList<>();
            // 将计划变量转换成map，方便后续处理
            Map<String, VariableItemRespVO> planVariableMap = planVariableList.stream().collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));

            VariableSchemeStepDTO emptySchemeStep = (VariableSchemeStepDTO) schemeStep;
            // 获取全局变量的变量列表
            List<VariableItemRespVO> variables = emptySchemeStep.getVariableList();
            // 遍历全局变量的变量列表，进行合并
            for (VariableItemRespVO item : variables) {
                if (planVariableMap.containsKey(item.getField())) {
                    VariableItemRespVO planVariableItem = planVariableMap.get(item.getField());
                    if (Objects.nonNull(planVariableItem) && Objects.nonNull(planVariableItem.getValue())) {
                        item.setValue(planVariableItem.getValue());
                    }
                }
                variableItemList.add(item);
            }

            emptySchemeStep.setVariableList(variableItemList);
            list.add(emptySchemeStep);
        }

        return list;
    }


    /**
     * 处理执行的应用，参数填充等
     *
     * @param configuration 方案配置
     * @param app           应用信息
     * @return 处理后的应用信息
     */
    public static AppMarketRespVO handlerExecuteApp(CreativeSchemeConfigurationDTO configuration, AppMarketRespVO app) {

        // 复制一份，避免修改原数据
        AppMarketRespVO appMarket = SerializationUtils.clone(app);

        // 获取应用的工作流配置
        WorkflowConfigRespVO workflowConfig = appMarket.getWorkflowConfig();

        // 获取工作流配置的步骤列表
        List<WorkflowStepWrapperRespVO> stepWrapperList = CollectionUtil.emptyIfNull(workflowConfig.getSteps());

        // 获取方案步骤，并且转换成map，方便后续处理
        Map<String, BaseSchemeStepEntity> schemeStepEntityMap = CollectionUtil.emptyIfNull(configuration.getSteps()).stream()
                .collect(Collectors.toMap(BaseSchemeStepDTO::getName, CreativeSchemeStepConvert.INSTANCE::convert));

        // 遍历工作流配置的步骤列表，根据方案步骤进行填充
        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
            Optional<BaseSchemeStepEntity> stepEntityOptional = Optional.ofNullable(schemeStepEntityMap.get(stepWrapper.getName()));
            if (!stepEntityOptional.isPresent()) {
                continue;
            }
            BaseSchemeStepEntity schemeStepEntity = stepEntityOptional.get();
            schemeStepEntity.transformAppStep(stepWrapper);
        }
        workflowConfig.setSteps(stepWrapperList);
        appMarket.setWorkflowConfig(workflowConfig);
        return appMarket;
    }

    public static AppMarketRespVO handlerExecuteApp(List<BaseSchemeStepDTO> schemeStepList,
                                                    PosterStyleDTO posterStyle,
                                                    AppMarketRespVO app,
                                                    List<String> useImageList) {

        // 复制一份，避免修改原数据
        AppMarketRespVO appMarket = SerializationUtils.clone(app);

        WorkflowConfigRespVO workflowConfig = appMarket.getWorkflowConfig();
        List<WorkflowStepWrapperRespVO> stepWrapperList = CollectionUtil.emptyIfNull(workflowConfig.getSteps());

        // 获取方案步骤，并且转换成map，方便后续处理
        Map<String, BaseSchemeStepEntity> schemeStepEntityMap = schemeStepList.stream()
                .collect(Collectors.toMap(BaseSchemeStepDTO::getName, CreativeSchemeStepConvert.INSTANCE::convert));

        // 段落步骤。
        ParagraphSchemeStepDTO paragraphSchemeStep = getParagraphSchemeStep(schemeStepList);

        for (WorkflowStepWrapperRespVO stepWrapper : stepWrapperList) {
            if (PosterActionHandler.class.getSimpleName().equals(stepWrapper.getFlowStep().getHandler())) {
                PosterStyleDTO style;
                if (Objects.isNull(paragraphSchemeStep)) {
                    style = CreativeImageUtils.handlerPosterStyleExecute(posterStyle, useImageList);
                } else {
                    Integer paragraphCount = paragraphSchemeStep.getParagraphCount();
                    style = CreativeImageUtils.handlerPosterStyleExecute(posterStyle, useImageList, paragraphCount);
                }
                stepWrapper.putVariable(Collections.singletonMap(CreativeConstants.POSTER_STYLE, JSONUtil.toJsonStr(style)));
            } else {
                Optional<BaseSchemeStepEntity> stepEntityOptional = Optional.ofNullable(schemeStepEntityMap.get(stepWrapper.getName()));
                if (!stepEntityOptional.isPresent()) {
                    continue;
                }
                BaseSchemeStepEntity schemeStepEntity = stepEntityOptional.get();
                schemeStepEntity.transformAppStep(stepWrapper);
            }
        }
        workflowConfig.setSteps(stepWrapperList);
        appMarket.setWorkflowConfig(workflowConfig);
        return appMarket;
    }

    /**
     * 处理海报风格
     *
     * @param style 海报风格
     * @return 处理后的海报风格
     */
    public static PosterStyleDTO handlerPosterStyle(PosterStyleDTO style) {

        // 复制一份，避免修改原数据
        PosterStyleDTO posterStyle = SerializationUtils.clone(style);

        // 获取海报模板信息
        List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());

        // 遍历海报模板列表，进行数据填充
        List<PosterTemplateDTO> templateList = new ArrayList<>();
        for (int i = 0; i < posterTemplateList.size(); i++) {
            PosterTemplateDTO posterTemplate = posterTemplateList.get(i);
            PosterTemplateDTO template = SerializationUtils.clone(posterTemplate);
            List<PosterVariableDTO> variableList = template.getVariableList();
            // 获取图片数量
            Integer imageNumber = (int) variableList.stream().filter(item -> CreativeConstants.IMAGE.equals(item.getType())).count();
            String mode = StringUtils.isBlank(template.getMode()) ? PosterModeEnum.RANDOM.name() : template.getMode();
            String titleGenerateMode = StringUtils.isBlank(template.getTitleGenerateMode()) ? PosterTitleModeEnum.DEFAULT.name() : template.getTitleGenerateMode();
            // 更新模板信息
            template.setIndex(i + 1);
            template.setIsMain(i == 0);
            template.setImageNumber(imageNumber);
            template.setMode(mode);
            template.setTitleGenerateMode(titleGenerateMode);
            // 添加到列表
            templateList.add(template);
        }
        Integer imageCount = templateList.stream().mapToInt(PosterTemplateDTO::getImageNumber).sum();
        posterStyle.setImageCount(imageCount);
        posterStyle.setTemplateList(templateList);
        return posterStyle;
    }

    public static Map<Integer, List<String>> splitImageListToMap(List<String> imageList, int maxSize) {
        Map<Integer, List<String>> resultMap = new HashMap<>();

        int index = 0;
        int imageListSize = imageList.size();

        while (index < imageListSize) {
            int endIndex = Math.min(index + maxSize, imageListSize);
            List<String> sublist = new ArrayList<>(imageList.subList(index, endIndex));

            resultMap.put(resultMap.size() + 1, sublist);

            index += maxSize;
        }

        return resultMap;
    }
}



