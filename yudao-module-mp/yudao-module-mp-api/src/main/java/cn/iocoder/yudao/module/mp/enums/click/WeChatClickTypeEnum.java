package cn.iocoder.yudao.module.mp.enums.click;


import lombok.Getter;

@Getter
public enum WeChatClickTypeEnum {
    SPECIAL_SIGN_IN("special_sign_in","签到"),

    SPECIAL_SHARE("special_share","分享链接");

    private String code;

    private String desc;

    WeChatClickTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Boolean contains(String code) {
        for (WeChatClickTypeEnum value : WeChatClickTypeEnum.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }
}
