package com.starcloud.ops.business.app.validate.app;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-06
 */
public class AppValidate {

    public static void isNull(Object object, ErrorCode code, Object... args) {
        Assert.isNull(object, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void notNull(Object object, ErrorCode code, Object... args) {
        Assert.notNull(object, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void notBlank(String str, ErrorCode code, Object... args) {
        Assert.notBlank(str, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void isTrue(boolean expression, ErrorCode code, Object... args) {
        Assert.isTrue(expression, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static void isFalse(boolean expression, ErrorCode code, Object... args) {
        Assert.isFalse(expression, () -> ServiceExceptionUtil.exception(code, args));
    }

    public static <E, T extends Iterable<E>> void notEmpty(T collection, ErrorCode code, Object... args) {
        Assert.notEmpty(collection, () -> ServiceExceptionUtil.exception(code, args));
    }

}
