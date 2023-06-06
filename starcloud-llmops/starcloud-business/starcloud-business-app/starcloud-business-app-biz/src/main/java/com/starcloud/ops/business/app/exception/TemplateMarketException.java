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
public class TemplateMarketException extends RuntimeException {

    private static final long serialVersionUID = -9190638476688443639L;

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
     * 模版市场异常
     *
     * @param code    // 3-002-000-000 系列
     * @param message 错误信息
     */
    public static TemplateMarketException exception(Integer code, String message) {
        TemplateMarketException exception = new TemplateMarketException();
        exception.setCode(code);
        exception.setMessage(message);
        return exception;
    }

    /**
     * 模版市场异常
     *
     * @param resultCode 错误码
     * @param args       错误信息参数
     * @return TemplateMarketException
     */
    public static TemplateMarketException exception(ResultCode resultCode, Object... args) {
        TemplateMarketException exception = new TemplateMarketException();
        exception.setCode(resultCode.getCode());
        exception.setMessage(String.format(resultCode.getMessage(), args));
        return exception;
    }

}
