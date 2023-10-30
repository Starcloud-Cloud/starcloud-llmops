package com.starcloud.ops.business.listing.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Listing 生成类型枚举
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-26
 */
@Getter
public enum ListingGenerateTypeEnum implements IEnumable<Integer> {

    /**
     * Listing标题生成
     */
    TITLE(1, "Listing标题生成", Arrays.asList("Listing", "Title")),

    /**
     * Listing五点描述生成
     */
    BULLET_POINT(2, "Listing五点描述生成", Arrays.asList("Listing", "BulletPoint")),

    /**
     * Listing产品描述生成
     */
    PRODUCT_DESCRIPTION(3, "Listing产品描述生成", Arrays.asList("Listing", "ProductDescription"));

    /**
     * 枚举值
     */
    private final Integer code;

    /**
     * 枚举描述
     */
    private final String label;

    /**
     * 枚举标签
     */
    private final List<String> tags;

    /**
     * 构造方法
     *
     * @param code  枚举值
     * @param label 枚举描述
     * @param tags  枚举标签
     */
    ListingGenerateTypeEnum(Integer code, String label, List<String> tags) {
        this.code = code;
        this.label = label;
        this.tags = tags;
    }

}
