package com.starcloud.ops.framework.common.api.dto;

/**
 * 通用返回码
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
public interface ResultCode {

    /**
     * 返回码
     *
     * @return 返回码
     */
    Integer getCode();

    /**
     * 返回信息
     *
     * @return 返回信息
     */
    String getMessage();
}
