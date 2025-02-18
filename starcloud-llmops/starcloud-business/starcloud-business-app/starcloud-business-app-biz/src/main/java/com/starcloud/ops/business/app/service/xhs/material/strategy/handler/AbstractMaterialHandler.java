package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.SliceCountReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.SliceUsageCountReqVO;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.CustomActionHandler;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeContentGenerateModelEnum;
import com.starcloud.ops.business.app.model.plan.PlanTotalCount;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.model.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.service.materiallibrary.MaterialLibraryService;
import com.starcloud.ops.business.app.service.xhs.material.strategy.metadata.MaterialMetadata;
import com.starcloud.ops.framework.common.api.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 资料库处理器抽象类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Component
public abstract class AbstractMaterialHandler {

    /**
     * 素材库服务
     */
    private static final MaterialLibraryService MATERIAL_LIBRARY_SERVICE = SpringUtil.getBean(MaterialLibraryService.class);

    /**
     * 提取素材索引正则
     */
    private static final Pattern PATTERN = Pattern.compile("\\.docs\\[(\\d+)]");

    /**
     * 分组字段
     */
    private static final String GROUP = "group";

    /**
     * 获取到 [n] 中的数字，如果包含多个 [n],只返回最大的数字
     *
     * @param input 输入
     * @return 返回的数字，没有匹配到，返回 -1
     */
    protected static Integer matcherMax(String input) {
        if (StringUtils.isBlank(input)) {
            return -1;
        }
        // 定义正则表达式匹配方括号中的数字
        Matcher matcher = PATTERN.matcher(input);
        // 如果匹配到，则返回最大的数字。
        int max = -1;
        try {
            while (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                if (number > max) {
                    max = number;
                }
            }
        } catch (Exception exception) {
            return -1;
        }
        return max;
    }

    protected static Integer matchMaxOrZero(String input) {
        Integer max = matcherMax(input);
        return max == -1 ? 0 : max + 1;
    }

    /**
     * 不同的素材类型，验证海报风格的要求可能不一样。
     * 提供一个默认的实现，子类可以覆盖
     *
     * @param posterStyle 海报风格
     */
    public void validatePosterStyle(PosterStyleDTO posterStyle) {
        AppValidate.notNull(posterStyle, "创作方案配置异常！海报风格不能为空！");
        AppValidate.notEmpty(posterStyle.getTemplateList(), "创作方案配置异常！海报模板不能为空！");
        for (PosterTemplateDTO posterTemplate : posterStyle.getTemplateList()) {
            AppValidate.notNull(posterStyle, "创作方案配置异常！海报模板不能为空！");
            for (PosterVariableDTO variable : CollectionUtil.emptyIfNull(posterTemplate.getVariableList())) {
                AppValidate.notNull(variable, "创作方案配比异常！海报模板变量不能为空！");
                AppValidate.notNull(variable.getType(), "创作方案配置异常！海报模板变量类型不能为空！");
                AppValidate.notNull(variable.getField(), "创作方案配置异常！海报模板变量不能为空！");
            }
        }
    }

    /**
     * 处理资料库列表，返回处理后的资料库列表
     *
     * @param materialList   资料库列表
     * @param posterStyleMap 海报风格列表
     * @return 处理后的资料库列表
     */
    public Map<String, List<Map<String, Object>>> handleMaterialMap(List<Map<String, Object>> materialList,
                                                                    Map<String, PosterStyleDTO> posterStyleMap,
                                                                    MaterialMetadata metadata) {

        Map<String, List<Map<String, Object>>> map = this.doHandleMaterialMap(materialList, posterStyleMap, metadata);
        // 如果不需要更新素材使用量，直接返回，默认更新素材使用量
        if (!metadata.getIsUpdateMaterialUsageCount()) {
            return map;
        }
        // 素材使用量增加
        List<Map<String, Object>> list = new ArrayList<>();
        map.values().forEach(list::addAll);
        // 去重
        list = new ArrayList<>(list.stream()
                .collect(Collectors.toMap(
                        m -> m.get("__id__"),
                        m -> m,
                        (existing, replacement) -> existing))
                .values());
        List<SliceCountReqVO> sliceCountRequestList = new ArrayList<>();
        for (Map<String, Object> mapItem : list) {
            Long id = Long.parseLong(mapItem.get("__id__").toString());
            SliceCountReqVO sliceCountRequest = new SliceCountReqVO();
            sliceCountRequest.setSliceId(id);
            sliceCountRequest.setNums(1);
            sliceCountRequestList.add(sliceCountRequest);
        }

        SliceUsageCountReqVO sliceUsageCountRequest = new SliceUsageCountReqVO();
        if (CreativePlanSourceEnum.isApp(metadata.getPlanSource().name())) {
            sliceUsageCountRequest.setAppUid(metadata.getAppUid());
        } else {
            sliceUsageCountRequest.setAppUid(metadata.getPlanUid());
        }
        sliceUsageCountRequest.setSliceCountReqVOS(sliceCountRequestList);
        MATERIAL_LIBRARY_SERVICE.materialLibrarySliceUsageCount(sliceUsageCountRequest);

        return map;
    }

