package com.starcloud.ops.business.app.exception;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 限流异常
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-01
 */
@SuppressWarnings("all")
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class AppLimitException extends RuntimeException {

    private static final long serialVersionUID = 666583625940820152L;

    /**
     * 异常码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 构造方法
     *
     * @param errorCode 异常信息
     */
    public AppLimitException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    /**
     * 构造方法
     *
     * @param code    异常码
     * @param message 异常信息
     */
    public AppLimitException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法
     *
     * @param code      异常码
     * @param message   异常信息
     * @param throwable 异常
     */
    public AppLimitException(Integer code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
        this.message = message;
    }

    /**
     * 获取异常
     *
     * @param code    异常码
     * @param message 异常信息
     * @return 异常
     */
    public static AppLimitException exception(Integer code, String message) {
        return new AppLimitException(code, message);
    }

}
