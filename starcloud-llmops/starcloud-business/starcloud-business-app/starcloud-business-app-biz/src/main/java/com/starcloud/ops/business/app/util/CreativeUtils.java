package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowConfigRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.model.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.VariableSchemeStepDTO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.CustomActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.recommend.RecommendStepWrapperFactory;
import com.starcloud.ops.business.app.service.xhs.manager.CreativeImageManager;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.utils.MaterialDefineUtil;
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

    private static final CreativeImageManager CREATIVE_IMAGE_MANAGER = SpringUtil.getBean(CreativeImageManager.class);

    private static final CreativeMaterialManager CREATIVE_MATERIAL_MANAGER = SpringUtil.getBean(CreativeMaterialManager.class);

    /**
     * 获取应用的素材步骤
     *
     * @param app 应用
     * @return 素材步骤
     */
    public static WorkflowStepWrapperRespVO getMaterialStepWrapper(AppMarketRespVO app) {
        return Optional.ofNullable(app.getStepByHandler(MaterialActionHandler.class.getSimpleName()))
                .orElseThrow(() -> ServiceExceptionUtil.invalidParamException("媒体矩阵类型应用【" + app.getName() + "】第一个步骤必须是【上传素材】步骤！且有且只能有一个！"));
    }

    /**
     * 获取应用的笔记生成步骤
     *
     * @param app 应用
     * @return 笔记生成步骤
     */
    public static WorkflowStepWrapperRespVO getAssembleStepWrapper(AppMarketRespVO app) {
        return Optional.ofNullable(app.getStepByHandler(AssembleActionHandler.class.getSimpleName()))
                .orElseThrow(() -> ServiceExceptionUtil.invalidParamException("媒体矩阵类型应用【" + app.getName() + "】倒数第二个步骤必须是【笔记生成】步骤！且有且只能有一个！"));
    }

    /**
     * 获取应用的海报生成步骤
     *
     * @param app 应用
     * @return 海报生成步骤
     */
    public static WorkflowStepWrapperRespVO getPosterStepWrapper(AppMarketRespVO app) {
        return Optional.ofNullable(app.getStepByHandler(PosterActionHandler.class.getSimpleName()))
                .orElseThrow(() -> ServiceExceptionUtil.invalidParamException("媒体矩阵类型应用【" + app.getName() + "】最后一个步骤必须是【图片生成】步骤！且有且只能有一个！"));
    }

    /**
     * 获取应用的变量步骤列表
     *
     * @param app 应用
     * @return 变量步骤列表
     */
    public static List<WorkflowStepWrapperRespVO> getVariableStepWrapperList(AppMarketRespVO app) {
        return Optional.ofNullable(app)
                .map(AppMarketRespVO::getWorkflowConfig)
                .map(WorkflowConfigRespVO::stepWrapperList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(step -> VariableActionHandler.class.getSimpleName().equals(step.getHandler()))
                .collect(Collectors.toList());
    }

    /**
     * 获取应用的内容生成步骤列表
     *
     * @param app 应用
     * @return 内容生成步骤列表
     */
    public static List<WorkflowStepWrapperRespVO> getContentStepWrapperList(AppMarketRespVO app) {
        return Optional.ofNullable(app)
                .map(AppMarketRespVO::getWorkflowConfig)
                .map(WorkflowConfigRespVO::stepWrapperList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(step -> CustomActionHandler.class.getSimpleName().equals(step.getHandler()))
                .collect(Collectors.toList());
    }

    /**
     * 将变量合并到方案全局变量步骤中
     *
     * @param schemeStepList   方案步骤列表
     * @param planVariableList 计划变量列表
     */
    public static List<BaseSchemeStepDTO> mergeSchemeStepVariable(List<BaseSchemeStepDTO> schemeStepList, List<VariableItemRespVO> planVariableList) {
        // 如果方案步骤列表为空，则直接返回
        if (CollectionUtil.isEmpty(planVariableList)) {
            return schemeStepList;
        }

        List<BaseSchemeStepDTO> list = new ArrayList<>();
        for (BaseSchemeStepDTO schemeStep : schemeStepList) {
            // 如果不是全局变量步骤，则直接添加返回步骤中
            if (!VariableActionHandler.class.getSimpleName().equals(schemeStep.getCode())) {
                list.add(schemeStep);
                continue;
            }

            // 如果是全局变量步骤，则合并变量
            VariableSchemeStepDTO variableSchemeStep = (VariableSchemeStepDTO) schemeStep;
            // 获取全局变量的变量列表
            List<VariableItemRespVO> variables = variableSchemeStep.getVariableList();

            List<VariableItemRespVO> variableItemList = new ArrayList<>();
            // 将计划变量转换成map，方便后续处理
            Map<String, VariableItemRespVO> planVariableMap = planVariableList.stream().collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));
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

            variableSchemeStep.setVariableList(variableItemList);
            list.add(variableSchemeStep);
        }

        return list;
    }

    /**
     * 判断是否是图片类型变量
     *
     * @param variable 变量
     * @return boolean
     */
    public static Boolean isImageVariable(PosterVariableDTO variable) {
        return AppVariableTypeEnum.IMAGE.name().equalsIgnoreCase(variable.getType());
    }

    /**
     * 获取海报模板中图片类型的所有变量
     *
     * @param posterTemplate 海报模板
     * @return 图片类型的变量
     */
    public static List<PosterVariableDTO> getImageVariableList(PosterTemplateDTO posterTemplate) {
        return posterTemplate.posterVariableList().stream()
                .filter(CreativeUtils::isImageVariable)
                .collect(Collectors.toList());
    }

    /**
     * 预处理海报风格列表，一些数据处理，填充。
     *
     * @param posterStyleList
     * @return
     */
    public static List<PosterStyleDTO> preHandlerPosterStyleList(List<PosterStyleDTO> posterStyleList) {
        return posterStyleList.stream()
                .map(CreativeUtils::handlerPosterStyle)
                .collect(Collectors.toList());
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
            PosterTemplateDTO template = SerializationUtils.clone(posterTemplateList.get(i));

            // 如果海报模板没有UUID，添加一个
            if (StringUtils.isBlank(template.getUuid())) {
                template.setUuid(IdUtil.fastSimpleUUID());
            }

            // 获取到模板变量列表，并且填充uuid。直接生成新的uuid，防止前端复制发生问题
            List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(template.getVariableList())
                    .stream()
                    .peek(item -> item.setUuid(IdUtil.fastSimpleUUID()))
                    .collect(Collectors.toList());

            // 获取模板变量重图片类型变量的数量
            Integer totalImageCount = (int) variableList.stream().filter(item -> isImageVariable(item)).count();
            // 获取模板模式，如果为空则默认为顺序模式
            String mode = StringUtils.isBlank(template.getMode()) ? PosterModeEnum.SEQUENCE.name() : template.getMode();
            // 获取模板标题生成模式，如果为空则默认为false
            boolean isMultimodalTitle = Objects.isNull(template.getIsMultimodalTitle()) ? Boolean.FALSE : template.getIsMultimodalTitle();
            boolean isCopy = Objects.isNull(template.getIsCopy()) ? Boolean.FALSE : template.getIsCopy();

            // 模板信息补充
            template.setIndex(i);
            template.setIsMain(i == 0);
            template.setTotalImageCount(totalImageCount);
            template.setMode(mode);
            template.setIsMultimodalTitle(isMultimodalTitle);
            template.setVariableList(variableList);
            template.setIsExecute(Boolean.TRUE);
            template.setIsCopy(isCopy);

            // 添加到列表
            templateList.add(template);
        }
        // 变量都为空是否执行
        Boolean noExecuteIfEmpty = Boolean.TRUE;
        posterStyle.setNoExecuteIfEmpty(noExecuteIfEmpty);
        Integer imageCount = templateList.stream().mapToInt(PosterTemplateDTO::getTotalImageCount).sum();
        posterStyle.setTotalImageCount(imageCount);
        posterStyle.setTemplateList(templateList);
        return posterStyle;
    }

    /**
     * 合并海报模板
     *
     * @param originalTemplate 原始海报模板
     * @param template         海报模板
     * @return 合并后的海报模板
     */
    public static PosterTemplateDTO mergePosterTemplate(PosterTemplateDTO originalTemplate, PosterTemplateDTO template) {
        Map<String, PosterVariableDTO> originalVariableMap = CollectionUtil.emptyIfNull(originalTemplate.getVariableList()).stream().collect(Collectors.toMap(PosterVariableDTO::getField, Function.identity()));
        List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(template.getVariableList()).stream().map(item -> {
            if (originalVariableMap.containsKey(item.getField())) {
                PosterVariableDTO originalVariable = originalVariableMap.get(item.getField());
                item.setUuid(originalVariable.getUuid());
                item.setValue(originalVariable.getValue());
            }
            return item;
        }).collect(Collectors.toList());

        originalTemplate.setIndex(null);
        originalTemplate.setIsMain(null);
        originalTemplate.setTotalImageCount(null);
        originalTemplate.setVariableList(variableList);
        return originalTemplate;
    }

    /**
     * 获取模板变量集合，变量 UUID 和 value 的Map集合
     *
     * @param posterTemplateList 模板列表
     * @return 模板变量集合
     */
    public static Map<String, Object> getPosterStyleVariableMap(PosterStyleDTO posterStyle) {
        Map<String, Object> variableValueMap = new HashMap<>();
        for (PosterTemplateDTO posterTemplate : CollectionUtil.emptyIfNull(posterStyle.getTemplateList())) {
            List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
            for (PosterVariableDTO variable : variableList) {
                String uuid = StringUtils.isBlank(variable.getUuid()) ? IdUtil.fastSimpleUUID() : variable.getUuid();
                variableValueMap.put(uuid, variable.getValue());
            }
        }
        return variableValueMap;
    }

    /**
     * 合并应用的海报缝合配置
     *
     * @param appMarket       应用
     * @param latestAppMarket 最新应用
     * @return 应用
     */
    public static AppMarketRespVO mergeAppInformation(AppMarketRespVO appMarket, AppMarketRespVO latestAppMarket) {

        // 获取最新应用海报步骤
        WorkflowStepWrapperRespVO latestWrapper = latestAppMarket.getStepByHandler(PosterActionHandler.class.getSimpleName());
        // 如果最新海报步骤不为空，则将系统海报配置设置到计划应用中, 保证最新的系统海报配置。
        if (Objects.nonNull(latestWrapper)) {
            // 获取到最新的海报风格配置列表
            List<PosterStyleDTO> latestSystemPosterList = getSystemPosterStyleListByStepWrapper(latestWrapper);
            // 获取应用海报步骤
            WorkflowStepWrapperRespVO wrapper = appMarket.getStepByHandler(PosterActionHandler.class.getSimpleName());
            if (Objects.nonNull(wrapper)) {
                // 放入到应用中
                appMarket.putModelVariable(wrapper.getField(), CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(latestSystemPosterList));
                appMarket.putVariable(wrapper.getField(), CreativeConstants.POSTER_STYLE_CONFIG, JsonUtils.toJsonString(Collections.emptyList()));
                appMarket.putVariable(wrapper.getField(), CreativeConstants.POSTER_STYLE, StrUtil.EMPTY_JSON);
            }
        }

        // 示例取最新的
        appMarket.setName(latestAppMarket.getName());
        appMarket.setIcon(latestAppMarket.getIcon());
        appMarket.setSpell(latestAppMarket.getSpell());
        appMarket.setSpellSimple(latestAppMarket.getSpellSimple());
        appMarket.setDescription(latestAppMarket.getDescription());
        appMarket.setExample(latestAppMarket.getExample());
        appMarket.setSource(latestAppMarket.getSource());
        appMarket.setCategory(latestAppMarket.getCategory());
        appMarket.setDemo(latestAppMarket.getDemo());
        appMarket.setScenes(latestAppMarket.getScenes());
        appMarket.setSort(latestAppMarket.getSort());
        appMarket.setModel(latestAppMarket.getModel());
        return appMarket;
    }

    /**
     * 合并海报分割列表
     *
     * @param posterStyleList   海报列表
     * @param appMarketResponse 应用配置
     * @return 海报风格列表
     */
    public static PosterStyleDTO mergeImagePosterStyle(PosterStyleDTO posterStyle, AppMarketRespVO appMarketResponse) {

        // 获取海报步骤
        WorkflowStepWrapperRespVO posterStepWrapper = appMarketResponse.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.isNull(posterStepWrapper)) {
            return posterStyle;
        }
        // 获取系统海报风格配置
        List<PosterStyleDTO> systemPosterStyleList = getSystemPosterStyleListByStepWrapper(posterStepWrapper);
        // 获取自定义海报风格配置
        List<PosterStyleDTO> customPosterStyleList = getCustomPosterStyleListByStepWrapper(posterStepWrapper);

        // 系统海报风格配置转为MAP
        Map<String, PosterStyleDTO> systemPosterStyleMap = systemPosterStyleList.stream().collect(Collectors.toMap(PosterStyleDTO::getUuid, Function.identity()));
        // 找到且不为空，替换并且返回
        if (systemPosterStyleMap.containsKey(posterStyle.getUuid())) {
            if (Objects.nonNull(systemPosterStyleMap.get(posterStyle.getUuid()))) {
                return systemPosterStyleMap.get(posterStyle.getUuid());
            }
        }

        // 自定义海报风格配置转为MAP
        Map<String, PosterStyleDTO> customPosterStyleMap = customPosterStyleList.stream().collect(Collectors.toMap(PosterStyleDTO::getUuid, Function.identity()));
        // 找到且不为空，替换并且返回
        if (customPosterStyleMap.containsKey(posterStyle.getUuid())) {
            if (Objects.nonNull(customPosterStyleMap.get(posterStyle.getUuid()))) {
                return customPosterStyleMap.get(posterStyle.getUuid());
            }
        }

        return posterStyle;
    }

    /**
     * 合并海报分割列表
     *
     * @param posterStyleList   海报列表
     * @param appMarketResponse 应用配置
     * @return 海报风格列表
     */
    public static List<PosterStyleDTO> mergeImagePosterStyleList(List<PosterStyleDTO> posterStyleList, AppMarketRespVO appMarketResponse) {
        // 如果海报风格列表为空，则直接返回
        if (CollectionUtil.isEmpty(posterStyleList)) {
            return posterStyleList;
        }
        // 获取海报步骤，如果没有则直接返回
        WorkflowStepWrapperRespVO posterStepWrapper = appMarketResponse.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.isNull(posterStepWrapper)) {
            return posterStyleList;
        }

        // 获取海报系统风格配置
        List<PosterStyleDTO> systemPosterStyleList = getSystemPosterStyleListByStepWrapper(posterStepWrapper);
        // 获取自定义海报风格配置
        List<PosterStyleDTO> customPosterStyleList = getCustomPosterStyleListByStepWrapper(posterStepWrapper);

        // 海报系统风格配置转为MAP
        Map<String, PosterStyleDTO> systemPostStyleMap = systemPosterStyleList.stream().collect(Collectors.toMap(PosterStyleDTO::getUuid, Function.identity()));
        // 自定义海报风格配置转为MAP
        Map<String, PosterStyleDTO> customPostStyleMap = customPosterStyleList.stream().collect(Collectors.toMap(PosterStyleDTO::getUuid, Function.identity()));
        return posterStyleList
                .stream()
                .map(item -> {
                    // 如果不是系统配置，说明为自定义配置，进行处理
                    if (Objects.isNull(item.getSystem()) || !item.getSystem()) {
                        // 如果是自定义配置，但是自定义配置中未找到，或者为空，直接返回 null
                        if (!customPostStyleMap.containsKey(item.getUuid()) || Objects.isNull(customPostStyleMap.get(item.getUuid()))) {
                            return null;
                        }
                        // 返回最新的自定义配置
                        return customPostStyleMap.get(item.getUuid());
                    }

                    // 如果是系统配置，但是系统配置中未找到，直接返回null
                    if (!systemPostStyleMap.containsKey(item.getUuid()) || Objects.isNull(systemPostStyleMap.get(item.getUuid()))) {
                        return null;
                    }
                    // 返回最新的系统配置
                    return systemPostStyleMap.get(item.getUuid());
                })
                // 过滤掉空值，这里直接结果就是，如果自定义或者系统配置中未找到，则直接过滤掉。只有找到的才会返回最新配置
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据应用组装计划配置
     *
     * @param appMarketResponse 应用信息
     * @return 计划配置
     */
    public static CreativePlanConfigurationDTO assemblePlanConfiguration(AppMarketRespVO appMarketResponse, String source) {
        // 补充步骤默认变量
        appMarketResponse.supplementStepVariable(RecommendStepWrapperFactory.getStepVariable());

        CreativePlanConfigurationDTO configuration = new CreativePlanConfigurationDTO();
        // 默认素材列表为空
        configuration.setMaterialList(Collections.emptyList());
        // 默认海报风格列表为空
        configuration.setImageStyleList(Collections.emptyList());

        // 素材库 应用市场新建计划需要copy
        WorkflowStepWrapperRespVO materialStepWrapper = appMarketResponse.getStepByHandler(MaterialActionHandler.class.getSimpleName());
        if (Objects.nonNull(materialStepWrapper) && CreativePlanSourceEnum.MARKET.name().equalsIgnoreCase(source)) {
            // copy 素材库
            CREATIVE_MATERIAL_MANAGER.upgradeMaterialLibrary(appMarketResponse);
        }

        // 默认应用信息为传入的应用信息
        configuration.setAppInformation(appMarketResponse);

        // 海报风格配置
        WorkflowStepWrapperRespVO stepWrapper = appMarketResponse.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.nonNull(stepWrapper)) {
            // 获取到海报风格配置
            List<PosterStyleDTO> posterStyleList = getPosterStyleListByStepWrapper(stepWrapper);
            // 获取到最新的海报模板
            posterStyleList = mergeImagePosterStyleList(posterStyleList, appMarketResponse);
            configuration.setImageStyleList(posterStyleList);

            // 应用参数处理
            List<PosterStyleDTO> systemPosterStyleList = getSystemPosterStyleListByStepWrapper(stepWrapper);
            // 重新放入应用
            appMarketResponse.putModelVariable(stepWrapper.getField(), CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(systemPosterStyleList));
            // 应用参数变为空
            appMarketResponse.putVariable(stepWrapper.getField(), CreativeConstants.CUSTOM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(Collections.emptyList()));
            appMarketResponse.putVariable(stepWrapper.getField(), CreativeConstants.POSTER_STYLE_CONFIG, JsonUtils.toJsonString(Collections.emptyList()));
            appMarketResponse.putVariable(stepWrapper.getField(), CreativeConstants.POSTER_STYLE, StrUtil.EMPTY_JSON);
        }

        return configuration;
    }

    /**
     * 变量替换
     * 占位符不存在，不替换为空字符串
     *
     * @param variableMap 变量集合
     * @param valueMap    值集合
     * @return 替换之后集合
     */
    public static Map<String, Object> replaceVariable(Map<String, Object> variableMap, Map<String, Object> valueMap, Boolean defEmpty) {
        return AppContext.parseMapFromVariablesValues(variableMap, valueMap, defEmpty);
    }

    /**
     * 移除占位符
     *
     * @param input 输入
     * @return 输出
     */
    public static String removePlaceholder(String input) {
        return input.replaceAll("\\{\\{.*?}}", "");
    }

    /**
     * 处理海报步骤
     *
     * @param posterStepWrapper 处理海报步骤
     * @return 海报步骤
     */
    public static WorkflowStepWrapperRespVO handlerPosterStepWrapper(WorkflowStepWrapperRespVO posterStepWrapper) {
        Map<String, PosterTemplateDTO> latestPosterTemplateMap = CREATIVE_IMAGE_MANAGER.mapPosterTemplate();
        // 处理海报系统风格配置
        List<PosterStyleDTO> systemPosterStyleList = getSystemPosterStyleListByStepWrapper(posterStepWrapper);
        // 合并海报风格列表
        systemPosterStyleList = mergePosterStyleList(systemPosterStyleList, latestPosterTemplateMap);

        // 处理自定义海报风格配置
        List<PosterStyleDTO> customPosterStyleList = getCustomPosterStyleListByStepWrapper(posterStepWrapper);
        // 合并海报风格列表
        customPosterStyleList = mergePosterStyleList(customPosterStyleList, latestPosterTemplateMap);

        // 海报配置
        List<PosterStyleDTO> posterStyleList = getPosterStyleListByStepWrapper(posterStepWrapper);
        // 合并海报风格列表
        posterStyleList = mergePosterStyleList(posterStyleList, latestPosterTemplateMap);

        // 重新放入海报步骤
        posterStepWrapper.putModelVariable(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(systemPosterStyleList));
        posterStepWrapper.putVariable(CreativeConstants.CUSTOM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(posterStyleList));
        posterStepWrapper.putVariable(CreativeConstants.POSTER_STYLE_CONFIG, JsonUtils.toJsonString(posterStyleList));
        return posterStepWrapper;
    }

    /**
     * 合并海报风格列表
     *
     * @param posterStyleList         海报风格列表
     * @param latestPosterTemplateMap 最新的海报模板
     * @return 合并之后的海报风格列表
     */
    private static List<PosterStyleDTO> mergePosterStyleList(List<PosterStyleDTO> posterStyleList, Map<String, PosterTemplateDTO> latestPosterTemplateMap) {

        for (PosterStyleDTO posterStyle : posterStyleList) {
            List<PosterTemplateDTO> templateList = posterStyle.getTemplateList();
            for (PosterTemplateDTO posterTemplate : templateList) {
                // 获取最新的海报模板
                PosterTemplateDTO latestPosterTemplate = latestPosterTemplateMap.get(posterTemplate.getCode());
                if (Objects.isNull(latestPosterTemplate)) {
                    continue;
                }

                // 获取最新的海报模板
                posterTemplate.setExample(latestPosterTemplate.getExample());
            }
            posterStyle.setTemplateList(templateList);
        }

        return posterStyleList;
    }

    /**
     * 根据应用步骤获取素材库列表
     *
     * @param materialWrapper 应用步骤
     * @return 素材库列表
     */
    public static List<Map<String, Object>> getMaterialListByStepWrapper(WorkflowStepWrapperRespVO materialWrapper) {
        // 素材列表配置
        if (Objects.isNull(materialWrapper)) {
            return Collections.emptyList();
        }

        // 获取到素材库列表
        String materialListString = materialWrapper.getVariableToString(CreativeConstants.MATERIAL_LIST);
        if (StringUtils.isBlank(materialListString) || "[]".equals(materialListString) || "null".equalsIgnoreCase(materialListString)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> materialList = MaterialDefineUtil.parseData(materialListString);
        if (CollectionUtil.isEmpty(materialList)) {
            return Collections.emptyList();
        }

        return materialList;
    }
    
    /**
     * 判断素材内容显示类型 true显示图片 false显示列表
     */
    public static boolean judgePicture(AppMarketRespVO appRespVO) {
        return CREATIVE_MATERIAL_MANAGER.judgePicture(appRespVO);
    }

    /**
     * 根据应用步骤获取素材库列表
     *
     * @param materialWrapper 应用步骤
     * @return 素材库列表
     */
    public static List<MaterialFieldConfigDTO> getMaterialFieldByStepWrapper(WorkflowStepWrapperRespVO materialWrapper) {
        // 素材列表配置
        if (Objects.isNull(materialWrapper)) {
            return Collections.emptyList();
        }

        // 获取到素材库列表
        String materialConfigString = materialWrapper.getVariableToString(CreativeConstants.MATERIAL_DEFINE);
        if (StringUtils.isBlank(materialConfigString) || "[]".equals(materialConfigString) || "null".equalsIgnoreCase(materialConfigString)) {
            return Collections.emptyList();
        }

        List<MaterialFieldConfigDTO> materialFieldConfigList = CREATIVE_MATERIAL_MANAGER.getHeader(materialLibraryJsonVariable);
        if (CollectionUtil.isEmpty(materialFieldConfigList)) {
            return Collections.emptyList();
        }

        return materialFieldConfigList;
    }

    /**
     * 根据应用步骤获取风格配置
     *
     * @param posterWrapper 海报步骤
     */
    public static PosterStyleDTO getPosterStyleByStepWrapper(WorkflowStepWrapperRespVO posterWrapper) {
        // 图片风格配置
        if (Objects.isNull(posterWrapper)) {
            return null;
        }

        String posterStyleString = posterWrapper.getVariableToString(CreativeConstants.POSTER_STYLE);
        if (StringUtils.isBlank(posterStyleString) || "{}".equals(posterStyleString) || "null".equalsIgnoreCase(posterStyleString)) {
            return null;
        }

        PosterStyleDTO posterStyle = JsonUtils.parseObject(posterStyleString, PosterStyleDTO.class);
        if (Objects.isNull(posterStyle)) {
            return null;
        }

        return posterStyle;
    }

    /**
     * 根据应用步骤获取风格配置
     *
     * @param posterWrapper 海报步骤
     * @return 风格配置
     */
    public static List<PosterStyleDTO> getPosterStyleListByStepWrapper(WorkflowStepWrapperRespVO posterWrapper) {
        // 图片风格配置
        if (Objects.isNull(posterWrapper)) {
            return Collections.emptyList();
        }

        String posterStyleString = posterWrapper.getVariableToString(CreativeConstants.POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(posterStyleString) || "[]".equals(posterStyleString) || "null".equalsIgnoreCase(posterStyleString)) {
            return Collections.emptyList();
        }

        List<PosterStyleDTO> posterStyleList = JsonUtils.parseArray(posterStyleString, PosterStyleDTO.class);
        if (CollectionUtil.isEmpty(posterStyleList)) {
            return Collections.emptyList();
        }

        return posterStyleList;
    }

    /**
     * 根据应用步骤获取自定义风格配置
     *
     * @param posterWrapper 海报步骤
     * @return 风格配置
     */
    public static List<PosterStyleDTO> getCustomPosterStyleListByStepWrapper(WorkflowStepWrapperRespVO posterWrapper) {
        // 图片风格配置
        if (Objects.isNull(posterWrapper)) {
            return Collections.emptyList();
        }

        String posterStyleString = posterWrapper.getVariableToString(CreativeConstants.CUSTOM_POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(posterStyleString) || "[]".equals(posterStyleString) || "null".equalsIgnoreCase(posterStyleString)) {
            return Collections.emptyList();
        }

        List<PosterStyleDTO> posterStyleList = JsonUtils.parseArray(posterStyleString, PosterStyleDTO.class);
        if (CollectionUtil.isEmpty(posterStyleList)) {
            return Collections.emptyList();
        }

        return CollectionUtil.emptyIfNull(posterStyleList).stream()
                .filter(item -> Objects.isNull(item.getSystem()) || !item.getSystem())
                .collect(Collectors.toList());
    }


    /**
     * 根据应用步骤获取系统海报风格配置
     *
     * @param posterWrapper 海报步骤
     * @return 风格配置
     */
    public static List<PosterStyleDTO> getSystemPosterStyleListByStepWrapper(WorkflowStepWrapperRespVO posterWrapper) {
        // 图片风格配置
        if (Objects.isNull(posterWrapper)) {
            return Collections.emptyList();
        }

        String posterStyleString = posterWrapper.getModelVariableToString(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(posterStyleString) || "[]".equals(posterStyleString) || "null".equalsIgnoreCase(posterStyleString)) {
            return Collections.emptyList();
        }

        List<PosterStyleDTO> posterStyleList = JsonUtils.parseArray(posterStyleString, PosterStyleDTO.class);
        if (CollectionUtil.isEmpty(posterStyleList)) {
            return Collections.emptyList();
        }

        return CollectionUtil.emptyIfNull(posterStyleList).stream()
                .filter(item -> Objects.nonNull(item.getSystem()) && item.getSystem())
                .collect(Collectors.toList());
    }

}