    /**
     * 处理资料库列表，返回处理后的资料库列表。
     * 默认不做处理，子类可以重写此方法来自定义处理逻辑
     *
     * @param posterStyle  海报风格
     * @param materialList 资料库列表
     * @param metadata     素材元数据
     * @return 处理后的海报风格
     */
    public PosterStyleDTO handlePosterStyle(PosterStyleDTO posterStyle, List<Map<String, Object>> materialList, MaterialMetadata metadata) {
        // 如果资料库为空，直接返回海报风格，不做处理
//        if (CollectionUtil.isEmpty(materialList)) {
//            return posterStyle;
//        }
//        PosterStyleDTO style = SerializationUtils.clone(posterStyle);
//        // 进行变量替换
//        Map<String, Object> replaceValueMap = this.replaceVariable(style, materialList, metadata, Boolean.TRUE);
//
//        List<PosterTemplateDTO> templates = Lists.newArrayList();
//
//        List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
//        // 如果只有一个模板图片，直接执行。
//        if (templateList.size() == 1) {
//            for (PosterTemplateDTO posterTemplate : templateList) {
//                posterTemplate.setIsExecute(Boolean.TRUE);
//                templates.add(posterTemplate);
//            }
//            style.setTemplateList(templates);
//            return style;
//        }
//        // 进行海报风格的处理
//        for (PosterTemplateDTO template : templateList) {
//            // 默认设置为 TRUE
//            template.setIsExecute(Boolean.TRUE);
//            // 模板变量列表
//            List<PosterVariableDTO> variableList = CollectionUtil.emptyIfNull(template.getVariableList());
//
//            // 只要存在是变量，且值不为空的，就需要生成图片
//            boolean anyMatchNotBlankValue = variableList.stream()
//                    .anyMatch(variable -> StringUtil.objectNotBlank(replaceValueMap.get(variable.getUuid())));
//
//            if (anyMatchNotBlankValue) {
//                templates.add(template);
//                continue;
//            }
//
//            // 设置为FALSE,表示不需要生成改图片
//            template.setIsExecute(Boolean.FALSE);
//            templates.add(template);
//        }
//
//        style.setTemplateList(templates);
//        return style;
        return posterStyle;
    }

    /**
     * 计算生成任务总数量。的
     *
     * @param materialList    素材列表
     * @param posterStyleList 海报风格列表
     * @return 生成任务总数量
     */
    public PlanTotalCount calculateTotalCount(List<Map<String, Object>> materialList,
                                              List<PosterStyleDTO> posterStyleList,
                                              MaterialMetadata metadata) {

        List<Integer> needMaterialSize = new ArrayList<>();
        for (PosterStyleDTO posterStyle : CollectionUtil.emptyIfNull(posterStyleList)) {
            needMaterialSize.add(computeNeedMaterialSize(posterStyle, metadata.getAppInformation()));
        }

        needMaterialSize = needMaterialSize.stream()
                .filter(item -> Objects.nonNull(item) && item > 0)
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(needMaterialSize)) {
            throw ServiceExceptionUtil.invalidParamException("您生成的笔记不依赖素材！请使用【立即生成】功能！");
        }

        // 获取素材字段配置
        String group = getGroupField(metadata.getMaterialFieldList());

