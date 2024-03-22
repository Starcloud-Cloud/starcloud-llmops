package com.starcloud.ops.business.user.controller.admin.rights.vo.rights;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "用户 App - 权益分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppAdminUserRightsPageReqVO extends PageParam {

    @Schema(description = "业务ID", example = "1")
    private Integer bizId;

}