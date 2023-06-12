package com.starcloud.ops.business.log.controller.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应用执行日志会话 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class LogAppConversationExcelVO {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("会话uid")
    private String uid;

    @ExcelProperty("app uid")
    private String appUid;

    @ExcelProperty("app 模式")
    private String appMode;

    @ExcelProperty("app 配置")
    private String appConfig;

    @ExcelProperty("模版状态，0：失败，1：成功")
    private Byte status;

    @ExcelProperty("执行场景")
    private String fromScene;

    @ExcelProperty("终端用户ID")
    private String endUser;

    @ExcelProperty("模版创建时间")
    private LocalDateTime createTime;

}