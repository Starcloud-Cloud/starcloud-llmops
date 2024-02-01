package com.starcloud.ops.business.user.controller.admin.invite.vo.records;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 邀请记录创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class InvitationRecordsCreateReqVO extends InvitationRecordsBaseVO {



}
