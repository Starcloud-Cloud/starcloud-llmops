package com.starcloud.ops.business.app.service.xhs.manager;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.business.app.feign.dto.PosterParam;
import com.starcloud.ops.business.app.feign.dto.PosterTemplate;
import com.starcloud.ops.business.app.feign.dto.PosterTemplateType;
import com.starcloud.ops.business.app.model.poster.PosterTemplateDTO;
import com.starcloud.ops.business.app.model.poster.PosterTemplateTypeDTO;
import com.starcloud.ops.business.app.model.poster.PosterVariableDTO;
import com.starcloud.ops.business.app.service.poster.PosterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
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
@Slf4j
@Component
public class CreativeImageManager {

    /**
     * 图片标识
     */
    private static final String IMAGE = "image";

    /**
     * 文字标识
     */
    private static final String TEXT = "text";

    @Resource
    private PosterService posterService;

    /**
     * 获取图片模板
     *
     * @param templateId 模板ID
     * @return 模板
     */
    public PosterTemplateDTO getPosterTemplate(String templateId) {
        return convertTemplate(posterService.getTemplate(templateId));
    }

    /**
     * 获取图片模板
     *
     * @return 图片模板
     */
    public List<PosterTemplateDTO> listPosterTemplate() {
        List<PosterTemplate> templates = posterService.listTemplate();
        return CollectionUtil.emptyIfNull(templates)
                .stream()
                .map(CreativeImageManager::convertTemplate)
                .collect(Collectors.toList());
    }

    /**
     * 获取图片模板 Map
     *
     * @return 图片模板 Map
     */
    public Map<String, PosterTemplateDTO> mapPosterTemplate() {
        return CollectionUtil.emptyIfNull(listPosterTemplate())
                .stream()
                .collect(Collectors.toMap(PosterTemplateDTO::getCode, Function.identity()));
    }

    /**
     * 根据类型分组获取模板列表
     *
     * @return 模板列表
     */
    public List<PosterTemplateTypeDTO> listPosterTemplateType() {
        List<PosterTemplateType> templateTypeList = posterService.listPosterTemplateType();
        return CollectionUtil.emptyIfNull(templateTypeList).stream()
                .map(item -> {
                    // 获取模板列表
                    List<PosterTemplateDTO> templateList = CollectionUtil.emptyIfNull(item.getList())
                            .stream()
                            .map(CreativeImageManager::convertTemplate)
                            .collect(Collectors.toList());
                    // 组装模板类型
                    PosterTemplateTypeDTO templateType = new PosterTemplateTypeDTO();
                    templateType.setId(item.getId());
                    templateType.setName(item.getLabel());
                    templateType.setOrder(item.getOrder());
                    templateType.setList(templateList);
                    return templateType;
                }).collect(Collectors.toList());
    }

    /**
     * 转换模板
     *
     * @param templateItem 模板
     * @return 模板
     */
    @NotNull
    private static PosterTemplateDTO convertTemplate(PosterTemplate templateItem) {

        // 获取模板列表
        List<PosterParam> params = CollectionUtil.emptyIfNull(templateItem.getParams());
        // 获取模板变量列表中图片数量
        int imageNumber = (int) params.stream().filter(param -> IMAGE.equals(param.getType())).count();
        // 处理模板
        List<PosterVariableDTO> variables = params.stream()
                .map(param -> {
                    Integer order = Optional.ofNullable(param.getOrder()).orElse(Integer.MAX_VALUE);
                    if (IMAGE.equals(param.getType())) {
                        return ofImageVariable(param.getId(), param.getName(), order);
                    } else if (TEXT.equals(param.getType())) {
                        return ofInputVariable(param.getId(), param.getName(), order, param.getCount());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(PosterVariableDTO::getOrder))
                .collect(Collectors.toList());

        // 组装数据
        PosterTemplateDTO template = new PosterTemplateDTO();
        template.setCode(templateItem.getId());
        template.setName(templateItem.getLabel());
        template.setExample(templateItem.getTempUrl());
        template.setVariableList(variables);
//        template.setJson(templateItem.getJson());
        template.setTotalImageCount(imageNumber);
        return template;
    }

    /**
     * 获取变量列表
     *
     * @param requestParams 请求参数
     * @return 变量列表
     */
    public static List<PosterVariableDTO> listVariable(String requestParams) {
        if (StringUtils.isBlank(requestParams)) {
            return Collections.emptyList();
        }
        List<PosterParam> params;
        try {
            params = JsonUtils.parseArray(requestParams, PosterParam.class);
        } catch (Exception exception) {
            log.error("[CreativeImageManager][listVariable] 解析参数失败，requestParams: {}", requestParams, exception);
            return Collections.emptyList();
        }
        return CollectionUtils.emptyIfNull(params).stream()
                .map(param -> {
                    Integer order = Optional.ofNullable(param.getOrder()).orElse(Integer.MAX_VALUE);
                    if (IMAGE.equals(param.getType())) {
                        return ofImageVariable(param.getId(), param.getName(), order);
                    } else if (TEXT.equals(param.getType())) {
                        return ofInputVariable(param.getId(), param.getName(), order, param.getCount());
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(PosterVariableDTO::getOrder))
                .collect(Collectors.toList());
    }

    /**
     * 获取文本变量
     *
     * @param field 字段
     * @param label 值
     * @return 文本变量
     */
    public static PosterVariableDTO ofImageVariable(String field, String label, Integer order) {
        PosterVariableDTO variableItem = new PosterVariableDTO();
        variableItem.setField(field);
        variableItem.setLabel(label);
        variableItem.setDescription(label);
        variableItem.setOrder(order);
        variableItem.setType(AppVariableTypeEnum.IMAGE.name());
        variableItem.setStyle(AppVariableStyleEnum.IMAGE.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(Lists.newArrayList());
        return variableItem;
    }

    /**
     * 获取文本变量
     *
     * @param field 字段
     * @param label 值
     * @return 文本变量
     */
    public static PosterVariableDTO ofInputVariable(String field, String label, Integer order, Integer count) {
        PosterVariableDTO variableItem = new PosterVariableDTO();
        variableItem.setField(field);
        variableItem.setLabel(label);
        variableItem.setDescription(label);
        variableItem.setOrder(order);
        variableItem.setType(AppVariableTypeEnum.TEXT.name());
        variableItem.setStyle(AppVariableStyleEnum.INPUT.name());
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.TRUE);
        variableItem.setOptions(Lists.newArrayList());
        variableItem.setCount(count);
        return variableItem;
    }
}
