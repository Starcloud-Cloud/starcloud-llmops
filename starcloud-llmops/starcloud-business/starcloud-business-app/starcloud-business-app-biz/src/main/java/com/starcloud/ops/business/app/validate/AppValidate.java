package com.starcloud.ops.business.app.validate;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-06
 */
public class AppValidate {

    /**
     * 断言对象为空
     *
     * @param object 对象
     * @param code   错误码
     * @param args   错误码参数
     */
    public static void isNull(Object object, ErrorCode code, Object... args) {
        Assert.isNull(object, () -> ServiceExceptionUtil.exception(code, args));
    }

    /**
     * 断言对象不为空
     *
     * @param object 对象
     * @param code   错误码
     * @param args   错误码参数
     */
    public static void notNull(Object object, ErrorCode code, Object... args) {
        Assert.notNull(object, () -> ServiceExceptionUtil.exception(code, args));
    }

    /**
     * 断言字符串不为空
     *
     * @param str  字符串
     * @param code 错误码
     * @param args 错误码参数
     */
    public static void notBlank(String str, ErrorCode code, Object... args) {
        Assert.notBlank(str, () -> ServiceExceptionUtil.exception(code, args));
    }

    /**
     * 断言表达式为真
     *
     * @param expression 表达式
     * @param code       错误码
     * @param args       错误码参数
     */
    public static void isTrue(boolean expression, ErrorCode code, Object... args) {
        Assert.isTrue(expression, () -> ServiceExceptionUtil.exception(code, args));
    }

    /**
     * 断言表达式为假
     *
     * @param expression 表达式
     * @param code       错误码
     * @param args       错误码参数
     */
    public static void isFalse(boolean expression, ErrorCode code, Object... args) {
        Assert.isFalse(expression, () -> ServiceExceptionUtil.exception(code, args));
    }

    /**
     * 断言集合不为空
     *
     * @param collection 集合
     * @param code       错误码
     * @param args       错误码参数
     * @param <E>        集合元素类型
     * @param <T>        集合类型
     */
    public static <E, T extends Iterable<E>> void notEmpty(T collection, ErrorCode code, Object... args) {
        Assert.notEmpty(collection, () -> ServiceExceptionUtil.exception(code, args));
    }

}