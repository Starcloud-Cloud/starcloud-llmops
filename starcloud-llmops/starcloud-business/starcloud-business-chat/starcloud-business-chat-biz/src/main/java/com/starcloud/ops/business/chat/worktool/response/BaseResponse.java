package com.starcloud.ops.business.chat.worktool.response;

import lombok.Data;

@Data
public class BaseResponse<T> {

    /**
     * 0 为接口请求成功
     */
    private int code;

    private String message;

    private T data;

}
