package com.starcloud.ops.business.app.api.app.vo.request.config.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @todo 先直接让前端传，后面是走单独表去配置
 */
@Data
@Schema(description = "GPT插件类型的技能配置")
@NoArgsConstructor
public class GptPluginSkillVO {

    @Schema(description = "开启")
    private Boolean enabled;


}
