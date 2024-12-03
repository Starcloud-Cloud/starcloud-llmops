package com.starcloud.ops.business.app.util;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.starcloud.ops.business.app.enums.plugin.ProcessMannerEnum;
import org.apache.tika.utils.StringUtils;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveWordUtil {

    public static String replace(String sourceContent, String riskWord, String processType) {
        String transformedKey = transformKey(riskWord, processType);
        return sourceContent.replaceAll(riskWord, transformedKey);
    }

    public static String transformKey(String riskWord, String processType) {
        if (Objects.equals(processType, ProcessMannerEnum.asterisk.getCode())) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < riskWord.length(); i++) {
                sb.append("*");
            }
            return sb.toString();
        } else if (Objects.equals(processType, ProcessMannerEnum.pinyin.getCode())) {
            return PinyinUtil.getPinyin(riskWord, StringUtils.EMPTY);
        } else {
            return StringUtils.EMPTY;
        }
    }


}
