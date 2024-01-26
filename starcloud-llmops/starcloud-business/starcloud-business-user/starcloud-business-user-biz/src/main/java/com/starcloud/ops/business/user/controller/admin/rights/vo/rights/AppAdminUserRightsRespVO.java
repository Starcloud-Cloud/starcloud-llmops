package com.starcloud.ops.business.user.controller.admin.rights.vo.rights;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "系统会员用户 App - 用户权益记录 Response VO")
@Data
public class AppAdminUserRightsRespVO {

    @Schema(description = "自增主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31457")
    private Long id;

    /**
     * 业务类型
     * <p>
     * 枚举 {@link AdminUserRightsBizTypeEnum}
     */
    @Schema(description = "权益类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    @InEnum(AdminUserRightsBizTypeEnum.class)
    private Integer bizType;

    @Schema(description = "权益标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    private String title;

    @Schema(description = "权益描述", example = "你猜")
    private String description;

    @Schema(description = "魔法豆", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer magicBeanInit;

    @Schema(description = "图片", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer magicImageInit;

    /**
     * '关联用户等级ID'
     * <p>
     */
    @Schema(description = "等级 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long userLevelId;

    @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private String levelName;

    @Schema(description = "生效时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime validStartTime;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime validEndTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

}
