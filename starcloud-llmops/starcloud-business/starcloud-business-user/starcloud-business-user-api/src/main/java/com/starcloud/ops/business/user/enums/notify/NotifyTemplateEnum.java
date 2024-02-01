package com.starcloud.ops.business.user.enums.notify;

import cn.hutool.core.util.ReUtil;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.TEMP_PARAMS_NOT_CONSISTENT;

@Getter
public enum NotifyTemplateEnum {

    NOTIFY_PURCHASE_EXPERIENCE("NOTIFY_PURCHASE_EXPERIENCE", "2-3天内的新用户，没买过体验包.", "{userId} {nickname} {createTime} {daysDiff}"),

    NOTIFY_EXPERIENCE_EXPIRED("NOTIFY_EXPERIENCE_EXPIRED", "买过体验包，还有一天过期", " {username} {level} {expireTime}"),
    ;

    private String code;

    private String desc;

    private String templateKey;

    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{([a-zA-Z0-9]*?)}");

    NotifyTemplateEnum(String code, String desc, String templateKey) {
        this.code = code;
        this.desc = desc;
        this.templateKey = templateKey;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(NotifyTemplateEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setDescription(item.getDesc());
                    option.setValue(item.getCode());
                    option.setLabel(item.templateKey);
                    return option;
                }).collect(Collectors.toList());
    }

    public static Boolean contains(String code) {
        for (NotifyTemplateEnum value : NotifyTemplateEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return true;
            }
        }
        return false;
    }

    public static void validTemplateKey(String templateCode, String templateContent) {
        for (NotifyTemplateEnum value : NotifyTemplateEnum.values()) {
            if (Objects.equals(value.getCode(), templateCode)) {
                List<String> requiredKey = ReUtil.findAllGroup1(PATTERN_PARAMS, value.getTemplateKey());
                List<String> contentParams = ReUtil.findAllGroup1(PATTERN_PARAMS, templateContent);
                if (!requiredKey.containsAll(contentParams) || !contentParams.containsAll(requiredKey)) {
                    throw exception(TEMP_PARAMS_NOT_CONSISTENT, contentParams, requiredKey);
                }
            }
        }
    }
}
