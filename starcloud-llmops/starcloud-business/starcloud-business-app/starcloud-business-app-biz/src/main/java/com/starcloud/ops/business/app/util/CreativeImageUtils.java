package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanImageExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateDTO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
public class CreativeImageUtils {

    private static final String IMAGE = "IMAGE";
    private static final String TITLE = "TITLE";
    private static final String SUB_TITLE = "SUB_TITLE";

    /**
     * 转换成执行参数
     *
     * @param content     图片的内容
     * @param copyWriting 文案的内容
     * @return 执行参数
     */
    public static XhsImageStyleExecuteRequest getImageStyleExecuteRequest(CreativeContentDO content,
                                                                          CopyWritingContentDTO copyWriting,
                                                                          Map<String, CreativeImageTemplateDTO> posterTemplateMap,
                                                                          Boolean force) {
        // 获取使用图片
        List<String> useImageList = JSONUtil.parseArray(content.getUsePicture()).toList(String.class);
        // 获取执行参数
        CreativePlanExecuteDTO executeParams = CreativeContentConvert.INSTANCE.toExecuteParams(content.getExecuteParams());
        // 获取图片风格执行参数
        CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = executeParams.getImageStyleExecuteRequest();
        // 获取风格中图片执行参数
        List<CreativePlanImageExecuteDTO> imageRequests = CollectionUtil.emptyIfNull(imageStyleExecuteRequest.getImageRequests());
        // 转换成执行参数
        XhsImageStyleExecuteRequest executeRequest = new XhsImageStyleExecuteRequest();
        List<XhsImageExecuteRequest> imageExecuteRequests = Lists.newArrayList();
        for (CreativePlanImageExecuteDTO imageRequest : imageRequests) {
            XhsImageExecuteRequest request = new XhsImageExecuteRequest();
            // 如果强制执行，则使用最新的模板参数
            if (force && posterTemplateMap.containsKey(imageRequest.getId())) {
                CreativeImageTemplateDTO imageTemplate = posterTemplateMap.get(imageRequest.getId());
                imageRequest.setParams(mergeVariables(imageRequest.getParams(), imageTemplate.getVariables()));
            }
            request.setId(imageRequest.getId());
            request.setName(imageRequest.getName());
            request.setIndex(imageRequest.getIndex());
            request.setIsMain(imageRequest.getIsMain());
            request.setParams(transformParams(imageRequest, useImageList, copyWriting, force));
            imageExecuteRequests.add(request);
        }
        executeRequest.setId(imageStyleExecuteRequest.getId());
        executeRequest.setName(imageStyleExecuteRequest.getName());
        executeRequest.setImageRequests(imageExecuteRequests);
        return executeRequest;
    }

    /**
     * 获取小红书批量图片执行参数
     *
     * @param style 图片模板列表
     * @return 图片执行参数
     */
    public static CreativePlanImageStyleExecuteDTO getCreativeImageStyleExecute(CreativeImageStyleDTO style, List<String> useImageList, Map<String, CreativeImageTemplateDTO> posterMap) {
        // 图片参数信息
        List<CreativePlanImageExecuteDTO> imageExecuteRequestList = Lists.newArrayList();
        List<CreativeImageTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        for (int i = 0; i < templateList.size(); i++) {
            CreativeImageTemplateDTO posterTemplate = mergeTemplate(templateList.get(i), posterMap);
            CreativePlanImageExecuteDTO imageExecuteRequest = new CreativePlanImageExecuteDTO();
            imageExecuteRequest.setIndex(i + 1);
            imageExecuteRequest.setIsMain(i == 0);
            imageExecuteRequest.setId(posterTemplate.getId());
            imageExecuteRequest.setName(posterTemplate.getName());
            imageExecuteRequest.setParams(transformParams(posterTemplate, useImageList));
            imageExecuteRequestList.add(imageExecuteRequest);
        }
        // 图片风格执行参数
        CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = new CreativePlanImageStyleExecuteDTO();
        imageStyleExecuteRequest.setId(style.getId());
        imageStyleExecuteRequest.setName(style.getName());
        imageStyleExecuteRequest.setImageRequests(imageExecuteRequestList);
        return imageStyleExecuteRequest;
    }

