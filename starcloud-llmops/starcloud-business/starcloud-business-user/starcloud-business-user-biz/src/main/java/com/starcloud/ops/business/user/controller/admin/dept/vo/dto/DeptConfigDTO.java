package com.starcloud.ops.business.user.controller.admin.dept.vo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门配置")
public class DeptConfigDTO {

    @Schema(description = "分享聊天历史")
    private Boolean shareChatHistory;

    @Schema(description = "分享应用历史")
    private Boolean shareAppHistory;

    @Schema(description = "分享图片历史")
    private Boolean shareImageHistory;
}
