package com.starcloud.ops.business.log.controller.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 应用执行日志结果 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class LogAppMessageExcelVO {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("消息uid")
    private String uid;

    @ExcelProperty("会话ID")
    private String appConversationUid;

    @ExcelProperty("app uid")
    private String appUid;

    @ExcelProperty("app 模式")
    private String appMode;

    @ExcelProperty("app 配置")
    private String appConfig;

    @ExcelProperty("执行的 app step")
    private String appStep;

    @ExcelProperty("模版状态，0：失败，1：成功")
    private Byte status;

    @ExcelProperty("错误码")
    private String errorCode;

    @ExcelProperty("错误信息")
    private String errorMsg;

    @ExcelProperty("app 配置")
    private String variables;

    @ExcelProperty("请求内容")
    private String message;

    @ExcelProperty("消耗token数")
    private Integer messageTokens;

    @ExcelProperty("消耗token单位价格")
    private Long messageUnitPrice;

    @ExcelProperty("返回内容")
    private String answer;

    @ExcelProperty("消耗token数")
    private Integer answerTokens;

    @ExcelProperty("消耗token单位价格")
    private Long answerUnitPrice;

    @ExcelProperty("执行耗时")
    private Object elapsed;

    @ExcelProperty("总消耗价格")
    private Long totalPrice;

    @ExcelProperty("价格单位")
    private String currency;

    @ExcelProperty("执行场景")
    private String fromScene;

    @ExcelProperty("临时用户ID")
    private String endUser;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}