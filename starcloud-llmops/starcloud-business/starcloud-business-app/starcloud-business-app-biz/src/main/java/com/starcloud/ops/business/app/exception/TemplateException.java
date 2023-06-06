package com.starcloud.ops.business.app.exception;

import cn.iocoder.yudao.framework.common.exception.enums.ServiceErrorCodeRange;
import com.starcloud.ops.framework.common.api.dto.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemplateException extends RuntimeException {

    private static final long serialVersionUID = 3865745191658514218L;

    /**
     * 业务错误码
     *
     * @see ServiceErrorCodeRange
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 模版异常
     *
     * @param code    // 3-001-000-000 系列
     * @param message // 错误信息
     */
    public static TemplateException exception(Integer code, String message) {
        TemplateException exception = new TemplateException();
        exception.setCode(code);
        exception.setMessage(message);
        return exception;
    }

    /**
     * 模版异常
     *
     * @param resultCode 错误码
     * @param args       错误信息参数
     * @return TemplateException
     */
    public static TemplateMarketException exception(ResultCode resultCode, Object... args) {
        TemplateMarketException exception = new TemplateMarketException();
        exception.setCode(resultCode.getCode());
        exception.setMessage(String.format(resultCode.getMessage(), args));
        return exception;
    }
}
