package com.starcloud.ops.business.listing.utils;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.starcloud.ops.business.listing.dto.DraftFiveDescScoreDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListingDraftScoreUtil {


    public static Boolean checkForMatch(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * 不包含 符号/表情
     *
     * @param text
     * @return
     */
    public static Boolean withoutSpecialChat(String text) {
        if (StringUtils.isBlank(text)) {
            return true;
        }
        return !containsEmoji(text) && !containsSymbol(text);
    }

    public static Boolean containsEmoji(String text) {
        String emojiPattern = "[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+";
        return checkForMatch(text, emojiPattern);
    }

    public static Boolean containsSymbol(String text) {
        String symbolPattern = "\\p{Punct}";
        return checkForMatch(text, symbolPattern);
    }

    /**
     * 是否包含图片/email/网址
     *
     * @param text
     * @return
     */
    public static Boolean contains(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        return containsUrl(text) || containsPic(text) || containsEmail(text);
    }

    public static Boolean allUppercase(Map<String, String> fiveDesc) {
        if (fiveDesc == null) {
            return false;
        }

        for (String value : fiveDesc.values()) {
            if (!uppercase(value)) {
                return false;
            }
        }
        return true;
    }

    public static Boolean partUppercase(Map<String, String> fiveDesc) {
        if (fiveDesc == null) {
            return false;
        }

        for (String value : fiveDesc.values()) {
            if (uppercase(value)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean uppercase(String text) {
        return !StringUtils.isBlank(text) && Character.isUpperCase(text.charAt(0));
    }


    public static Boolean titleUppercase(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        for (String s : text.split(" ")) {
            if (!Character.isUpperCase(s.charAt(0))) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, DraftFiveDescScoreDTO> fiveDescScore(Map<String, String> fiveDesc) {
        if (fiveDesc == null) {
            return null;
        }
        Map<String, DraftFiveDescScoreDTO> result = new HashMap<>(fiveDesc.size());
        for (String key : fiveDesc.keySet()) {
            String value = fiveDesc.get(key);
            Boolean uppercase = uppercase(value);
            Boolean length = judgmentLength(value, 150, 250);
            DraftFiveDescScoreDTO draftFiveDescScoreDTO = new DraftFiveDescScoreDTO(length, uppercase);
            result.put(key,draftFiveDescScoreDTO);
        }
        return result;
    }

    public static Boolean judgmentLength(String text, int min, int max) {
        return (!StringUtils.isBlank(text)) && text.length() >= min && text.length() < max;
    }

    public static Boolean judgmentLength(Map<String, String> fiveDesc, int min, int max) {
        if (fiveDesc == null) {
            return false;
        }
        for (String value : fiveDesc.values()) {
            if (StringUtils.isBlank(value) || value.length() < min || value.length() > max) {
                return false;
            }
        }
        return true;
    }

    public static Boolean containsUrl(String text) {
        String websiteRegex = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,}\\.){1,4}([a-zA-Z]){2,6}(\\/([a-zA-Z-_\\/\\.0-9#:?=&;,]*)?)?)";

        return checkForMatch(text, websiteRegex);
    }

    public static Boolean containsPic(String text) {
        String imageRegex = "([^\\s]+.(?i)(jpg|png|gif|bmp))";
        return checkForMatch(text, imageRegex);
    }


    public static Boolean containsEmail(String text) {
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return checkForMatch(text, emailRegex);
    }


}