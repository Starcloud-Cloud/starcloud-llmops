package com.starcloud.ops.business.app.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-19
 */
public class LogUtils {

    /**
     * 格式化答案
     *
     * @param answer 答案
     * @return 格式化后的答案
     */
    public static String formatAnswer(String answer) {
        if (StringUtils.isBlank(answer)) {
            return "";
        }
        String formatAnswer = answer.trim();
        if (formatAnswer.startsWith("\"")) {
            formatAnswer = formatAnswer.substring(1);
        }
        if (formatAnswer.endsWith("\"")) {
            formatAnswer = formatAnswer.substring(0, formatAnswer.length() - 1);
        }
        return formatAnswer;
    }
}
