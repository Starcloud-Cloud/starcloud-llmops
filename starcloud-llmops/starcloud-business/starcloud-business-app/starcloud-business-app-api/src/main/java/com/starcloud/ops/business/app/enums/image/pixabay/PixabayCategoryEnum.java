package com.starcloud.ops.business.app.enums.image.pixabay;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Getter
public enum PixabayCategoryEnum implements IEnumable<Integer> {

    BACKGROUNDS("背景","backgrounds"),
    FASHION("时尚","fashion"),
    NATURE("自然","nature"),
    SCIENCE("科学","science"),
    EDUCATION("教育","education"),
    FEELINGS("情感","feelings"),
    HEALTH("健康","health"),
    PEOPLE("人","people"),
    RELIGION("宗教","religion"),
    PLACES("地方","places"),
    ANIMALS("动物","animals"),
    INDUSTRY("工业","industry"),
    COMPUTER("计算机","computer"),
    FOOD("食品","food"),
    SPORTS("体育","sports"),
    TRANSPORTATION("交通","transportation"),
    TRAVEL("旅游","travel"),
    BUILDINGS("建筑","buildings"),
    BUSINESS("商业","business"),
    MUSIC("音乐","music"),

    ;


    /**
     * 标签
     */
    private final String chineseName;

    /**
     * 标签英文
     */
    private final String code;

    PixabayCategoryEnum(String chineseName, String code) {
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
