package com.starcloud.ops.business.app.domain.handler.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * action 响应实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class HandlerResponse<R> {

    /**
     * 响应状态
     */
    private Boolean success;

    /**
     * 响应错误码
     */
    private Integer errorCode;

    /**
     * 响应错误码
     */
    private String errorMsg;

    /**
     * 执行耗时
     */
    private long elapsed;

    /**
     * 相应类型
     */
    private String type;

    /**
     * 请求数据
     */
    private String message;

    /**
     * 响应数据
     */
    private String answer;

    /**
     * 响应数据
     */
    private R output;

    /**
     * 扩展信息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Object ext;


    /**
     * 请求 token 使用
     */
    private Long messageTokens = 0L;

    /**
     * 请求单价
     */
    private BigDecimal messageUnitPrice = BigDecimal.ZERO;

    /**
     * 响应 token 使用
     */
    private Long answerTokens = 0L;

    /**
     * 响应单价
     */
    private BigDecimal answerUnitPrice = BigDecimal.ZERO;

    /**
     * 总 token 数量
     */
    private Long totalTokens = 0L;

    /**
     * 总价格
     */
    private BigDecimal totalPrice = BigDecimal.ZERO;

    /**
     * step 执行的参数
     */
    private Object stepConfig;


}
