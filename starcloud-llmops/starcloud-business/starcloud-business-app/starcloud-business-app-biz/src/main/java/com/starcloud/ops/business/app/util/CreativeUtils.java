package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.BaseSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.MaterialSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.ParagraphSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.PosterSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action.VariableSchemeStepDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.ParagraphActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterTitleModeEnum;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.poster.PosterStyleEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.poster.PosterTemplateEntity;
import com.starcloud.ops.business.app.service.xhs.scheme.entity.poster.PosterVariableEntity;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
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
        return (VariableSchemeStepDTO) schemeStepList.stream()
                .filter(item -> VariableActionHandler.class.getSimpleName().equals(item.getCode()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取段落方案步骤, 如果没有则返回null
     *
     * @param schemeStepList 方案步骤列表
     * @return 段落方案步骤
     */
    public static MaterialSchemeStepDTO getMaterialSchemeStep(List<BaseSchemeStepDTO> schemeStepList) {
        return (MaterialSchemeStepDTO) schemeStepList.stream()
                .filter(item -> MaterialActionHandler.class.getSimpleName().equals(item.getCode()))
                .findFirst()
                .orElse(null);
    }

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
        return posterStyleList.stream().map(CreativeUtils::handlerPosterStyle).collect(Collectors.toList());
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

            // 获取到模板变量列表，并且填充uuid。
            List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(template.getVariableList())
                    .stream()
                    .peek(item -> {
                        if (StringUtils.isBlank(item.getUuid())) {
                            item.setUuid(IdUtil.fastSimpleUUID());
                        }
                    })
                    .collect(Collectors.toList());

            // 获取模板变量重图片类型变量的数量
            Integer totalImageCount = (int) variableList.stream().filter(item -> isImageVariable(item)).count();
            // 获取模板模式，如果为空则默认为随机模式
            String mode = StringUtils.isBlank(template.getMode()) ? PosterModeEnum.RANDOM.name() : template.getMode();
            // 获取模板标题生成模式，如果为空则默认为默认模式
            String titleGenerateMode = StringUtils.isBlank(template.getTitleGenerateMode()) ? PosterTitleModeEnum.DEFAULT.name() : template.getTitleGenerateMode();

            // 模板信息补充
            template.setIndex(i + 1);
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
     * 获取模板变量集合，变量 UUID 和 value 的Map集合
     *
     * @param posterTemplateList 模板列表
     * @return 模板变量集合
     */
    public static Map<String, Object> getPosterStyleVariableMap(PosterStyleEntity posterStyle) {
        Map<String, Object> variableValueMap = new HashMap<>();
        for (PosterTemplateEntity posterTemplate : CollectionUtil.emptyIfNull(posterStyle.getTemplateList())) {
            List<PosterVariableEntity> variableList = CollectionUtil.emptyIfNull(posterTemplate.getVariableList());
            for (PosterVariableEntity variable : variableList) {
                String uuid = StringUtils.isBlank(variable.getUuid()) ? IdUtil.fastSimpleUUID() : variable.getUuid();
                variableValueMap.put(uuid, variable.getValue());
            }
        }
        return variableValueMap;
    }

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
     * 变量替换
     *
     * @param variableMap 变量集合
     * @param valueMap    值集合
     * @return 替换之后集合
     */
    public static Map<String, Object> replaceVariable(Map<String, Object> variableMap, Map<String, Object> valueMap) {
        Map<String, Object> replaceVariableMap = new HashMap<>();
        MapUtil.emptyIfNull(variableMap).forEach((key, value) -> {

            if (Objects.isNull(value)) {
                replaceVariableMap.put(key, value);
            }

            // 进行变量替换
            Object handleValue = QLExpressUtils.execute(String.valueOf(value), valueMap);

            if (handleValue instanceof String) {
                // 二次替换、递归处理？
                if (QLExpressUtils.check((String) handleValue)) {
                    handleValue = QLExpressUtils.execute(String.valueOf(handleValue), valueMap);
                }

                // 如果替换之后结果为空，则使用原始值，真正执行时候需要二次替换的变量
                if (StringUtils.isBlank((String) handleValue)) {
                    handleValue = value;
                }

                // 替换{xxx}占位符
                handleValue = StrUtil.format(String.valueOf(handleValue), valueMap);
            }

            replaceVariableMap.put(key, handleValue);
        });
        return replaceVariableMap;
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

}



