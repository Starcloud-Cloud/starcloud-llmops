package com.starcloud.ops.framework.common.api.util;

import cn.hutool.core.util.StrUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2022-10-25
 */
public class ExceptionUtil {


    /**
     * 获取异常的堆栈信息，默认限制长度为 500
     *
     * @param throwable 异常
     * @return 异常堆栈信息
     */
    public static String stackTraceToString(Throwable throwable) {
        return stackTraceToString(throwable, 1500);
    }

    /**
     * 获取异常的堆栈信息，限制长度
     *
     * @param throwable 异常
     * @param limit     限制长度, 小于等于0表示不限制
     * @return 异常堆栈信息
     */
    public static String stackTraceToString(Throwable throwable, int limit) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        String message = stringWriter.toString();
        if (limit <= 0) {
            return message;
        }
        printWriter.flush();
        printWriter.close();
        return StrUtil.subPre(message, Math.min(limit, message.length()));
    }
}
