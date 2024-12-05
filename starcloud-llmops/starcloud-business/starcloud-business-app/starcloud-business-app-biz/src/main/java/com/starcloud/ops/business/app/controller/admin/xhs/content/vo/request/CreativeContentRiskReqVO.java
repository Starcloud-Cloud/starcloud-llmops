package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Schema(description = "敏感词检测")
public class CreativeContentRiskReqVO {

    /**
     * 文案内容
     */
    @Schema(description = "文案内容")
    @NotBlank(message = "文案内容必填")
    private String content;


}
