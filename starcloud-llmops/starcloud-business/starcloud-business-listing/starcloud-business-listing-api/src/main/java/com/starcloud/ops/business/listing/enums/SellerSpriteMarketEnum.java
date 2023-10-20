package com.starcloud.ops.business.listing.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 卖家精灵站点枚举
 */
@Getter
@AllArgsConstructor
public enum SellerSpriteMarketEnum {
    US(1, "美国站"),
    JP(6, "日本站"),
    UK(3, "英国站"),
    DE(4, "德国站"),
    FR(5, "法国站"),
    IT(35691, "意大利"),
    ES(44551, "西班牙"),
    CA(7, "加拿大"),
    IN(44571, "印度站"),


    ;

    private int code;

    private String name;


    public static SellerSpriteMarketEnum getByCode(Integer code) {
        return ArrayUtil.firstMatch(o -> o.getCode() == code, values());
    }


}
