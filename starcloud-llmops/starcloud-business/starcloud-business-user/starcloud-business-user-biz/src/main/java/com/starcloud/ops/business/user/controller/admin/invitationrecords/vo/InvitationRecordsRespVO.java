package com.starcloud.ops.business.user.controller.admin.invitationrecords.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 邀请记录 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvitationRecordsRespVO extends InvitationRecordsBaseVO {

    @Schema(description = "主键 ID", required = true, example = "6819")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

}
