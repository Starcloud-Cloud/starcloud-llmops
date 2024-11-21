package com.starcloud.ops.business.user.controller.admin.level.vo.level;

import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 会员等级创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserLevelDetailRespVO extends AdminUserLevelBaseVO {

    /**
     * 等级配置
     */
    @Schema(description = "等级配置", example = "xxx")
    private LevelConfigDTO levelConfigDTO;

    /**
     * 等级配置
     */
    @Schema(description = " 显示排序", example = "xxx")
    private Integer sort;

}
