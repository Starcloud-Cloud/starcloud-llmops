package com.starcloud.ops.business.user.enums;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;

import java.util.function.Function;

import com.starcloud.ops.business.user.api.level.dto.LevelConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户权益限制枚举
 */
@AllArgsConstructor
@Getter
public enum LevelRightsLimitEnums implements IntArrayValuable {


    LISTING_QUERY("listing_query", LevelConfig::getListingQuery, "Listing 查询"),
    QUICK_PUBLISH_COUNT("quick_publish_count", LevelConfig::getQuickPublishCount, "一键发布"),
    ;

    /**
     * 描述
     */
    private final String redisKey;

    private final Function<LevelConfig, Object> extractor;
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
}
