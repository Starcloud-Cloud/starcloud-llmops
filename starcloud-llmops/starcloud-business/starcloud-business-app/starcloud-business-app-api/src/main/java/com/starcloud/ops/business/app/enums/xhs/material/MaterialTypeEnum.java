package com.starcloud.ops.business.app.enums.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefinitionDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.*;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_TYPE_NOT_EXIST;

@Getter
public enum MaterialTypeEnum implements IEnumable<String> {
    BOOK_LIST("bookList", "书单", BookListCreativeMaterialDTO.class),
    CONTRACT("contract", "合同模板", ContractCreativeMaterialDTO.class),
    NOTE("note", "普通笔记", OrdinaryNoteMaterialDTO.class),
    NOTE_TITLE("noteTitle", "普通笔记标题", OrdinaryNoteTitleMaterialDTO.class),
    NOTE_CONTENT("noteContent", "普通笔记内容", OrdinaryNoteContentMaterialDTO.class),
    PERSONA("persona", "人设", PersonaCreativeMaterialDTO.class),
    PICTURE("picture", "图片", PictureCreativeMaterialDTO.class),
    QUOTATION("quotation", "语录号", PositiveQuotationCreativeMaterialDTO.class),
    SNACK("snack", "小吃配方", SnackRecipeCreativeMaterialDTO.class);

    private final String typeCode;

    private final String desc;

    private final Class<? extends AbstractCreativeMaterialDTO> aClass;

    public static final Map<String, MaterialTypeEnum> ENUM_MAP = Arrays.stream(MaterialTypeEnum.values())
            .collect(Collectors.toMap(MaterialTypeEnum::getTypeCode, Function.identity()));

    // 参考素材类型
    private static final List<MaterialTypeEnum> REFER_MATERIALS = Arrays.asList(NOTE, NOTE_TITLE, NOTE_CONTENT);

    MaterialTypeEnum(String typeCode, String desc, Class<? extends AbstractCreativeMaterialDTO> aClass) {
        this.typeCode = typeCode;
        this.desc = desc;
        this.aClass = aClass;
    }

    public static MaterialTypeEnum of(String typeCode) {
        MaterialTypeEnum typeEnum = ENUM_MAP.get(typeCode);
        if (Objects.isNull(typeEnum)) {
            throw exception(MATERIAL_TYPE_NOT_EXIST, typeCode);
        }
        return typeEnum;
    }

    /**
     * 素材数据结构
     *
     * @param typeCode
     * @return
     */
    public static List<FieldDefinitionDTO> fieldDefine(String typeCode) {
        MaterialTypeEnum typeEnum = of(typeCode);
        return typeEnum.fieldDefine();
    }

    public List<FieldDefinitionDTO> fieldDefine() {
        Field[] fields = this.getAClass().getDeclaredFields();
        List<FieldDefinitionDTO> result = new ArrayList<>();
        for (Field field : fields) {
            FieldDefine annotation = field.getAnnotation(FieldDefine.class);
            if (Objects.nonNull(annotation)) {
                FieldDefinitionDTO definitionDTO = new FieldDefinitionDTO();
                definitionDTO.setFieldName(field.getName());
                definitionDTO.setType(annotation.type().getTypeCode());
                definitionDTO.setDesc(annotation.desc());
                definitionDTO.setRequired(annotation.required());
                result.add(definitionDTO);
            }
        }
        return result;
    }

    /**
     * 筛选指定类型的字段
     * @param fieldTypeEnum
     * @return
     */
    public List<Field> filterField(FieldTypeEnum fieldTypeEnum){
        Field[] fields = this.getAClass().getDeclaredFields();
        List<Field> result = new ArrayList<>();
        for (Field field : fields) {
            FieldDefine annotation = field.getAnnotation(FieldDefine.class);
            if (Objects.nonNull(annotation) && fieldTypeEnum.getTypeCode().equals(annotation.type().getTypeCode())) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<Option> allOptions() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(MaterialTypeEnum::ordinal))
                .map(MaterialTypeEnum::option).collect(Collectors.toList());
    }

    public static List<Option> referOptions() {
        return REFER_MATERIALS.stream().sorted(Comparator.comparingInt(MaterialTypeEnum::ordinal))
                .map(MaterialTypeEnum::option).collect(Collectors.toList());
    }

    public Option option() {
        Option option = new Option();
        option.setLabel(desc);
        option.setValue(typeCode);
        return option;
    }

    @Override
    public String getCode() {
        return this.typeCode;
    }

    @Override
    public String getLabel() {
        return this.desc;
    }
}
