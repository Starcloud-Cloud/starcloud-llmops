package com.starcloud.ops.business.mission.api.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "小程序查询通告列表")
public class AppNotificationQueryReqVO extends PageParam {

    @Schema(description = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String claimUserId;

//    @Schema(description = "区域")
//    private String address;
//
//    @Schema(description = "性别")
//    private String gender;
//
//    @Schema(description = "帐号类型")
//    private String accountType;
//
//    @Schema(description = "粉丝数")
//    private Long fansNum;

}
