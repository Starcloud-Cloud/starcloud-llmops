package com.starcloud.ops.business.app.exception;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 步骤结果异常
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionResponseException extends RuntimeException {

    private static final long serialVersionUID = 7113571922194139438L;

    /**
     * 全局错误码
     */
    private Integer code;

    /**
     * 全局错误信息
     */
    private String message;

    /**
     * 步骤结果
     */
    private ActionResponse response;

    /**
     * 构造函数
     */
    public ActionResponseException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param cause 异常
     */
    public ActionResponseException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数
     *
     * @param code     错误码
     * @param message  错误信息
     * @param response 步骤结果
     */
    public ActionResponseException(Integer code, String message, ActionResponse response) {
        super(message);
        this.code = code;
        this.message = message;
        this.response = response;
    }

    /**
     * 构造函数
     *
     * @param code     错误码
     * @param message  错误信息
     * @param response 步骤结果
     * @param cause    异常
     */
    public ActionResponseException(Integer code, String message, ActionResponse response, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.response = response;
    }

    /**
     * 构造函数
     *
     * @param response  步骤结果
     * @param exception 异常
     * @return 步骤结果异常
     */
    public static ActionResponseException exception(ActionResponse response, Exception exception) {
        int errorCode = ErrorCodeConstants.EXECUTE_APP_ACTION_FAILURE.getCode();
        if (exception instanceof ServiceException ||
                (exception.getCause() != null && exception.getCause() instanceof ServiceException)) {
            ServiceException serviceException = (ServiceException) (exception instanceof ServiceException ? exception : exception.getCause());
            errorCode = serviceException.getCode();
        }

        response.setSuccess(false);
        response.setErrorCode(String.valueOf(errorCode));
        response.setErrorMsg(exception.getMessage());
        response.setCostPoints(0);

        // 如果是 ActionResponseException 异常，更新 response 后直接返回。
        if (exception instanceof ActionResponseException) {
            ActionResponseException actionResponseException = (ActionResponseException) exception;
            actionResponseException.setCode(errorCode);
            actionResponseException.setResponse(response);
            return actionResponseException;
        }

        // 抛出异常
        ActionResponseException actionResponseException = new ActionResponseException(exception);
        actionResponseException.setCode(errorCode);
        actionResponseException.setMessage(exception.getMessage());
        actionResponseException.setResponse(response);
        throw actionResponseException;
    }

}
