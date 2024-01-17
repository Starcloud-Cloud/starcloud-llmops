package com.starcloud.ops.business.app.domain.entity.poster;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.ParagraphDTO;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "海报风格对象 VO")
public class PosterStyleEntity implements Serializable {

    private static final long serialVersionUID = 1093411003287488657L;

    /**
     * 海报风格ID
     */
    @Schema(description = "海报风格ID")
    @NotBlank(message = "海报风格ID不能为空")
    private String id;

    /**
     * 海报风格名称
     */
    @Schema(description = "海报风格名称")
    private String name;

    /**
     * 海报风格描述
     */
    @Schema(description = "海报风格描述")
    private String description;

    /**
     * 海报模板列表
     */
    @Schema(description = "海报模板列表")
    @NotEmpty(message = "海报模板列表不能为空")
    @Valid
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
     *
     * @param paragraphList 段落内容
     */
    public void assemble(String title, List<ParagraphDTO> paragraphList) {
        List<PosterTemplateEntity> templates = new ArrayList<>();
        for (PosterTemplateEntity template : this.templateList) {
            List<VariableItemEntity> variables = new ArrayList<>();
            List<VariableItemEntity> variableList = template.getVariableList();
            for (VariableItemEntity variableItem : variableList) {
                // 只有主图才会替换标题和副标题
                if (template.getIsMain()) {
                    if (Objects.isNull(variableItem.getValue()) || ((variableItem.getValue() instanceof String && StringUtils.isBlank((String) variableItem.getValue())))) {
                        if (CreativeImageUtils.TITLE.equalsIgnoreCase(variableItem.getField())) {
                            // todo 图片主标题
                        } else if (CreativeImageUtils.SUB_TITLE.equalsIgnoreCase(variableItem.getField())) {
                            // todo 图片副标题
                        } else if (CreativeImageUtils.TEXT_TITLE.equalsIgnoreCase(variableItem.getField())) {
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
    private void paragraphTitle(VariableItemEntity variableItem, List<ParagraphDTO> paragraphList) {
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
    private void paragraphContent(VariableItemEntity variableItem, List<ParagraphDTO> paragraphList) {

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

}
