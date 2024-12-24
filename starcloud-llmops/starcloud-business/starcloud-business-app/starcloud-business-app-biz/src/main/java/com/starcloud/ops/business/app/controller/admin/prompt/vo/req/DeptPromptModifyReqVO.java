package com.starcloud.ops.business.app.controller.admin.prompt.vo.req;

import com.starcloud.ops.business.app.controller.admin.prompt.vo.PromptBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "修改提示词")
public class DeptPromptModifyReqVO extends PromptBaseVO {

    @Schema(description = "提示词uid")
    @NotBlank(message = "提示词uid必填")
    private String uid;
}
