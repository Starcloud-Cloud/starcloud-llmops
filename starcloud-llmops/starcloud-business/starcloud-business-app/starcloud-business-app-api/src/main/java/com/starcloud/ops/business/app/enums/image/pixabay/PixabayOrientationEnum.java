package com.starcloud.ops.business.app.enums.image.pixabay;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Getter
public enum PixabayOrientationEnum implements IEnumable<Integer> {

    HORIZONTAL("水平","horizontal"),
    VERTICAL("垂直","vertical"),
    ALL("全部","all"),

    ;


    /**
     * 标签
     */
    private final String chineseName;

    /**
     * 标签英文
     */
    private final String code;

    PixabayOrientationEnum(String chineseName, String code) {
        this.chineseName = chineseName;
        this.code = code;
    }

    /**
     * 获取枚举编码
     *
     * @return 枚举值
     */
    @Override
    public Integer getCode() {
        return 0;
    }

    /**
     * 获取枚举标签
     *
     * @return 枚举标签
     */
    @Override
    public String getLabel() {
        return "";
    }

    /**
     * 获取描述 <br>
     *
     * @return 描述
     */
    @Override
    public String getDescription() {
        return IEnumable.super.getDescription();
    }
}
