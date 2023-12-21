package com.starcloud.ops.business.user.controller.admin.rights.vo.rights;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "系统会员用户 App - 用户积分记录 Response VO")
@Data
public class AppAdminUserRightsRespVO {

    @Schema(description = "自增主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31457")
    private Long id;

    @Schema(description = "积分标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    private String title;

    @Schema(description = "积分描述", example = "你猜")
    private String description;

    @Schema(description = "魔法豆", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer magicBeanInit;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer magicImageInit;

    @Schema(description = "发生时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
