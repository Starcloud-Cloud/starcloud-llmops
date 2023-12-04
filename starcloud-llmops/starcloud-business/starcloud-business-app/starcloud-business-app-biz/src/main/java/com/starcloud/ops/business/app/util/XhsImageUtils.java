package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanImageExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanImageStyleExecuteDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.execute.XhsImageStyleExecuteRequest;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.XhsCreativeContentDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
public class XhsImageUtils {

    /**
     * 转换成执行参数
     *
     * @param content     图片的内容
     * @param copyWriting 文案的内容
     * @return 执行参数
     */
    public static XhsImageStyleExecuteRequest transformExecuteImageStyle(XhsCreativeContentDO content, CopyWritingContentDTO copyWriting, Boolean force) {
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
            request.setImageTemplate(imageRequest.getImageTemplate());
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
    public static CreativePlanImageStyleExecuteDTO getImageStyleExecuteRequest(XhsImageStyleDTO style, List<String> useImageList) {
        // 图片参数信息
        List<CreativePlanImageExecuteDTO> imageExecuteRequestList = Lists.newArrayList();
        List<XhsImageTemplateDTO> templateList = CollectionUtil.emptyIfNull(style.getTemplateList());
        for (int i = 0; i < templateList.size(); i++) {
            XhsImageTemplateDTO template = templateList.get(i);
            CreativePlanImageExecuteDTO imageExecuteRequest = new CreativePlanImageExecuteDTO();
            imageExecuteRequest.setIndex(i + 1);
            imageExecuteRequest.setIsMain(i == 0);
            imageExecuteRequest.setImageTemplate(template.getId());
            imageExecuteRequest.setParams(transformParams(template, useImageList));
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
     * 转换成执行参数
     *
     * @param template     图片模板
     * @param useImageList 使用的图片
     * @return 执行参数
     */
    private static List<VariableItemDTO> transformParams(XhsImageTemplateDTO template, List<String> useImageList) {
        List<VariableItemDTO> params = Lists.newArrayList();
        // 图片集合，用于替换图片。
        List<VariableItemDTO> variableItemList = CollectionUtil.emptyIfNull(template.getVariables());
        for (VariableItemDTO variableItem : variableItemList) {
            VariableItemDTO item = SerializationUtils.clone(variableItem);
            if (!"IMAGE".equalsIgnoreCase(item.getStyle())) {
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
        List<VariableItemDTO> imageVariableItemList = CollectionUtil.emptyIfNull(variableItemList.stream().filter(item -> "IMAGE".equalsIgnoreCase(item.getStyle())).collect(Collectors.toList()));
        for (VariableItemDTO variableItem : variableItemList) {
            if (force && "IMAGE".equalsIgnoreCase(variableItem.getStyle())) {
                // 如果变量图片数量大于使用的图片数量，说明图片不够用，随机获取图片，但是可能会重复。
                if (imageVariableItemList.size() > useImageList.size()) {
                    params.put(variableItem.getField(), useImageList.get(RandomUtil.randomInt(useImageList.size())));
                } else {
                    // 如果变量图片数量小于使用的图片数量，说明图片够用，随机获取图片，但是不重复。
                    params.put(variableItem.getField(), randomImageList(imageList, useImageList));
                }
            } else {
                if (Objects.isNull(variableItem.getValue())) {
                    // 只有主图才会替换标题和副标题
                    if (imageRequest.getIsMain()) {
                        if ("TITLE".equalsIgnoreCase(variableItem.getField())) {
                            params.put(variableItem.getField(), Optional.ofNullable(copyWriting.getImgTitle()).orElse(StringUtils.EMPTY));
                        } else if ("SUB_TITLE".equalsIgnoreCase(variableItem.getField())) {
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
     * 随机图片,递归保证图片不重复
     *
     * @param imageList    图片集合
     * @param useImageList 使用的图片
     * @return 随机图片
     */
    public static String randomImageList(List<String> imageList, List<String> useImageList) {
        int randomInt = RandomUtil.randomInt(useImageList.size());
        String image = useImageList.get(randomInt);
        // 如果图片不在图片集合中，说明图片没有被使用过。记录图片并返回
        if (!imageList.contains(image)) {
            imageList.add(image);
            return image;
        }
        // 如果图片在图片集合中，说明图片已经被使用过，递归获取
        return randomImageList(imageList, useImageList);
    }
}
