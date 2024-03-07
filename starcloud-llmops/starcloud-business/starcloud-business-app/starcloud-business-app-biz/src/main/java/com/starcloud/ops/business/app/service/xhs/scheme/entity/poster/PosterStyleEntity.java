package com.starcloud.ops.business.app.service.xhs.scheme.entity.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ParagraphDTO;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.poster.PosterModeEnum;
import com.starcloud.ops.business.app.util.CreativeImageUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "PosterStyleEntity", description = "创作中心图片风格对象")
public class PosterStyleEntity implements java.io.Serializable {

    private static final long serialVersionUID = 3693634357817132472L;

    /**
     * 风格id
     */
    @Schema(description = "风格ID")
    @NotBlank(message = "风格ID不能为空！")
    private String id;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    @NotBlank(message = "风格名称不能为空！")
    private String name;

    /**
     * 图片数量
     */
    @Schema(description = "图片数量")
    private Integer imageCount;

    /**
     * 最大图片数量
     */
    @Schema(description = "最大图片数量")
    private Integer maxImageCount;

    /**
     * 图片素材列表
     */
    @Schema(description = "图片素材列表")
    private List<String> imageMaterialList;

    /**
     * 海报风格描述
     */
    @Schema(description = "海报风格描述")
    private String description;

    /**
     * 模板列表
     */
    @Schema(description = "模板列表")
    @Valid
    @NotEmpty(message = "请选择图片模板！")
    private List<PosterTemplateEntity> templateList;

    /**
     * 校验
     */
    public void validate() {
        if (CollectionUtil.isEmpty(this.templateList)) {
            throw ServiceExceptionUtil.exception(CreativeErrorCodeConstants.POSTER_IMAGE_TEMPLATE_NOT_EXIST);
        }
        for (PosterTemplateEntity template : this.templateList) {
            template.validate();
        }
    }

    /**
     * 组装
     */
    public void assemble() {
        List<PosterTemplateEntity> templates = new ArrayList<>();
        Map<String, List<String>> materialMap = splitImageListToMap(this.templateList, this.imageMaterialList);

        for (PosterTemplateEntity template : this.templateList) {

            // 获取模板对应的图片列表
            List<String> imageList = materialMap.get(template.getId());
            if (CollectionUtil.isEmpty(imageList)) {
                continue;
            }

            List<PosterVariableEntity> variables = new ArrayList<>();
            List<PosterVariableEntity> variableList = template.getVariableList();

            // 顺序生成索引，用于顺序生成图片
            int index = 0;
            // 随机生成图片，已经使用的图片列表
            List<String> usedImageList = new ArrayList<>();
            for (PosterVariableEntity variableItem : variableList) {
                if (CreativeConstants.IMAGE.equals(variableItem.getType())) {

                    // 顺序生成
                    if (PosterModeEnum.SEQUENCE.name().equals(template.getMode())) {
                        if (index < imageList.size()) {
                            variableItem.setValue(imageList.get(index));
                            index++;
                        }
                    } else {
                        // 随机生成
                        variableItem.setValue(randomImage(imageList, usedImageList));
                    }
                }
                variables.add(variableItem);
            }
            template.setVariableList(variables);
            templates.add(template);
        }
        this.templateList = templates;
    }

