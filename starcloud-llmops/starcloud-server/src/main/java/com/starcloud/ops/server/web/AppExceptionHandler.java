package com.starcloud.ops.server.web;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.exception.AppLimitException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，将 Exception 翻译成 CommonResult + 对应的异常编号
 *
 * @author 芋道源码
 */
@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class AppExceptionHandler {

    /**
     * 限流异常
     */
    @ExceptionHandler(value = AppLimitException.class)
    public CommonResult<?> defaultExceptionHandler(AppLimitException exception) {
        log.error("[AppLimitException]: message: {}", exception.getMessage(), exception);
        return CommonResult.error(exception.getCode(), exception.getMessage());
    }

}
