package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_FIELD_NOT_VALID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BookListCreativeMaterialDTO.class, name = "bookList"),
        @JsonSubTypes.Type(value = ContractCreativeMaterialDTO.class, name = "contract"),
        @JsonSubTypes.Type(value = OrdinaryNoteContentMaterialDTO.class, name = "noteContent"),
        @JsonSubTypes.Type(value = OrdinaryNoteMaterialDTO.class, name = "note"),
        @JsonSubTypes.Type(value = OrdinaryNoteTitleMaterialDTO.class, name = "noteTitle"),
        @JsonSubTypes.Type(value = PersonaCreativeMaterialDTO.class, name = "persona"),
        @JsonSubTypes.Type(value = PictureCreativeMaterialDTO.class, name = "picture"),
        @JsonSubTypes.Type(value = PositiveQuotationCreativeMaterialDTO.class, name = "quotation"),
        @JsonSubTypes.Type(value = SnackRecipeCreativeMaterialDTO.class, name = "snack"),
        @JsonSubTypes.Type(value = TravelGuideCreativeMaterialDTO.class, name = "travel")
})
@Schema(description = "素材内容")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractCreativeMaterialDTO implements java.io.Serializable {

    private static final long serialVersionUID = 3089429508471148974L;
    /**
     * 素材类型 {@link BookListCreativeMaterialDTO#getType()}
     */
    @Schema(description = "素材类型")
    @JsonPropertyDescription("素材类型")
    private String type;

    /**
     * 导入素材: 摘要内容 用于筛选
     * 参考素材: 获取参考内容
     *
     * @return
     */
    public abstract String generateContent();

    /**
     * 校验参数
     * 注解必填字段校验
     */
    public void valid() {
        for (Field field : this.getClass().getDeclaredFields()) {
            FieldDefine fieldDefine = field.getDeclaredAnnotation(FieldDefine.class);
            if (Objects.isNull(fieldDefine)) {
                continue;
            }
            if (fieldDefine.required()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value instanceof String && StringUtils.isBlank((String) value)) {
                        throw exception(MATERIAL_FIELD_NOT_VALID, fieldDefine.desc() + "不能为空");
                    }

                    if (Objects.isNull(value)) {
                        throw exception(MATERIAL_FIELD_NOT_VALID, fieldDefine.desc() + "不能为空");
                    }
                } catch (IllegalAccessException e) {
                    throw exception(MATERIAL_FIELD_NOT_VALID, fieldDefine.desc() + "校验错误：" + e.getMessage());
                }
            }
        }
    }

    ;

    /**
     * 去除冗余字段
     */
    public void clean() {
        this.type = null;
    }
}
