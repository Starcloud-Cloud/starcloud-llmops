package com.starcloud.ops.business.app.enums.xhs.material;

import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.PictureCreativeMaterialDTO;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum MaterialTypeEnum {
    picture("picture", "基础仿写-图片", PictureCreativeMaterialDTO.class),
    BOOK_LIST("bookList", "书单",null)
    ;

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
//
        }
        return typeEnum;
    }

}