    /**
     * 组装
     *
     * @param paragraphList 段落内容
     */
    public void assemble(String title, List<ParagraphDTO> paragraphList) {
        List<PosterTemplateEntity> templates = new ArrayList<>();
        for (PosterTemplateEntity template : this.templateList) {
            List<PosterVariableEntity> variables = new ArrayList<>();
            List<PosterVariableEntity> variableList = template.getVariableList();
            for (PosterVariableEntity variableItem : variableList) {
                // 只有主图才会替换标题和副标题
                if (template.getIsMain()) {
                    if (Objects.isNull(variableItem.getValue()) || ((variableItem.getValue() instanceof String && StringUtils.isBlank((String) variableItem.getValue())))) {
                        if (CreativeImageUtils.TEXT_TITLE.equalsIgnoreCase(variableItem.getField())) {
                            variableItem.setValue(title);
                        } else if (CreativeImageUtils.PARAGRAPH_TITLE.contains(variableItem.getField())) {
                            paragraphTitle(variableItem, paragraphList);
                        } else if (CreativeImageUtils.PARAGRAPH_CONTENT.contains(variableItem.getField())) {
                            paragraphContent(variableItem, paragraphList);
                        } else {
                            Object value = Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY);
                            variableItem.setValue(value);
                        }
                    }
                } else {
                    if (Objects.isNull(variableItem.getValue()) || ((variableItem.getValue() instanceof String && StringUtils.isBlank((String) variableItem.getValue())))) {
                        if (CreativeImageUtils.PARAGRAPH_TITLE.contains(variableItem.getField())) {
                            paragraphTitle(variableItem, paragraphList);
                        } else if (CreativeImageUtils.PARAGRAPH_CONTENT.contains(variableItem.getField())) {
                            paragraphContent(variableItem, paragraphList);
                        } else {
                            Object value = Optional.ofNullable(variableItem.getDefaultValue()).orElse(StringUtils.EMPTY);
                            variableItem.setValue(value);
                        }
                    }
                }

                variables.add(variableItem);
            }
            template.setVariableList(variables);
            templates.add(template);
        }
        this.templateList = templates;
    }

    /**
     * 段落标题
     *
     * @param variableItem  variableItem
     * @param paragraphList paragraphList
     */
    private void paragraphTitle(PosterVariableEntity variableItem, List<ParagraphDTO> paragraphList) {
        if (CollectionUtil.isEmpty(paragraphList)) {
            Object value = Optional.ofNullable(variableItem.getValue()).orElse(variableItem.getDefaultValue());
            variableItem.setValue(Optional.ofNullable(value).orElse(StringUtils.EMPTY));
        }
        for (ParagraphDTO paragraph : paragraphList) {
            if (!paragraph.getIsUseTitle()) {
                String title = Optional.ofNullable(paragraph.getParagraphTitle()).orElse(StringUtils.EMPTY);
                variableItem.setValue(title);
                paragraph.setIsUseTitle(true);
                return;
            }
        }
        Object value = Optional.ofNullable(variableItem.getValue()).orElse(variableItem.getDefaultValue());
        variableItem.setValue(Optional.ofNullable(value).orElse(StringUtils.EMPTY));
    }

    /**
     * 段落内容
     *
     * @param variableItem  variableItem
     * @param paragraphList paragraphList
     */
    private void paragraphContent(PosterVariableEntity variableItem, List<ParagraphDTO> paragraphList) {
        if (CollectionUtil.isEmpty(paragraphList)) {
            Object value = Optional.ofNullable(variableItem.getValue()).orElse(variableItem.getDefaultValue());
            variableItem.setValue(Optional.ofNullable(value).orElse(StringUtils.EMPTY));
        }
        for (ParagraphDTO paragraph : paragraphList) {
            if (!paragraph.getIsUseContent()) {
                String content = Optional.ofNullable(paragraph.getParagraphContent()).orElse(StringUtils.EMPTY);
                variableItem.setValue(content);
                paragraph.setIsUseContent(true);
                return;
            }
        }
        Object value = Optional.ofNullable(variableItem.getValue()).orElse(variableItem.getDefaultValue());
        variableItem.setValue(Optional.ofNullable(value).orElse(StringUtils.EMPTY));
    }

    /**
     * 固定风格1
     *
     * @return 风格1
     */
    public static PosterStyleEntity ofOne() {
        PosterStyleEntity posterStyle = new PosterStyleEntity();
        posterStyle.setId("STYLE_1");
        posterStyle.setName("风格 1");
        posterStyle.setTemplateList(Collections.singletonList(PosterTemplateEntity.ofMain()));
        return posterStyle;
    }

    /**
     * 为每个图片模板分配图片。
     *
     * @param templateList 模板列表
     * @param imageList    图片列表
     * @return 模板ID和图片列表的映射
     */
    public static Map<String, List<String>> splitImageListToMap(List<PosterTemplateEntity> templateList, List<String> imageList) {
        Map<String, List<String>> resultMap = new HashMap<>();

        int index = 0;
        int imageListSize = imageList.size();

        for (PosterTemplateEntity posterTemplate : templateList) {
            Integer imageNumber = posterTemplate.getImageNumber();
            if (imageNumber == null || imageNumber <= 0) {
                continue;
            }
            int endIndex = Math.min(index + imageNumber, imageListSize);
            List<String> sublist = new ArrayList<>(imageList.subList(index, endIndex));

            resultMap.put(posterTemplate.getId(), sublist);

            index += imageNumber;

        }

        return resultMap;
    }

    /**
     * 随机获取图片,并且不重复
     *
     * @param imageList     图片列表
     * @param usedImageList 已经使用的图片列表
     * @return 图片
     */
    public static String randomImage(List<String> imageList, List<String> usedImageList) {

        int index = RandomUtil.randomInt(imageList.size());
        String image = imageList.get(index);
        if (usedImageList.contains(image)) {
            return randomImage(imageList, usedImageList);
        }
        usedImageList.add(image);
        return image;
    }
}
