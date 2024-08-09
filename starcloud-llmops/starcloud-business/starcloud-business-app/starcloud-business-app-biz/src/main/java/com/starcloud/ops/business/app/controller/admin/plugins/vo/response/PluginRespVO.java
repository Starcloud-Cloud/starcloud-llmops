package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "插件配置")
public class PluginRespVO extends PluginDefinitionVO {

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建人")
    private String creator;

}
