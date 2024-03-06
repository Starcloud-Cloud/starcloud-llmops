package com.starcloud.ops.business.app.enums.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.FieldDefine;
import com.starcloud.ops.business.app.api.xhs.material.FieldDefinitionDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.BookListCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.PersonaCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.PictureCreativeMaterialDTO;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.MATERIAL_TYPE_NOT_EXIST;

@Getter
public enum MaterialTypeEnum {
    PICTURE("picture", "基础仿写-图片", PictureCreativeMaterialDTO.class),
    BOOK_LIST("bookList", "书单", BookListCreativeMaterialDTO.class),
    PERSONA("persona", "人设", PersonaCreativeMaterialDTO.class);

    private String typeCode;

    private String desc;

    private Class<? extends AbstractBaseCreativeMaterialDTO> aClass;

    private static final Map<String, MaterialTypeEnum> enumMap = Arrays.stream(MaterialTypeEnum.values())
            .collect(Collectors.toMap(MaterialTypeEnum::getTypeCode, Function.identity()));

    MaterialTypeEnum(String typeCode, String desc, Class<? extends AbstractBaseCreativeMaterialDTO> aClass) {
        this.typeCode = typeCode;
        this.desc = desc;
        this.aClass = aClass;
    }

    public static MaterialTypeEnum of(String typeCode) {
        MaterialTypeEnum typeEnum = enumMap.get(typeCode);
        if (Objects.isNull(typeEnum)) {
            throw exception(MATERIAL_TYPE_NOT_EXIST);
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
        Field[] fields = typeEnum.getAClass().getDeclaredFields();
        List<FieldDefinitionDTO> result = new ArrayList<>();
        for (Field field : fields) {
            FieldDefine annotation = field.getAnnotation(FieldDefine.class);
            if (!Objects.isNull(annotation)) {
                FieldDefinitionDTO definitionDTO = new FieldDefinitionDTO();
                definitionDTO.setFieldName(field.getName());
                definitionDTO.setType(annotation.type().getTypeCode());
                definitionDTO.setDesc(annotation.desc());
                result.add(definitionDTO);
            }
        }
        return result;
    }

}