    /**
     * 合并海报模板
     *
     * @param template  海报模板
     * @param posterMap 海报模板集合，最新的海报模板
     * @return 合并后的海报模板
     */
    public static CreativeImageTemplateDTO mergeTemplate(CreativeImageTemplateDTO template, Map<String, CreativeImageTemplateDTO> posterMap) {
        if (Objects.isNull(template)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_IMAGE_TEMPLATE_NOT_EXIST);
        }
        // 海报模板不存在，直接跳过
        if (!posterMap.containsKey(template.getId())) {
            log.warn("海报模板不存在: 模板名称：{}, 模板ID：{}!", template.getName(), template.getId());
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_IMAGE_TEMPLATE_NOT_EXIST, template.getName());
        }
        CreativeImageTemplateDTO posterTemplate = posterMap.get(template.getId());
        if (Objects.isNull(posterTemplate)) {
            log.warn("海报模板不存在: 模板名称：{}, 模板ID：{}!", template.getName(), template.getId());
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_IMAGE_TEMPLATE_NOT_EXIST, template.getName());
        }

        // 非图片类型参数如果有值，则覆盖
        posterTemplate.setVariables(mergeVariables(CollectionUtil.emptyIfNull(template.getVariables()), CollectionUtil.emptyIfNull(posterTemplate.getVariables())));
        return posterTemplate;
    }

    /**
     * 合并海报模板变量
     *
     * @param variableList       图片模板变量
     * @param posterVariableList 海报模板变量
     * @return 合并后的海报模板变量
     */
    public static List<VariableItemDTO> mergeVariables(List<VariableItemDTO> variableList, List<VariableItemDTO> posterVariableList) {
        Map<String, VariableItemDTO> variableMap = CollectionUtil.emptyIfNull(variableList).stream().collect(Collectors.toMap(VariableItemDTO::getField, Function.identity()));
        for (VariableItemDTO variableItem : posterVariableList) {
            if (!IMAGE.equalsIgnoreCase(variableItem.getStyle()) && variableMap.containsKey(variableItem.getField())) {
                VariableItemDTO variable = variableMap.get(variableItem.getField());
                if (Objects.nonNull(variable.getValue())) {
                    variableItem.setValue(variable.getValue());
                }
            }
        }
        return posterVariableList;
    }

    /**
     * 转换成执行参数
     *
     * @param template     图片模板
     * @param useImageList 使用的图片
     * @return 执行参数
     */
    private static List<VariableItemDTO> transformParams(CreativeImageTemplateDTO template, List<String> useImageList) {
        List<VariableItemDTO> params = Lists.newArrayList();
        // 图片集合，用于替换图片。
        List<String> imageList = Lists.newArrayList();
        List<VariableItemDTO> variableItemList = CollectionUtil.emptyIfNull(template.getVariables());
        List<VariableItemDTO> imageVariableItemList = imageStyleVariableList(variableItemList);
        for (VariableItemDTO variableItem : variableItemList) {
            VariableItemDTO item = SerializationUtils.clone(variableItem);
            if (IMAGE.equalsIgnoreCase(item.getStyle())) {
                item.setValue(randomImage(imageList, useImageList, imageVariableItemList.size()));
            } else {
                if (Objects.nonNull(variableItem.getValue())) {
                    item.setValue(variableItem.getValue());
                }
            }
            params.add(item);
        }
        return params;
    }

    /**
     * 转换成执行参数
     *
     * @param imageRequest 图片执行参数
     * @param useImageList 使用的图片
     * @param copyWriting  文案的内容
     * @return 执行参数
     */
    @NotNull
    private static Map<String, Object> transformParams(CreativePlanImageExecuteDTO imageRequest, List<String> useImageList, CopyWritingContentDTO copyWriting, Boolean force) {
        Map<String, Object> params = Maps.newHashMap();
        // 图片集合，用于替换图片。
        List<String> imageList = Lists.newArrayList();
        List<VariableItemDTO> variableItemList = CollectionUtil.emptyIfNull(imageRequest.getParams());
        List<VariableItemDTO> imageVariableItemList = imageStyleVariableList(variableItemList);
        for (VariableItemDTO variableItem : variableItemList) {
            if (force && IMAGE.equalsIgnoreCase(variableItem.getStyle())) {
                // 如果变量图片数量大于使用的图片数量，说明图片不够用，随机获取图片，但是可能会重复。
                params.put(variableItem.getField(), randomImage(imageList, useImageList, imageVariableItemList.size()));
            } else {
                if (Objects.isNull(variableItem.getValue())) {
                    // 只有主图才会替换标题和副标题
                    if (imageRequest.getIsMain()) {
                        if (TITLE.equalsIgnoreCase(variableItem.getField())) {
                            params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgTitle()).orElse(StringUtils.EMPTY));
                        } else if (SUB_TITLE.equalsIgnoreCase(variableItem.getField())) {
                            params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgSubTitle()).orElse(StringUtils.EMPTY));
                        } else {
                            params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
                        }
                    } else {
                        params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
                    }
                } else {
                    params.put(variableItem.getField(), variableItem.getValue());
                }
            }
        }
        return params;
    }

    /**
     * 获取图片类型变量
     *
     * @param variableItemList 变量列表
     * @return 图片类型变量
     */
    public static List<VariableItemDTO> imageStyleVariableList(List<VariableItemDTO> variableItemList) {
        return CollectionUtil.emptyIfNull(variableItemList).stream().filter(item -> IMAGE.equalsIgnoreCase(item.getStyle())).collect(Collectors.toList());
    }

    /**
     * 获取非图片类型变量
     *
     * @param variableItemList 变量列表
     * @return 非图片类型变量
     */
    public static List<VariableItemDTO> otherStyleVariableList(List<VariableItemDTO> variableItemList) {
        return CollectionUtil.emptyIfNull(variableItemList).stream().filter(item -> !IMAGE.equalsIgnoreCase(item.getStyle())).collect(Collectors.toList());
    }

    /**
     * 打散图片素材列表
     *
     * @param imageUrlList 图片素材列表
     * @param total        任务数量
     * @return 打散后的图片素材列表
     */
    public static List<String> disperseImageUrlList(List<String> imageUrlList, Integer total) {
        List<String> disperseImageUrlList = SerializationUtils.clone((ArrayList<String>) imageUrlList);
        Collections.shuffle(disperseImageUrlList);
        // 如果图片素材数量大于等于任务数量，直接返回打撒后的图片素材列表
        if (imageUrlList.size() >= total) {
            return disperseImageUrlList;
        }
        // 如果图片素材数量小于任务数量，需要循环使用图片素材
        List<String> dilatationDisperseImageUrlList = Lists.newArrayList();
        for (int i = 0; i < total; i++) {
            dilatationDisperseImageUrlList.add(disperseImageUrlList.get(i % disperseImageUrlList.size()));
        }
        return dilatationDisperseImageUrlList;
    }

    /**
     * 随机图片,递归保证图片不重复
     *
     * @param imageList    图片集合
     * @param useImageList 使用的图片
     * @return 随机图片
     */
    public static String randomImage(List<String> imageList, List<String> useImageList, Integer imageTypeNumber) {
        if (CollectionUtil.isEmpty(useImageList)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PLAN_UPLOAD_IMAGE_EMPTY);
        }
        // 如果图片类型数量大于使用的图片数量，说明图片不够用，随机获取图片，但是可能会重复。
        if (imageTypeNumber > useImageList.size()) {
            return useImageList.get(RandomUtil.randomInt(useImageList.size()));
        }
        // 如果图片类型数量小于使用的图片数量，说明图片够用，随机获取图片，但是不重复。
        String image = useImageList.get(RandomUtil.randomInt(useImageList.size()));
        // 如果图片不在图片集合中，说明图片没有被使用过。记录图片并返回
        if (!imageList.contains(image)) {
            imageList.add(image);
            return image;
        }
        // 如果图片在图片集合中，说明图片已经被使用过，递归获取
        return randomImage(imageList, useImageList, imageTypeNumber);
    }

    /**
     * 获取文本变量
     *
     * @param field 字段
     * @param label 值
     * @return 文本变量
     */
    public static VariableItemDTO ofImageVariable(String field, String label, Integer order) {
        VariableItemDTO variableItem = new VariableItemDTO();
        variableItem.setField(field);
        variableItem.setLabel(label);
        variableItem.setDescription(label);
        variableItem.setOrder(order);
        variableItem.setType(IMAGE);
        variableItem.setStyle(IMAGE);
        variableItem.setGroup(AppVariableGroupEnum.PARAMS.name());
        variableItem.setIsPoint(Boolean.TRUE);
        variableItem.setIsShow(Boolean.FALSE);
        variableItem.setOptions(Lists.newArrayList());
        return variableItem;
    }
}
