package com.starcloud.ops.business.log.api.feedbacks.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用执行日志结果反馈 Excel 导出 Request VO，参数和 LogAppMessageFeedbacksPageReqVO 是一致的")
@Data
public class LogAppMessageFeedbacksExportReqVO {

    @Schema(description = "uid", example = "633")
    private String uid;

    @Schema(description = "会话ID", example = "5983")
    private String appConversationUid;

    @Schema(description = "消息ID", example = "28830")
    private String appMessageUid;

    @Schema(description = "消息内容标识，返回一个结果的情况下字段默认都为空")
    private String appMessageItem;

    @Schema(description = "反馈类型，like: 喜欢，dislike:不喜欢")
    private String rating;

    @Schema(description = "临时用户ID")
    private String endUser;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}