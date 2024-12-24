package com.starcloud.ops.business.app.controller.admin.prompt.vo.resp;

import com.starcloud.ops.business.app.controller.admin.prompt.vo.PromptBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "提示词详情")
public class PromptRespVO extends PromptBaseVO {

    private String uid;
}
