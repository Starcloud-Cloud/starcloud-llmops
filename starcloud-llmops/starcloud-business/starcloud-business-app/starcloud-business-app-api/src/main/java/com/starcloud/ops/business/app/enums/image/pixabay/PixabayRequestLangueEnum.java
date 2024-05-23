package com.starcloud.ops.business.app.enums.image.pixabay;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-12
 */
@Getter
public enum PixabayRequestLangueEnum implements IEnumable<Integer> {

    cs("捷克语","cs"),
    da("丹麦语","da"),
    de("德语","de"),
    en("英语","en"),
    es("西班牙语","es"),
    fr("法语","fr"),
    id("印尼语","id"),
    it("意大利语","it"),
    hu("匈牙利语","hu"),
    nl("荷兰语","nl"),
    no("挪威语","no"),
    pl("波兰语","pl"),
    pt("葡萄牙语","pt"),
    ro("罗马尼亚语","ro"),
    sk("斯洛伐克语","sk"),
    fi("芬兰语","fi"),
    sv("瑞典语","sv"),
    tr("土耳其语","tr"),
    vi("越南语","vi"),
    th("泰语","th"),
    bg("保加利亚语","bg"),
    ru("俄语","ru"),
    el("希腊语","el"),
    ja("日语","ja"),
    ko("韩语","ko"),
    zh("中文","zh"),

    ;


    /**
     * 标签
     */
    private final String chineseName;

    /**
     * 标签英文
     */
    private final String code;

    PixabayRequestLangueEnum( String chineseName, String code) {
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
