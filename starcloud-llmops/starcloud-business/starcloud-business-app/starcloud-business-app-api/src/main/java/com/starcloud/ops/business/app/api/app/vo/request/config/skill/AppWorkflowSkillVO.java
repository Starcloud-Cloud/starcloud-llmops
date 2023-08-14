package com.starcloud.ops.business.app.api.app.vo.request.config.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @todo 先直接让前端传，后面是走单独表去配置
 */
@Data
@Schema(description = "技能配置")
@NoArgsConstructor
public class AppWorkflowSkillVO {

    @Schema(description = "开启")
    private Boolean enabled;

    private String skillAppUid;

}
