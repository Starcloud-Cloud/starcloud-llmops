package com.starcloud.ops.business.app.enums.image.pixabay;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Getter
public enum PixabayColorEnum implements IEnumable<Integer> {

    GRAYSCALE("灰色","grayscale"),
    TRANSPARENT("透明","transparent"),
    RED("红色","red"),
    ORANGE("橙","orange"),
    YELLOW("黄","yellow"),
    GREEN("绿","green"),
    TURQUOISE("绿松石","turquoise"),
    BLUE("蓝色","blue"),
    LILAC("淡紫色","lilac"),
    PINK("粉红色","pink"),
    WHITE("白色","white"),
    GRAY("灰色","gray"),
    BLACK("黑","black"),
    BROWN("棕色","brown"),

    ;


    /**
     * 标签
     */
    private final String chineseName;

    /**
     * 标签英文
     */
    private final String code;

    PixabayColorEnum(String chineseName, String code) {
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
