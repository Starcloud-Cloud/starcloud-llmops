package com.starcloud.ops.business.limits.controller.admin.userbenefits.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 用户权益创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserBenefitsCreateReqVO extends UserBenefitsBaseVO {

}
