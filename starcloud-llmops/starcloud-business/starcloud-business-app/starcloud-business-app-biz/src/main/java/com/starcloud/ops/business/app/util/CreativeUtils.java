package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.MaterialSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.ParagraphSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.VariableSchemeStepDTO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ParagraphActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterTitleModeEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * 获取段落方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 段落方案步骤
     */
    public static VariableSchemeStepDTO getVariableSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (VariableSchemeStepDTO) schemeStepList.stream().filter(item -> VariableActionHandler.class.getSimpleName().equals(item.getCode())).findFirst().orElse(null);
    }

    /**
     * 获取段落方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 段落方案步骤
     */
    public static MaterialSchemeStepDTO getMaterialSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (MaterialSchemeStepDTO) schemeStepList.stream().filter(item -> MaterialActionHandler.class.getSimpleName().equals(item.getCode())).findFirst().orElse(null);
    }

    /**
     * 获取海报方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 海报方案步骤
     */
    public static PosterSchemeStepDTO getPosterSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (PosterSchemeStepDTO) schemeStepList.stream().filter(item -> PosterActionHandler.class.getSimpleName().equals(item.getCode())).findFirst().orElse(null);
    }

    /**
     * 获取段落方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 段落方案步骤
     */
    public static ParagraphSchemeStepDTO getParagraphSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (ParagraphSchemeStepDTO) schemeStepList.stream().filter(item -> ParagraphActionHandler.class.getSimpleName().equals(item.getCode())).findFirst().orElse(null);
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
     * 预处理海报风格列表，一些数据处理，填充。
     *
     * @param posterStyleList
     * @return
     */
    public static List<PosterStyleDTO> preHandlerPosterStyleList(List<PosterStyleDTO> posterStyleList) {
        return posterStyleList.stream()
                .map(CreativeUtils::handlerPosterStyle)
                .sorted(Comparator.comparingInt(PosterStyleDTO::getIndex))
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

        // 加上一个UUID
        if (StringUtils.isBlank(posterStyle.getUuid())) {
            posterStyle.setUuid(IdUtil.fastSimpleUUID());
        }

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
            // 获取模板标题生成模式，如果为空则默认为默认模式
            String titleGenerateMode = StringUtils.isBlank(template.getTitleGenerateMode()) ? PosterTitleModeEnum.DEFAULT.name() : template.getTitleGenerateMode();

            // 模板信息补充
            template.setIndex(i);
            template.setIsMain(i == 0);
            template.setTotalImageCount(totalImageCount);
            template.setMode(mode);
            template.setTitleGenerateMode(titleGenerateMode);
            template.setVariableList(variableList);

            // 添加到列表
            templateList.add(template);
        }
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
     * 合并海报分割列表
     *
     * @param posterStyleList   海报列表
     * @param appMarketResponse 应用配置
     * @return 海报风格列表
     */
    public static List<PosterStyleDTO> mergePosterStyle(List<PosterStyleDTO> posterStyleList, AppMarketRespVO appMarketResponse) {
        if (CollectionUtil.isEmpty(posterStyleList)) {
            return posterStyleList;
        }
        // 获取海报步骤
        WorkflowStepWrapperRespVO posterStepWrapper = appMarketResponse.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.isNull(posterStepWrapper)) {
            return posterStyleList;
        }
        // 获取应用的海报风格配置
        String posterConfig = appMarketResponse.getStepModelVariableValue(posterStepWrapper.getField(), CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(posterConfig) || "null".equalsIgnoreCase(posterConfig)) {
            posterConfig = "[]";
        }
        List<PosterStyleDTO> styleList = JsonUtils.parseArray(posterConfig, PosterStyleDTO.class);
        if (CollectionUtil.isEmpty(styleList)) {
            return posterStyleList;
        }
        return mergePosterStyleList(posterStyleList, styleList);
    }

    /**
     * 合并海报风格
     *
     * @param posterStyleList       海报风格列表
     * @param systemPosterStyleList 海报风格列表
     * @return 合并之后的海报风格
     */
    public static List<PosterStyleDTO> mergePosterStyleList(List<PosterStyleDTO> posterStyleList, List<PosterStyleDTO> systemPosterStyleList) {
        // 转为MAP
        Map<String, PosterStyleDTO> styleMap = systemPosterStyleList.stream().collect(Collectors.toMap(PosterStyleDTO::getUuid, Function.identity()));
        return posterStyleList
                .stream()
                .map(item -> {
                    // 如果不是系统配置，直接返回
                    if (Objects.isNull(item.getSystem()) || !item.getSystem()) {
                        return item;
                    }
                    // 如果是系统配置，但是系统配置中未找到，直接返回null
                    if (!styleMap.containsKey(item.getUuid()) || Objects.isNull(styleMap.get(item.getUuid()))) {
                        return null;
                    }
                    // 返回最新的系统配置
                    return styleMap.get(item.getUuid());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 合并应用的海报缝合配置
     *
     * @param appMarket       应用
     * @param latestAppMarket 最新应用
     * @return 应用
     */
    public static AppMarketRespVO mergeAppPosterStyleConfig(AppMarketRespVO appMarket, AppMarketRespVO latestAppMarket) {
        // 获取最新应用海报步骤
        WorkflowStepWrapperRespVO latestWrapper = latestAppMarket.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.isNull(latestWrapper)) {
            return appMarket;
        }
        // 获取应用海报步骤
        WorkflowStepWrapperRespVO wrapper = appMarket.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.isNull(wrapper)) {
            return appMarket;
        }

        // 获取最新的海报风格配置
        String latestPosterConfig = latestWrapper.getStepModelVariableValue(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(latestPosterConfig) || "null".equalsIgnoreCase(latestPosterConfig)) {
            latestPosterConfig = "[]";
        }
        // 获取到最新的海报风格配置列表
        List<PosterStyleDTO> latestPosterList = JsonUtils.parseArray(latestPosterConfig, PosterStyleDTO.class);
        // 过滤掉非系统的配置
        latestPosterList = CollectionUtil.emptyIfNull(latestPosterList).stream()
                .filter(item -> item.getSystem())
                .collect(Collectors.toList());

        // 放入到应用中
        appMarket.putStepModelVariable(
                wrapper.getField(),
                Collections.singletonMap(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(latestPosterList))
        );

        // 应用参数变为空
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.POSTER_STYLE_CONFIG, JsonUtils.toJsonString(Collections.emptyList()));
        variableMap.put(CreativeConstants.POSTER_STYLE, StrUtil.EMPTY_JSON);
        appMarket.putStepVariable(wrapper.getField(), variableMap);

        // 示例取最新的
        appMarket.setExample(latestAppMarket.getExample());
        return appMarket;
    }

    /**
     * 根据应用组装计划配置
     *
     * @param appMarketResponse 应用信息
     * @return 计划配置
     */
    public static CreativePlanConfigurationDTO assemblePlanConfiguration(AppMarketRespVO appMarketResponse) {
        CreativePlanConfigurationDTO configuration = new CreativePlanConfigurationDTO();
        configuration.setMaterialList(Collections.emptyList());

        // 海报分割配置
        WorkflowStepWrapperRespVO stepWrapper = appMarketResponse.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.isNull(stepWrapper)) {
            configuration.setImageStyleList(Collections.emptyList());
            configuration.setAppInformation(appMarketResponse);
            return configuration;
        }

        // 获取到海报风格配置
        String posterConfig = stepWrapper.getStepVariableValue(CreativeConstants.POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(posterConfig) || "null".equalsIgnoreCase(posterConfig)) {
            posterConfig = "[]";
        }
        List<PosterStyleDTO> posterStyleList = JsonUtils.parseArray(posterConfig, PosterStyleDTO.class);

        // 处理系统海报风格配置
        String systemPosterConfig = stepWrapper.getStepModelVariableValue(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG);
        if (StringUtils.isBlank(systemPosterConfig) || "null".equalsIgnoreCase(systemPosterConfig)) {
            systemPosterConfig = "[]";
        }
        List<PosterStyleDTO> systemPosterList = JsonUtils.parseArray(systemPosterConfig, PosterStyleDTO.class);

        // 保证 posterStyleList 是从 systemPosterList 获取的最新的
        posterStyleList = mergePosterStyleList(posterStyleList, systemPosterList);

        // 过过滤掉非系统配置
        systemPosterList = CollectionUtil.emptyIfNull(systemPosterList).stream()
                .filter(item -> item.getSystem())
                .collect(Collectors.toList());

        // 重新放入应用
        Map<String, Object> modelVariableMap = new HashMap<>();
        modelVariableMap.put(CreativeConstants.SYSTEM_POSTER_STYLE_CONFIG, JsonUtils.toJsonString(systemPosterList));
        appMarketResponse.putStepModelVariable(stepWrapper.getField(), modelVariableMap);

        // 应用参数变为空
        Map<String, Object> variableMap = new HashMap<>();
        variableMap.put(CreativeConstants.POSTER_STYLE_CONFIG, JsonUtils.toJsonString(Collections.emptyList()));
        variableMap.put(CreativeConstants.POSTER_STYLE, StrUtil.EMPTY_JSON);
        appMarketResponse.putStepVariable(stepWrapper.getField(), variableMap);

        configuration.setImageStyleList(posterStyleList);
        configuration.setAppInformation(appMarketResponse);
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
     * 校验素材
     *
     * @param appInformation 应用信息
     */
    public static void validateMaterial(AppMarketRespVO appInformation) {
        // 素材列表校验
        WorkflowStepWrapperRespVO materialWrapper = appInformation.getStepByHandler(MaterialActionHandler.class.getSimpleName());
        if (Objects.nonNull(materialWrapper)) {
            String materialType = materialWrapper.getStepVariableValue(CreativeConstants.MATERIAL_TYPE);
            if (StringUtils.isBlank(materialType) || "null".equalsIgnoreCase(materialType)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "素材类型不能为空！请联系管理员！");
            }

            String materialList = materialWrapper.getStepVariableValue(CreativeConstants.MATERIAL_LIST);
            if (StringUtils.isBlank(materialList) || "[]".equals(materialList) || "null".equalsIgnoreCase(materialList)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "素材列表不能为空！请上传素材后重试！");
            }
        }
    }

    /**
     * 校验风格
     *
     * @param appInformation 应用信息
     */
    public static void validatePosterStyle(AppMarketRespVO appInformation) {
        // 图片风格配置
        WorkflowStepWrapperRespVO posterWrapper = appInformation.getStepByHandler(PosterActionHandler.class.getSimpleName());
        if (Objects.nonNull(posterWrapper)) {
            String posterStyle = posterWrapper.getStepVariableValue(CreativeConstants.POSTER_STYLE);
            if (StringUtils.isBlank(posterStyle) || "{}".equals(posterStyle) || "null".equalsIgnoreCase(posterStyle)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.PARAMETER_EXCEPTION.getCode(), "图片生成配置不能为空！请配置图片生成后重试！");
            }
        }
    }
}