        // 分组字段不为空的素材
        List<Map<String, Object>> groupMaterial = materialList.stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtil.objectNotBlank(item.get(group)))
                .collect(Collectors.toList());

        // 1. 如果有分组的素材为空，直接按照默认的复制逻辑进行复制
        if (CollectionUtil.isEmpty(groupMaterial)) {
            return this.calculateNoGroupTotalCount(materialList, needMaterialSize);
        }

        // 将同一组的素材分组，并且保持原有的顺序。
        LinkedHashMap<Object, List<Map<String, Object>>> collect = groupMaterial.stream()
                .collect(Collectors.groupingBy(item -> item.get(group), LinkedHashMap::new, Collectors.toList()));

        // 如果分组为空，说明选择素材出现问题。
        if (CollectionUtil.isEmpty(collect)) {
            PlanTotalCount totalCount = new PlanTotalCount();
            totalCount.setTotal(0);
            totalCount.setWarning("选择的素材数量不足以生成一篇笔记，请重新选择后重试！");
        }

        return PlanTotalCount.of(collect.size());
    }

    /**
     * 计算生成任务总数量。不是分组模式
     *
     * @param materialList     素材列表
     * @param needMaterialSize 每个风格需要的素材数量
     * @return 生成任务总数量
     */
    public PlanTotalCount calculateNoGroupTotalCount(List<Map<String, Object>> materialList,
                                                     List<Integer> needMaterialSize) {

        int totalCount = 0;
        int currentMaterialSize = materialList.size();
        int loopCount = 0;
        do {
            loopCount++;
            for (int i = 0; i < needMaterialSize.size(); i++) {
                int currentNeed = needMaterialSize.get(i);
                // 如果素材数量为0，直接返回
                if (currentMaterialSize == 0) {
                    return PlanTotalCount.of(totalCount);
                }
                if (currentNeed > currentMaterialSize) {
                    // 如果是第一次循环，并且是第一个海报风格，说明素材数量不足
                    if (loopCount == 1 && i == 0) {
                        throw ServiceExceptionUtil.invalidParamException("素材数量不足以生成一篇笔记【"
                                + currentMaterialSize + "/" + currentNeed + "】，请添加素材后重试！");
                    }
                    // 其他情况，给提示，即第totalCount + 1 个海报风格的素材数量不足
                    return PlanTotalCount.of(totalCount, "选择的素材数量不足以生成第 " + (totalCount + 1) + " 篇笔记，我们将会为您生成 " + totalCount + " 篇笔记。");
                } else {
                    totalCount++;
                    currentMaterialSize -= currentNeed;
                }
            }
        } while (currentMaterialSize > 0);

        return PlanTotalCount.of(totalCount);
    }

    /**
     * 处理素材为一个Map，如果需要不同的分组逻辑，子类重写此方法即可
     *
     * @param materialList   素材列表
     * @param posterStyleMap 海报风格列表
     * @return 海报素材 map
     */
    protected Map<String, List<Map<String, Object>>> doHandleMaterialMap(List<Map<String, Object>> materialList,
                                                                         Map<String, PosterStyleDTO> posterStyleMap,
                                                                         MaterialMetadata metadata) {

        // 复制一份素材列表，防止修改原始数据
        List<Map<String, Object>> copyMaterialList = SerializationUtils.clone((ArrayList<Map<String, Object>>) materialList);
        // 获取素材字段配置，前面已经做过校验，不需要校验
        String group = getGroupField(metadata.getMaterialFieldList());

        // 分组字段值不为空的素材
        List<Map<String, Object>> groupMaterial = copyMaterialList.stream()
                .filter(Objects::nonNull).filter(item -> StringUtil.objectNotBlank(item.get(group)))
                .collect(Collectors.toList());

        // 获取素材使用模式
        MaterialUsageModel usageModel = metadata.getMaterialUsageModel();

        // 1. 如果所有的素材，分组字段值都为空，则按照默认复制逻辑复制即可。
        if (CollectionUtil.isEmpty(groupMaterial)) {
            if (MaterialUsageModel.SELECT.equals(usageModel)) {
                return this.defaultMaterialListMap(copyMaterialList, posterStyleMap, metadata);
            }
            return this.defaultCopyMaterialListMap(copyMaterialList, posterStyleMap, metadata);
        }

        // 将同一组的素材分组，并且保持原有的顺序。
        LinkedHashMap<Object, List<Map<String, Object>>> materialGroupMap = groupMaterial.stream()
                .collect(Collectors.groupingBy(item -> item.get(group), LinkedHashMap::new, Collectors.toList()));

        // 2.如果组的数量大于生成生成任务的数量，则按照顺序从分组中取出 posterStyleMap.size() 个分组即可
        if (materialGroupMap.size() >= posterStyleMap.size()) {
            int index = 0;
            Map<String, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();
            List<List<Map<String, Object>>> list = new ArrayList<>(materialGroupMap.values());
            for (Map.Entry<String, PosterStyleDTO> entry : posterStyleMap.entrySet()) {
                String key = entry.getKey();
                // 数组不会越界，因为 materialGroupMap.size() >= posterStyleMap.size()
                List<Map<String, Object>> groupList = list.get(index);
                resultMap.put(key, groupList);
                index++;
            }
            return resultMap;
        }

        // 3. 否则，一部分按照分组复制，一部分按照默认复制逻辑复制
        Map<String, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();
        int index = 0;
        Map<String, PosterStyleDTO> noHandlerMap = new LinkedHashMap<>();
        List<List<Map<String, Object>>> list = new ArrayList<>(materialGroupMap.values());
        for (Map.Entry<String, PosterStyleDTO> entry : posterStyleMap.entrySet()) {
            String key = entry.getKey();
            if (index < materialGroupMap.size()) {
                List<Map<String, Object>> groupList = list.get(index);
                resultMap.put(key, groupList);
            } else {
                noHandlerMap.put(key, entry.getValue());
            }
            index++;
        }

        // 分组字段为空的素材
        List<Map<String, Object>> noGroupMaterial = copyMaterialList.stream()
                .filter(Objects::nonNull)
                .filter(item -> Objects.isNull(item.get(group)))
                .collect(Collectors.toList());

        // 将没有分组的素材按照默认复制逻辑复制
        Map<String, List<Map<String, Object>>> noGroupMap;
        if (MaterialUsageModel.SELECT.equals(usageModel)) {
            noGroupMap = this.defaultMaterialListMap(noGroupMaterial, noHandlerMap, metadata);
        } else {
            noGroupMap = this.defaultCopyMaterialListMap(noGroupMaterial, noHandlerMap, metadata);
        }

        // 将分组的素材和没有分组的素材合并
        resultMap.putAll(noGroupMap);

        return resultMap;
    }

    /**
     * 默认逻辑： 将资料库列表按照指定的大小和总数进行分组
     * 进行复制素材库。
     *
     * @param materialList   资料库列表
     * @param posterStyleMap 需要复制的列表
     * @return 分组后的资料库列表
     */
    protected Map<String, List<Map<String, Object>>> defaultCopyMaterialListMap(List<Map<String, Object>> materialList,
                                                                                Map<String, PosterStyleDTO> posterStyleMap,
                                                                                MaterialMetadata metadata) {
        // 结果集合
        Map<String, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();

        // 获取每一个风格需要的素材数量的Map
        Map<String, Integer> needSizeMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<String, PosterStyleDTO> entry : posterStyleMap.entrySet()) {
            PosterStyleDTO posterStyle = entry.getValue();
            Integer size = computeNeedMaterialSize(posterStyle, metadata.getAppInformation());
            if (materialList.size() < size && index == 0) {
                throw ServiceExceptionUtil.invalidParamException("素材数量不足以生成一篇笔记【"
                        + materialList.size() + "/" + size + "】，请添加素材后重试！");
            }
            needSizeMap.put(entry.getKey(), size);
            index++;
        }

        List<Map<String, Object>> copiedMaterialList = SerializationUtils.clone((ArrayList<Map<String, Object>>) materialList);

        // 记录原始素材列表的大小
        int originalSize = materialList.size();
        // 计算所有海报风格需要的素材数量
        int copyTotal = needSizeMap.values().stream().mapToInt(Integer::intValue).sum();

        // 如果素材列表的数量小于需要复制的总数，说明需要进行复制。
        if (originalSize < copyTotal) {
            // 如果imageList的数量不够，则需要先对imageList进行扩容
            int requiredSize = copyTotal - originalSize;
            // 将imageList从头开始按顺序复制，直至满足扩容要求
            for (int i = 0; i < requiredSize; i++) {
                copiedMaterialList.add(materialList.get(i % originalSize));
            }
        }

        // 扩容后按照顺序复制图片
        int currentIndex = 0;
        for (Map.Entry<String, Integer> entry : needSizeMap.entrySet()) {
            int size = entry.getValue();
            List<Map<String, Object>> subList = new ArrayList<>(copiedMaterialList.subList(currentIndex, currentIndex + size));
            resultMap.put(entry.getKey(), subList);
            currentIndex += size;
        }

        return resultMap;
    }

    /**
     * 默认逻辑： 将资料库列表按照指定的大小和总数进行分组
     *
     * @param materialList   资料库列表
     * @param posterStyleMap 需要复制的列表
     * @return 分组后的资料库列表
     */
    protected Map<String, List<Map<String, Object>>> defaultMaterialListMap(List<Map<String, Object>> materialList,
                                                                            Map<String, PosterStyleDTO> posterStyleMap,
                                                                            MaterialMetadata metadata) {
        // 结果集合
        Map<String, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();

        // 获取每一个风格需要的素材数量的Map
        Map<String, Integer> needSizeMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<String, PosterStyleDTO> entry : posterStyleMap.entrySet()) {
            PosterStyleDTO posterStyle = entry.getValue();
            Integer size = computeNeedMaterialSize(posterStyle, metadata.getAppInformation());
            if (materialList.size() < size && index == 0) {
                throw ServiceExceptionUtil.invalidParamException("素材数量不足以生成一篇笔记【"
                        + materialList.size() + "/" + size + "】，请添加素材后重试！");
            }
            needSizeMap.put(entry.getKey(), size);
            index++;
        }

        // 记录原始素材列表的大小
        int originalSize = materialList.size();
        int currentIndex = 0;
        for (Map.Entry<String, Integer> entry : needSizeMap.entrySet()) {
            String key = entry.getKey();
            int currentNeed = entry.getValue();
            if (currentNeed > originalSize) {
                if (currentIndex == 0) {
                    throw ServiceExceptionUtil.invalidParamException("素材数量不足以生成一篇笔记，请添加素材后重试！");
                } else {
                    break;
                }
            } else {
                List<Map<String, Object>> subMaterialList = new ArrayList<>(materialList.subList(currentIndex, currentIndex + currentNeed));
                resultMap.put(key, subMaterialList);
                currentIndex += currentNeed;
                originalSize -= currentNeed;
            }
        }

        return resultMap;
    }

    /**
     * 获取分组字段
     *
     * @param materialFieldList 素材字段配置
     * @return 分组字段
     */
    protected String getGroupField(List<MaterialFieldConfigDTO> materialFieldList) {
        // 如果没有分组字段，则定义一个默认分组字段，作为默认分组字段
        MaterialFieldConfigDTO defaultGroup = new MaterialFieldConfigDTO();
        defaultGroup.setFieldName(GROUP);
        defaultGroup.setIsGroupField(Boolean.TRUE);

        // 获取分组字段
        MaterialFieldConfigDTO groupField = materialFieldList.stream()
                .filter(item -> Objects.nonNull(item.getIsGroupField()) && Objects.equals(item.getIsGroupField(), Boolean.TRUE))
                .findFirst()
                .orElse(defaultGroup);

        return groupField.getFieldName();
    }

    /**
     * 计算需要的素材数量
     *
     * @param posterStyle    海报风格
     * @param appInformation 应用信息
     * @return 需要的素材数量
     */
    public Integer computeNeedMaterialSize(PosterStyleDTO posterStyle, AppMarketRespVO appInformation) {
        Integer posterNeedSize = computePosterStyleNeedMaterialSize(posterStyle);
        Integer appNeedSize = computeAppInformationNeedSize(appInformation);
        return Math.max(posterNeedSize, appNeedSize);
    }

    /**
     * 计算海报风格需要的素材数量
     *
     * @param posterStyle 海报风格
     * @return 需要的素材数量
     */
    protected Integer computePosterStyleNeedMaterialSize(PosterStyleDTO posterStyle) {
        // 计算图片配置需要的素材数量。
        List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());
        List<Integer> materialIndexList = new ArrayList<>();
        for (PosterTemplateDTO template : posterTemplateList) {
            // 计算每一个模板需要的素材数量，即每一个模板中选择的素材的最大索引 +1
            // 如果整个图片模板未曾引用过素材，则整个模板需要的素材数量为 0
            Integer maxIndex = CollectionUtil.emptyIfNull(template.getVariableList())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(item -> matchMaxOrZero(String.valueOf(item.getValue())))
                    .max(Comparator.comparingInt(Integer::intValue)).orElse(0);

            materialIndexList.add(maxIndex);
        }
        // 图片配置需要的素材数量。
        return CollectionUtil.emptyIfNull(materialIndexList)
                .stream()
                .max(Comparator.comparingInt(Integer::intValue))
                .orElse(0);
    }

    /**
     * 计算应用配置需要的素材数量
     *
     * @param appInformation 应用信息
     * @return 应用配置需要的素材数量
     */
    protected Integer computeAppInformationNeedSize(AppMarketRespVO appInformation) {
        // 计算生成步骤需要的最大的素材数量
        Integer completionStepNeedSize = computeAppCompletionStepNeedSize(appInformation);
        // 计算笔记生成步骤需要的最大的素材数量
        Integer noteStepNeedSize = computeAppNoteStepNeedSize(appInformation);
        return Math.max(completionStepNeedSize, noteStepNeedSize);
    }

    /**
     * 计算生成步骤需要的最大的素材数量
     *
     * @param appInformation 应用信息
     * @return 最大的素材数量
     */
    private Integer computeAppCompletionStepNeedSize(AppMarketRespVO appInformation) {
        List<WorkflowStepWrapperRespVO> completionStepList = listCompletionStep(appInformation);
        if (CollectionUtil.isEmpty(completionStepList)) {
            return 0;
        }
        List<Integer> needCount = new ArrayList<>();
        for (WorkflowStepWrapperRespVO stepWrapper : completionStepList) {
            String generate = stepWrapper.getVariableToString(CreativeConstants.GENERATE_MODE);
            if (CreativeContentGenerateModelEnum.AI_PARODY.name().equals(generate)) {
                String parodyRequirement = stepWrapper.getVariableToString(CreativeConstants.PARODY_REQUIREMENT);
                needCount.add(matchMaxOrZero(parodyRequirement));
            } else if (CreativeContentGenerateModelEnum.AI_CUSTOM.name().equals(generate)) {
                String customRequirement = stepWrapper.getVariableToString(CreativeConstants.CUSTOM_REQUIREMENT);
                needCount.add(matchMaxOrZero(customRequirement));
            } else {
                needCount.add(0);
            }
        }
        return needCount.stream().max(Comparator.comparingInt(Integer::intValue)).orElse(0);
    }

    /**
     * 计算笔记生成步骤需要的最大的素材数量
     *
     * @param appInformation 应用信息
     * @return 最大的素材数量
     */
    private Integer computeAppNoteStepNeedSize(AppMarketRespVO appInformation) {
        WorkflowStepWrapperRespVO notStepWrapper = appInformation.getStepByHandler(AssembleActionHandler.class);
        // 获取标题变量值
        String title = notStepWrapper.getVariableToString(CreativeConstants.TITLE);
        Integer titleNeedSize = matchMaxOrZero(title);

        // 获取内容变量值
        String content = notStepWrapper.getVariableToString(CreativeConstants.CONTENT);
        Integer contentNeedSize = matchMaxOrZero(content);
        return Math.max(titleNeedSize, contentNeedSize);
    }

    /**
     * 获取生成相关步骤
     *
     * @param appInformation 应用信息
     * @return 生成相关步骤
     */
    private List<WorkflowStepWrapperRespVO> listCompletionStep(AppMarketRespVO appInformation) {
        if (Objects.isNull(appInformation) || Objects.isNull(appInformation.getWorkflowConfig())) {
            return Collections.emptyList();
        }

        List<WorkflowStepWrapperRespVO> workflowStepList = appInformation.getWorkflowConfig().stepWrapperList();
        return workflowStepList.stream()
                .filter(step -> CustomActionHandler.class.getSimpleName().equalsIgnoreCase(step.getHandler()))
                .collect(Collectors.toList());
    }


}
