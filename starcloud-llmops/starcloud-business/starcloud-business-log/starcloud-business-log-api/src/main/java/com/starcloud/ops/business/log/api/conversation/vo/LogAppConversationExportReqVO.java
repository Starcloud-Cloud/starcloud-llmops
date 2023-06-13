package com.starcloud.ops.business.log.api.conversation.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用执行日志会话 Excel 导出 Request VO，参数和 LogAppConversationPageReqVO 是一致的")
@Data
public class LogAppConversationExportReqVO {

    @Schema(description = "会话uid", example = "10286")
    private String uid;

    @Schema(description = "app uid", example = "24921")
    private String appUid;

    @Schema(description = "app 模式")
    private String appMode;

    @Schema(description = "app 配置")
    private String appConfig;

    @Schema(description = "执行状态，error：失败，success：成功", required = true, example = "2")
    private String status;

    @Schema(description = "执行场景")
    private String fromScene;

    @Schema(description = "终端用户ID")
    private String endUser;

    @Schema(description = "模版创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}