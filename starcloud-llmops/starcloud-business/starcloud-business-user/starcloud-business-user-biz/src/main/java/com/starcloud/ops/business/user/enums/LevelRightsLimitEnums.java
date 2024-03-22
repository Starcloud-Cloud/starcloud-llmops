package com.starcloud.ops.business.user.enums;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.function.Function;

/**
 * 用户权益限制枚举
 */
@AllArgsConstructor
@Getter
public enum LevelRightsLimitEnums implements IntArrayValuable {


    LISTING_QUERY("listingQuery", LevelConfigDTO::getListingQuery, "Listing 查询"),
    QUICK_PUBLISH_COUNT("quickPublishCount", LevelConfigDTO::getQuickPublishCount, "一键发布"),
    ;

    /**
     * 描述
     */
    private final String redisKey;

    private final Function<LevelConfigDTO, Object> extractor;
    /**
     * 描述
     */
    private final String desc;


    /**
     * @return int 数组
     */
    @Override
    public int[] array() {
        return new int[0];
    }

    public static LevelRightsLimitEnums getByRedisKey(String redisKey) {
        return EnumUtil.getBy(LevelRightsLimitEnums.class,
                e -> Objects.equals(redisKey, e.getRedisKey()));
    }
}


