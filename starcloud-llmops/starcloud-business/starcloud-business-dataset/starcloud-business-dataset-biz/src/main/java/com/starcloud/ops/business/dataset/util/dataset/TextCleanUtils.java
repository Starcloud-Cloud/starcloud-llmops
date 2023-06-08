package com.starcloud.ops.business.dataset.util.dataset;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class TextCleanUtils {

    public static String cleanText(String text, SplitRule splitRule) {
        if (StringUtils.isNotBlank(splitRule.getPattern())) {
            Pattern line = Pattern.compile(splitRule.getPattern());
            text = line.matcher(text).replaceAll(StringUtils.EMPTY);
        }

        if (BooleanUtils.isNotFalse(splitRule.getAutomatic())) {
            text = removeUrlsEmails(text);
            return text;
        }

        if (BooleanUtils.isTrue(splitRule.getRemoveExtraSpaces())) {
            text = removeExtraSpaces(text);
        }

        if (BooleanUtils.isTrue(splitRule.getRemoveUrlsEmails())) {
            text = removeUrlsEmails(text);
        }
        return text;
    }

    public static String removeExtraSpaces(String text) {
        Pattern line = Pattern.compile("\n{2,}");
        text = line.matcher(text).replaceAll(StringUtils.LF);
        Pattern space = Pattern.compile("[\t\f\r\\x20\u00a0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]{2,}");
        return space.matcher(text).replaceAll(StringUtils.SPACE);
    }

    public static String removeUrlsEmails(String text) {
        Pattern email = Pattern.compile("([a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+)");
        return email.matcher(text).replaceAll(StringUtils.EMPTY);
    }
}
