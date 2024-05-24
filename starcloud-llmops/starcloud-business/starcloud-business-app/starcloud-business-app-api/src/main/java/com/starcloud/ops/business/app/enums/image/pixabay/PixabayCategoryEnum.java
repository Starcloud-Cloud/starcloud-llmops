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

    backgrounds("背景","backgrounds"),
    fashion("时尚","fashion"),
    nature("自然","nature"),
    science("科学","science"),
    education("教育","education"),
    feelings("情感","feelings"),
    health("健康","health"),
    people("人","people"),
    religion("宗教","religion"),
    places("地方","places"),
    animals("动物","animals"),
    industry("工业","industry"),
    computer("计算机","computer"),
    food("食品","food"),
    sports("体育","sports"),
    transportation("交通","transportation"),
    travel("旅游","travel"),
    buildings("建筑","buildings"),
    business("商业","business"),
    music("音乐","music"),

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
