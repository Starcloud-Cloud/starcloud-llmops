package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "敏感词替换")
public class RiskReplaceRespVO {

    @Schema(description = "文案内容")
    private String replaceContent;

    public RiskReplaceRespVO(String replaceContent) {
        this.replaceContent = replaceContent;
    }
}
