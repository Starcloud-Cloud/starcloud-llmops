package com.starcloud.ops.business.log.controller.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 应用执行日志结果保存 Excel VO
 *
 * @author 芋道源码
 */
@Data
public class LogAppMessageSaveExcelVO {

    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("uid")
    private String uid;

    @ExcelProperty("会话ID")
    private String appConversationUid;

    @ExcelProperty("消息ID")
    private String appMessageUid;

    @ExcelProperty("消息内容标识，返回一个结果的情况下字段默认都为空")
    private String appMessageItem;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}