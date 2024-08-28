package com.starcloud.ops.business.app.service.xhs.material.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.SliceCountReqVO;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library.SliceUsageCountReqVO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialUsageModel;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanSourceEnum;
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
     * 生成数据相关正则
     */
    private static final Pattern DATA_PATTERN = Pattern.compile("\\.data|\\.title|\\.content|\\.tagList");

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

    /**
     * 匹配数据
     *
     * @param input 输入
     * @return 返回匹配结果
     */
    protected static Integer matchData(String input) {
        if (StringUtils.isBlank(input)) {
            return -1;
        }
        Matcher matcher = DATA_PATTERN.matcher(input);
        if (matcher.find()) {
            return 1;
        }
        return -1;
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
     * @param materialList    资料库列表
     * @param posterStyleList 海报风格列表
     * @return 处理后的资料库列表
     */
    public Map<Integer, List<Map<String, Object>>> handleMaterialMap(List<Map<String, Object>> materialList, List<PosterStyleDTO> posterStyleList, MaterialMetadata metadata) {
        if (CollectionUtil.isEmpty(materialList) || CollectionUtil.isEmpty(posterStyleList)) {
            return Collections.emptyMap();
        }

        List<Integer> needMaterialSizeList = computeNeedMaterialSize(posterStyleList);
        if (CollectionUtil.isEmpty(needMaterialSizeList)) {
            return Collections.emptyMap();
        }
        Map<Integer, List<Map<String, Object>>> map = this.doHandleMaterialMap(materialList, needMaterialSizeList, metadata);

        // 素材使用量增加
        List<Map<String, Object>> list = new ArrayList<>();
        map.values().forEach(list::addAll);
        // 去重
        list = new ArrayList<>(list.stream().collect(Collectors.toMap(m -> m.get("__id__"), m -> m, (existing, replacement) -> existing)).values());
        List<SliceCountReqVO> sliceCountRequestList = new ArrayList<>();
        for (Map<String, Object> mapItem : list) {
            Long id = (Long) mapItem.get("__id__");
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
     * 计算每个海报风格需要的素材数量 <p>
     * 如果需要不同的分组逻辑，子类重写此方法即可
     *
     * @param posterStyleList 海报风格列表
     * @return 每个海报风格需要的素材数量
     */
    protected List<Integer> computeNeedMaterialSize(List<PosterStyleDTO> posterStyleList) {
        return this.defaultComputeNeedMaterialSize(posterStyleList);
    }

    /**
     * 计算生成任务总数量。的
     *
     * @param materialList    素材列表
     * @param posterStyleList 海报风格列表
     * @return 生成任务总数量
     */
    public PlanTotalCount calculateTotalCount(List<Map<String, Object>> materialList, List<PosterStyleDTO> posterStyleList) {
        // 计算每个海报风格需要的素材数量
        List<Integer> needMaterialSize = computeNeedMaterialSize(posterStyleList);
        if (CollectionUtil.isEmpty(needMaterialSize)) {
            return PlanTotalCount.of(0);
        }
        int totalCount = 0;
        int currentMaterialSize = materialList.size();
        int loopCount = 0;
        do {
            loopCount++;
            for (int i = 0; i < needMaterialSize.size(); i++) {
                int currentNeed = needMaterialSize.get(i);

                if (currentNeed > currentMaterialSize) {
                    // 如果是第一次循环，并且是第一个海报风格，说明素材数量不足
                    if (loopCount == 1 && i == 0) {
                        throw ServiceExceptionUtil.invalidParamException("选择的素材数量不足以生成一篇笔记，请重新选择后重试！");
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
     * @param materialList     素材列表
     * @param materialSizeList 每个海报风格需要的素材数量
     * @return 海报素材 map
     */
    protected Map<Integer, List<Map<String, Object>>> doHandleMaterialMap(List<Map<String, Object>> materialList, List<Integer> materialSizeList, MaterialMetadata metadata) {

        // 复制一份素材列表，防止修改原始数据
        List<Map<String, Object>> copyMaterialList = SerializationUtils.clone((ArrayList<Map<String, Object>>) materialList);

        // 获取素材字段配置
        List<MaterialFieldConfigDTO> materialFieldList = metadata.getMaterialFieldList();
        if (CollectionUtil.isEmpty(materialFieldList)) {
            return this.defaultMaterialListMap(copyMaterialList, materialSizeList);
        }

        // 如果没有分组字段，则定义一个默认分组字段，作为默认分组字段
        MaterialFieldConfigDTO defaultGroup = new MaterialFieldConfigDTO();
        defaultGroup.setFieldName(GROUP);
        defaultGroup.setIsGroupField(Boolean.TRUE);
        // 获取分组字段
        MaterialFieldConfigDTO groupField = materialFieldList.stream().filter(item -> Objects.nonNull(item.getIsGroupField()) && Objects.equals(item.getIsGroupField(), Boolean.TRUE)).findFirst().orElse(defaultGroup);

        // 如果分组字段为空，直接按照默认的复制逻辑进行复制
        if (StringUtil.isBlank(groupField.getFieldName())) {
            return this.defaultMaterialListMap(copyMaterialList, materialSizeList);
        }

        String group = groupField.getFieldName();

        // 分组字段不为空的素材
        List<Map<String, Object>> groupMaterial = copyMaterialList.stream().filter(Objects::nonNull).filter(item -> StringUtil.objectNotBlank(item.get(group))).collect(Collectors.toList());

        // 1. 如果有分组的素材为空，直接按照默认的复制逻辑进行复制
        if (CollectionUtil.isEmpty(groupMaterial)) {
            return this.defaultMaterialListMap(copyMaterialList, materialSizeList);
        }

        // 将同一组的素材分组，并且保持原有的顺序。
        LinkedHashMap<Object, List<Map<String, Object>>> collect = groupMaterial.stream().collect(Collectors.groupingBy(item -> item.get(group), LinkedHashMap::new, Collectors.toList()));

        // 2. 如果分组的数量大于等于 materialSizeList 的数量，说明分组的数量大于等于需要复制的数量，这时候，直接按照 materialSizeList 的数量进行复制即可
        if (collect.size() >= materialSizeList.size()) {
            Map<Integer, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();
            int index = 0;
            for (Map.Entry<Object, List<Map<String, Object>>> entry : collect.entrySet()) {
                resultMap.put(index, entry.getValue());
                index++;
                if (index >= materialSizeList.size()) {
                    break;
                }
            }
            return resultMap;
        }

        // 3. 否则，一部分按照分组复制，一部分按照默认复制逻辑复制
        Map<Integer, List<Map<String, Object>>> groupMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<Object, List<Map<String, Object>>> entry : collect.entrySet()) {
            groupMap.put(index, entry.getValue());
            index++;
        }

        // 此时不需要担心下标越界，因为 collect.size() < materialSizeList.size()
        // 剩余的部分按照默认复制逻辑复制
        // 截取 materialSizeList 的剩余部分
        List<Integer> subList = materialSizeList.subList(collect.size(), materialSizeList.size());

        // 分组字段为空的素材
        List<Map<String, Object>> noGroupMaterial = copyMaterialList.stream().filter(Objects::nonNull).filter(item -> Objects.isNull(item.get(group))).collect(Collectors.toList());

        // 将没有分组的素材按照默认复制逻辑复制
        Map<Integer, List<Map<String, Object>>> noGroupMap = this.defaultMaterialListMap(noGroupMaterial, subList);

        // 将分组的素材和没有分组的素材合并
        Map<Integer, List<Map<String, Object>>> resultMap = new LinkedHashMap<>(groupMap);
        int noGroupIndex = groupMap.size();
        for (Map.Entry<Integer, List<Map<String, Object>>> entry : noGroupMap.entrySet()) {
            resultMap.put(noGroupIndex, entry.getValue());
            noGroupIndex++;
        }

        return resultMap;
    }

    /**
     * 获取风格海报素材选择最大索引列表
     *
     * @param posterStyleList 海报列表
     * @return 风格海报素材选择最大索引列表
     */
    protected List<Integer> defaultComputeNeedMaterialSize(List<PosterStyleDTO> posterStyleList) {
        List<Integer> materialIndexList = new ArrayList<>();
        for (PosterStyleDTO posterStyle : CollectionUtil.emptyIfNull(posterStyleList)) {
            // 如果风格为空，填充 0
            if (Objects.isNull(posterStyle)) {
                materialIndexList.add(0);
                continue;
            }

            List<PosterTemplateDTO> posterTemplateList = CollectionUtil.emptyIfNull(posterStyle.getTemplateList());
            // 如果海报模板为空，填充 0
            if (CollectionUtil.isEmpty(posterTemplateList)) {
                materialIndexList.add(0);
                continue;
            }

            // 报模板，图片变量值，获取到选择素材的最大索引列表
            List<Integer> templateIndexList = new ArrayList<>();
            for (PosterTemplateDTO template : posterTemplateList) {
                // 如果海报模板为空，设置默认值为 -1
                if (Objects.isNull(template)) {
                    templateIndexList.add(-1);
                    continue;
                }
                // 获取每一个海报模板，图片变量值，获取到选择素材的最大索引
                Integer maxIndex = CollectionUtil.emptyIfNull(template.getVariableList()).stream().filter(Objects::nonNull)
                        //.filter(CreativeUtils::isImageVariable)
                        .map(item -> {
                            Integer matcher = matcherMax(String.valueOf(item.getValue()));
                            if (matcher == -1) {
                                return -1;
                            }
                            return matcher + 1;
                        }).max(Comparator.comparingInt(Integer::intValue)).orElse(-1);

                templateIndexList.add(maxIndex);
            }

            // 所有的海报模板中获取最大的那个素材索引。如果没有，为 -1
            Integer maxIndex = templateIndexList.stream().max(Comparator.comparingInt(Integer::intValue)).orElse(-1);
            if (maxIndex == -1) {
                // 如果没有找到，设置该值为图片总数
                materialIndexList.add(posterStyle.getTotalImageCount());
            } else {
                materialIndexList.add(maxIndex);
            }
        }
        return materialIndexList;
    }

    /**
     * 默认逻辑： 将资料库列表按照指定的大小和总数进行分组
     * 不会进行复制素材
     *
     * @param materialList 资料库列表
     * @param needSizeList 需要复制的列表
     * @return 分组后的资料库列表
     */
    protected Map<Integer, List<Map<String, Object>>> defaultCopyMaterialListMap(List<Map<String, Object>> materialList, List<Integer> needSizeList) {
        // 结果集合
        Map<Integer, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();

        // 如果素材列表为空或者需要复制的数量集合为空
        if (CollectionUtil.isEmpty(materialList) || CollectionUtil.isEmpty(needSizeList)) {
            return resultMap;
        }

        // 处理制的数量集合
        needSizeList = needSizeList.stream().map(item -> {
            if (item == null || item <= 0) {
                return 0;
            }
            return item;
        }).collect(Collectors.toList());

        // 记录原始素材列表的大小
        int originalSize = materialList.size();
        // 计算需要复制的总数
        int copyTotal = needSizeList.stream().mapToInt(Integer::intValue).sum();

        // 如果素材列表的数量小于需要复制的总数，说明需要进行复制。
        if (originalSize < copyTotal) {
            // 如果imageList的数量不够，则需要先对imageList进行扩容
            int requiredSize = copyTotal - originalSize;
            // 将imageList从头开始按顺序复制，直至满足扩容要求
            for (int i = 0; i < requiredSize; i++) {
                materialList.add(materialList.get(i % originalSize));
            }
        }

        // 扩容后按照顺序复制图片
        int currentIndex = 0;
        for (int i = 0; i < needSizeList.size(); i++) {
            int size = needSizeList.get(i);
            List<Map<String, Object>> copiedImages = new ArrayList<>(materialList.subList(currentIndex, currentIndex + size));
            resultMap.put(i, copiedImages);
            currentIndex += size;
        }

        return resultMap;
    }

    /**
     * 默认逻辑： 将资料库列表按照指定的大小和总数进行分组
     *
     * @param materialList 资料库列表
     * @param needSizeList 需要复制的列表
     * @return 分组后的资料库列表
     */
    protected Map<Integer, List<Map<String, Object>>> defaultMaterialListMap(List<Map<String, Object>> materialList, List<Integer> needSizeList) {
        // 结果集合
        Map<Integer, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();

        // 如果素材列表为空或者需要复制的数量集合为空
        if (CollectionUtil.isEmpty(materialList) || CollectionUtil.isEmpty(needSizeList)) {
            return resultMap;
        }

        // 处理制的数量集合
        needSizeList = needSizeList.stream().map(item -> {
            if (item == null || item <= 0) {
                return 0;
            }
            return item;
        }).collect(Collectors.toList());

        // 记录原始素材列表的大小
        int originalSize = materialList.size();
        int currentIndex = 0;
        for (int i = 0; i < needSizeList.size(); i++) {
            int currentNeed = needSizeList.get(i);
            if (currentNeed > originalSize) {
                if (i == 0) {
                    throw ServiceExceptionUtil.invalidParamException("素材数量不足以生成一篇笔记，请添加素材后重试！");
                } else {
                    break;
                }
            } else {
                List<Map<String, Object>> subMaterialList = new ArrayList<>(materialList.subList(currentIndex, currentIndex + currentNeed));
                resultMap.put(i, subMaterialList);
                currentIndex += currentNeed;
                originalSize -= currentNeed;
            }

        }

        return resultMap;
    }
}
