package com.starcloud.ops.business.app.enums.channel;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-27
 */
@Getter
public enum AppPublishChannelEnum implements IEnumable<Integer> {

    MARKET(1, "应用市场", "应用市场"),

    SHARE_LINK(2, "分享链接", "分享链接"),

    JS_IFRAME(3, "JS嵌入", "JS嵌入"),

    OPEN_API(4, "API 调用", "API 调用"),

    WX_GROUP(5, "微信群聊", "微信群聊"),

    WX_MP(6, "微信公众号", "微信公众号"),

    WX_WORK(7, "企业微信", "企业微信"),

    WX_MINI_APP(8, "微信小程序", "微信小程序"),

    FEI_SHU(9, "飞书", "飞书"),

    DING_TALK(10, "钉钉", "钉钉"),

    QQ(11, "QQ", "QQ"),

    ;

    private final Integer code;

    private final String label;

    private final String description;

    AppPublishChannelEnum(Integer code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }
}
