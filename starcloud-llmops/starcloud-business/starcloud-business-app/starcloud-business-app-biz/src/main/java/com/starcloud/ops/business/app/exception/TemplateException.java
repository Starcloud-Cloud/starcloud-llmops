package com.starcloud.ops.business.app.exception;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class TemplateException extends RuntimeException {

    /**
     * 异常码
     */
    private Integer code;

    private Set<TemplateErrorData> errorData;

    public TemplateException(ErrorCode errorCode, Set<TemplateErrorData> errorData) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.errorData = errorData;
    }
}
