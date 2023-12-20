package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
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
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ParagraphDTO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppVariableGroupEnum;
import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String PARAGRAPH_ONE_TITLE = "PARAGRAPH_ONE_TITLE";
    private static final String PARAGRAPH_ONE_CONTENT = "PARAGRAPH_ONE_CONTENT";
    private static final String PARAGRAPH_TWO_TITLE = "PARAGRAPH_TWO_TITLE";
    private static final String PARAGRAPH_TWO_CONTENT = "PARAGRAPH_TWO_CONTENT";
    private static final String PARAGRAPH_THREE_TITLE = "PARAGRAPH_THREE_TITLE";
    private static final String PARAGRAPH_THREE_CONTENT = "PARAGRAPH_THREE_CONTENT";
    private static final String PARAGRAPH_FOUR_TITLE = "PARAGRAPH_FOUR_TITLE";
    private static final String PARAGRAPH_FOUR_CONTENT = "PARAGRAPH_FOUR_CONTENT";

    private static final List<String> PARAGRAPH_TITLE = Arrays.asList(PARAGRAPH_ONE_TITLE, PARAGRAPH_TWO_TITLE, PARAGRAPH_THREE_TITLE, PARAGRAPH_FOUR_TITLE);
    private static final List<String> PARAGRAPH_CONTENT = Arrays.asList(PARAGRAPH_ONE_CONTENT, PARAGRAPH_TWO_CONTENT, PARAGRAPH_THREE_CONTENT, PARAGRAPH_FOUR_CONTENT);

    /**
     * 转换成执行参数
     *
     * @param content     图片的内容
     * @param copyWriting 文案的内容
     * @return 执行参数
     */
    public static XhsImageStyleExecuteRequest getImageStyleExecuteRequest(CreativeContentDO content,
                                                                          CopyWritingContentDTO copyWriting,
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

        // 干货图文生成
        List<ParagraphDTO> paragraphList = Lists.newArrayList();
        if (CreativeSchemeModeEnum.PRACTICAL_IMAGE_TEXT.name().equals(executeParams.getSchemeMode())) {
            paragraphList = CollectionUtil.emptyIfNull(copyWriting.getParagraphList());
            if (paragraphList.size() != executeParams.getParagraphCount()) {
                throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.PARAGRAPH_SIZE_NOT_EQUAL);
            }
        }

        for (CreativePlanImageExecuteDTO imageRequest : imageRequests) {
            XhsImageExecuteRequest request = new XhsImageExecuteRequest();
            request.setId(imageRequest.getId());
            request.setName(imageRequest.getName());
            request.setIndex(imageRequest.getIndex());
            request.setIsMain(imageRequest.getIsMain());
            if (CreativeSchemeModeEnum.RANDOM_IMAGE_TEXT.name().equals(executeParams.getSchemeMode())) {
                // 随机图文生成
                request.setParams(transformParams(imageRequest, useImageList, copyWriting, force));
            } else {
                Map<String, Object> params = Maps.newHashMap();
                // 图片集合，用于替换图片。
                List<String> imageList = Lists.newArrayList();
                List<VariableItemDTO> variableItemList = CollectionUtil.emptyIfNull(imageRequest.getParams());
                List<VariableItemDTO> imageVariableItemList = imageTypeVariableList(variableItemList);

                for (VariableItemDTO variableItem : variableItemList) {
                    if (force && IMAGE.equalsIgnoreCase(variableItem.getType())) {
                        // 如果变量图片数量大于使用的图片数量，说明图片不够用，随机获取图片，但是可能会重复。
                        params.put(variableItem.getField(), randomImage(imageList, useImageList, imageVariableItemList.size()));
                    } else {
                        if (Objects.isNull(variableItem.getValue()) || ((variableItem.getValue() instanceof String) && StringUtils.isBlank((String) variableItem.getValue()))) {
                            // 只有主图才会替换标题和副标题
                            if (imageRequest.getIsMain()) {
                                if (TITLE.equalsIgnoreCase(variableItem.getField())) {
                                    params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgTitle()).orElse(StringUtils.EMPTY));
                                } else if (SUB_TITLE.equalsIgnoreCase(variableItem.getField())) {
                                    params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgSubTitle()).orElse(StringUtils.EMPTY));
                                } else if (PARAGRAPH_TITLE.contains(variableItem.getField())) {
                                    paragraphTitle(params, variableItem, paragraphList);
                                } else if (PARAGRAPH_CONTENT.contains(variableItem.getField())) {
                                    paragraphContent(params, variableItem, paragraphList);
                                } else {
                                    params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
                                }
                            } else {
                                if (PARAGRAPH_TITLE.contains(variableItem.getField())) {
                                    paragraphTitle(params, variableItem, paragraphList);
                                } else if (PARAGRAPH_CONTENT.contains(variableItem.getField())) {
                                    paragraphContent(params, variableItem, paragraphList);
                                } else {
                                    params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
                                }
                            }
                        } else {
                            params.put(variableItem.getField(), Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
                        }
                    }
                }
                request.setParams(params);
            }
            imageExecuteRequests.add(request);
        }
        executeRequest.setId(imageStyleExecuteRequest.getId());
        executeRequest.setName(imageStyleExecuteRequest.getName());
        executeRequest.setImageRequests(imageExecuteRequests);
        return executeRequest;
    }

    /**
     * 处理段落标题
     *
     * @param params        参数
     * @param variableItem  变量
     * @param paragraphList 段落
     */
    private static void paragraphTitle(Map<String, Object> params, VariableItemDTO variableItem, List<ParagraphDTO> paragraphList) {
        if (CollectionUtil.isEmpty(paragraphList)) {
            params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
        }
        for (ParagraphDTO paragraph : paragraphList) {
            if (!paragraph.getIsUseTitle()) {
                String title = Optional.ofNullable(paragraph.getParagraphTitle()).orElse(StringUtils.EMPTY);
                int count = Optional.ofNullable(variableItem.getCount()).orElse(0);
                if (title.length() > count - 3) {
                    // 超出部分用 ... 代替
                    title = StrUtil.maxLength(title, count - 3);
                }
                params.put(variableItem.getField(), title);
                paragraph.setIsUseTitle(true);
                return;
            }
        }
        params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
    }

    /**
     * 处理段落内容
     *
     * @param params        参数
     * @param variableItem  变量
     * @param paragraphList 段落
     */
    private static void paragraphContent(Map<String, Object> params, VariableItemDTO variableItem, List<ParagraphDTO> paragraphList) {
        if (CollectionUtil.isEmpty(paragraphList)) {
            params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
        }
        for (ParagraphDTO paragraph : paragraphList) {
            if (!paragraph.getIsUseContent()) {
                String content = Optional.ofNullable(paragraph.getParagraphContent()).orElse(StringUtils.EMPTY);
                int count = Optional.ofNullable(variableItem.getCount()).orElse(0);
                if (content.length() > count - 3) {
                    // 超出部分用 ... 代替
                    content = StrUtil.maxLength(content, count - 3);
                }
                params.put(variableItem.getField(), content);
                paragraph.setIsUseContent(true);
                return;
            }
        }
        params.put(variableItem.getField(), Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY));
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
     * 获取小红书批量图片执行参数
     *
     * @param style 图片模板列表
     * @return 图片执行参数
     */
    public static CreativePlanImageStyleExecuteDTO getCreativeImageStyleExecute(CreativeImageStyleDTO style, List<String> useImageList,
                                                                                Integer paragraphCount, Map<String, CreativeImageTemplateDTO> posterMap) {
        // 图片参数信息
        List<CreativePlanImageExecuteDTO> imageExecuteRequestList = Lists.newArrayList();
        List<CreativeImageTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        // 图片参数配置的总段落数
        List<Integer> paragraphParamCountList = new ArrayList<>();
        for (int i = 0; i < templateList.size(); i++) {
            CreativeImageTemplateDTO posterTemplate = mergeTemplate(templateList.get(i), posterMap);
            CreativePlanImageExecuteDTO imageExecuteRequest = new CreativePlanImageExecuteDTO();
            imageExecuteRequest.setIndex(i + 1);
            imageExecuteRequest.setIsMain(i == 0);
            imageExecuteRequest.setId(posterTemplate.getId());
            imageExecuteRequest.setName(posterTemplate.getName());
            List<VariableItemDTO> params = transformParams(posterTemplate, useImageList);
            int size = (int) params.stream().filter(item -> PARAGRAPH_TITLE.contains(item.getField())).count();
            paragraphParamCountList.add(size);
            imageExecuteRequest.setParams(params);
            imageExecuteRequestList.add(imageExecuteRequest);
        }

        List<CreativePlanImageExecuteDTO> imageRequestList = Lists.newArrayList();
        // paragraphParamCountList 依次相加 stream 方式
        int total = paragraphParamCountList.stream().mapToInt(Integer::intValue).sum();
        if (total == paragraphCount) {
            imageRequestList = imageExecuteRequestList;
        } else if (total > paragraphCount) {
            // 超过段落数，截取
            int sumCount = 0;
            for (int i = 0; i < paragraphParamCountList.size(); i++) {
                if (i == paragraphParamCountList.size() - 1 && paragraphParamCountList.get(i) == 0) {
                    imageRequestList.add(imageExecuteRequestList.get(i));
                }
                if (sumCount > paragraphCount) {
                    continue;
                }
                sumCount += paragraphParamCountList.get(i);
                imageRequestList.add(imageExecuteRequestList.get(i));
            }
        } else {
            // 少于段落数，补充，获取作为复制的基数索引
            imageRequestList.addAll(imageExecuteRequestList);
            // 获取需要补充的数量
            int diff = paragraphCount - total;
            // 获取需要复制的元素的索引。
            int index = getNextNonZeroIndex(paragraphParamCountList);
            // 获取需要复制的元素
            CreativePlanImageExecuteDTO imageExecuteRequest = imageExecuteRequestList.get(index);
            // 复制元素
            while (diff > 0) {
                CreativePlanImageExecuteDTO copyImageExecuteRequest = SerializationUtils.clone(imageExecuteRequest);
                // 在 index 索引后面添加复制的元素
                imageRequestList.add(index + 1, copyImageExecuteRequest);
                // diff 扣除复制的元素的数量
                diff -= paragraphParamCountList.get(index);
            }
        }

        // 图片风格执行参数
        CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = new CreativePlanImageStyleExecuteDTO();
        imageStyleExecuteRequest.setId(style.getId());
        imageStyleExecuteRequest.setName(style.getName());
        imageStyleExecuteRequest.setImageRequests(imageRequestList);
        return imageStyleExecuteRequest;
    }

    private static int getNextNonZeroIndex(List<Integer> params) {
        for (int i = 1; i < params.size(); i++) {
            if (params.get(i) > 0) {
                return i;
            }
        }
        throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_NOT_SUPPORTED);
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
            if (!IMAGE.equalsIgnoreCase(variableItem.getType()) && variableMap.containsKey(variableItem.getField())) {
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
        List<VariableItemDTO> imageVariableItemList = imageTypeVariableList(variableItemList);
        for (VariableItemDTO variableItem : variableItemList) {
            VariableItemDTO item = SerializationUtils.clone(variableItem);
            if (IMAGE.equalsIgnoreCase(item.getType()) && CollectionUtil.isNotEmpty(useImageList)) {
                item.setValue(randomImage(imageList, useImageList, imageVariableItemList.size()));
            } else {
                item.setValue(Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
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
        List<VariableItemDTO> imageVariableItemList = imageTypeVariableList(variableItemList);
        for (VariableItemDTO variableItem : variableItemList) {
            if (force && IMAGE.equalsIgnoreCase(variableItem.getType()) && CollectionUtil.isNotEmpty(useImageList)) {
                // 如果变量图片数量大于使用的图片数量，说明图片不够用，随机获取图片，但是可能会重复。
                params.put(variableItem.getField(), randomImage(imageList, useImageList, imageVariableItemList.size()));
            } else {
                if (Objects.isNull(variableItem.getValue()) ||
                        ((variableItem.getValue() instanceof String) && StringUtils.isBlank((String) variableItem.getValue()))) {
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
                    params.put(variableItem.getField(), Optional.ofNullable(variableItem.getValue()).orElse(StringUtils.EMPTY));
                }
            }
        }
        return params;
    }

    public static String handlerParagraphDemand(CreativeContentDO business) {
        StringBuilder builder = new StringBuilder();
        // 获取执行参数
        CreativePlanExecuteDTO executeParams = CreativeContentConvert.INSTANCE.toExecuteParams(business.getExecuteParams());
        CreativePlanImageStyleExecuteDTO imageStyleExecuteRequest = executeParams.getImageStyleExecuteRequest();
        List<CreativePlanImageExecuteDTO> imageRequests = imageStyleExecuteRequest.getImageRequests();
        int i = 1;
        for (CreativePlanImageExecuteDTO imageRequest : imageRequests) {
            List<VariableItemDTO> params = imageRequest.getParams();
            for (VariableItemDTO param : params) {
                if ("TEXT".equalsIgnoreCase(param.getType())) {
                    if (PARAGRAPH_TITLE.contains(param.getField())) {
                        builder.append("第 ").append(i).append(" 个段落标题，需要满足：").append(param.getCount()).append("左右的字符数量。").append("\n");
                    }
                    if (PARAGRAPH_CONTENT.contains(param.getField())) {
                        builder.append("第 ").append(i).append(" 个段落内容，需要满足：").append(param.getCount()).append("左右的字符数量。").append("\n");
                    }
                    i = i + 1;
                }
            }

        }

        return builder.toString();
    }

    /**
     * 获取图片类型变量
     *
     * @param variableItemList 变量列表
     * @return 图片类型变量
     */
    public static List<VariableItemDTO> imageTypeVariableList(List<VariableItemDTO> variableItemList) {
        return CollectionUtil.emptyIfNull(variableItemList).stream().filter(item -> IMAGE.equalsIgnoreCase(item.getType())).collect(Collectors.toList());
    }

    /**
     * 获取非图片类型变量
     *
     * @param variableItemList 变量列表
     * @return 非图片类型变量
     */
    public static List<VariableItemDTO> otherTypeVariableList(List<VariableItemDTO> variableItemList) {
        return CollectionUtil.emptyIfNull(variableItemList).stream().filter(item -> !IMAGE.equalsIgnoreCase(item.getType())).collect(Collectors.toList());
    }

    /**
     * 打散图片素材列表
     *
     * @param imageUrlList 图片素材列表
     * @param total        任务数量
     * @return 打散后的图片素材列表
     */
    public static List<String> disperseImageUrlList(List<String> imageUrlList, Integer total) {
        if (CollectionUtil.isEmpty(imageUrlList)) {
            return Lists.newArrayList();
        }
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
