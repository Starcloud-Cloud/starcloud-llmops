package com.starcloud.ops.business.log.api.messagesave.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用执行日志结果保存 Excel 导出 Request VO，参数和 LogAppMessageSavePageReqVO 是一致的")
@Data
public class LogAppMessageSaveExportReqVO {

    @Schema(description = "uid", example = "17312")
    private String uid;

    @Schema(description = "会话ID", example = "15763")
    private String appConversationUid;

    @Schema(description = "消息ID", example = "17481")
    private String appMessageUid;

    @Schema(description = "消息内容标识，返回一个结果的情况下字段默认都为空")
    private String appMessageItem;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}