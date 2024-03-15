package com.starcloud.ops.business.app.api.xhs.material.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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
        @JsonSubTypes.Type(value = SnackRecipeCreativeMaterialDTO.class, name = "snack")
})
@Schema(description = "素材内容")
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractBaseCreativeMaterialDTO {

    /**
     * 素材类型 {@link BookListCreativeMaterialDTO#getType()}
     */
    @Schema(description = "素材类型")
    @JsonPropertyDescription("素材类型")
    private String type;

    /**
     * 导入素材: 摘要内容 用于筛选
     * 参考素材: 获取参考内容
     * @return
     */
    public abstract String generateContent();

    /**
     * 校验参数
     */
    public abstract void valid();

    /**
     * 去除冗余字段
     */
    public void clean() {
        this.type = null;
    }
}
