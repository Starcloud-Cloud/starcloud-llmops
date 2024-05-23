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

    CS("捷克语","cs"),
    DA("丹麦语","da"),
    DE("德语","de"),
    EN("英语","en"),
    ES("西班牙语","es"),
    FR("法语","fr"),
    ID("印尼语","id"),
    IT("意大利语","it"),
    HU("匈牙利语","hu"),
    NL("荷兰语","nl"),
    NO("挪威语","no"),
    PL("波兰语","pl"),
    PT("葡萄牙语","pt"),
    RO("罗马尼亚语","ro"),
    SK("斯洛伐克语","sk"),
    FI("芬兰语","fi"),
    SV("瑞典语","sv"),
    TR("土耳其语","tr"),
    VI("越南语","vi"),
    TH("泰语","th"),
    BG("保加利亚语","bg"),
    RU("俄语","ru"),
    EL("希腊语","el"),
    JA("日语","ja"),
    KO("韩语","ko"),
    ZH("中文","zh"),

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
