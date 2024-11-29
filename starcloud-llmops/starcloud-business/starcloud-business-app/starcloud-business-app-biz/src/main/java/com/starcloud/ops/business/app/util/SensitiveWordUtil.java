package com.starcloud.ops.business.app.util;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.starcloud.ops.business.app.enums.plugin.ProcessMannerEnum;
import org.apache.tika.utils.StringUtils;

import java.util.Objects;
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

//    public static String tongYin(String source) {
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < source.length(); i++) {
//            String ch = source.substring(i, i + 1);
//            String pinyin = PinyinHelper.toPinyin(ch, PinyinStyleEnum.NUM_LAST);
//            List<String> samed = PinyinHelper.samePinyinList(pinyin);
//            if (CollectionUtils.isEmpty(samed) || samed.size() <= 1) {
//                sb.append(pinyin, 0, pinyin.length() - 1);
//            } else {
//                samed.remove(ch);
//                sb.append(samed.get(0));
//            }
//        }
//        return sb.toString();
//    }


}
