package com.starcloud.ops.business.user.controller.admin.signin.vo.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 签到记录 Response VO")
@Data
public class AppAdminUserSignInRecordRespVO {

    @Schema(description = "第几天签到", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer day;

    @Schema(description = "签到的魔法豆", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer magicBean;

    @Schema(description = "签到的图片", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer magicImage;

    @Schema(description = "签到时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
