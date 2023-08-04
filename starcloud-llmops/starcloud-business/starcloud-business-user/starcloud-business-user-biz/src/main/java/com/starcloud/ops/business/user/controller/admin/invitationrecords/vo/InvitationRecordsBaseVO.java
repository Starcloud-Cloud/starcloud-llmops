package com.starcloud.ops.business.user.controller.admin.invitationrecords.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
* 邀请记录 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class InvitationRecordsBaseVO {

    @Schema(description = "邀请人 ID", required = true, example = "12854")
    @NotNull(message = "邀请人 ID不能为空")
    private Long inviterId;

    @Schema(description = "被邀请人 ID", required = true, example = "11389")
    @NotNull(message = "被邀请人 ID不能为空")
    private Long inviteeId;

    @Schema(description = "邀请时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime invitationDate;

}
