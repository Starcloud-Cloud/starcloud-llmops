package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import com.starcloud.ops.business.app.api.plugin.RiskWord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "敏感词检测")
public class CreativeContentRiskRespVO {

    @Schema(description = "文案内容")
    private String content;

    @Schema(description = "文字检测结果（带颜色html标签，请自行增加样式，禁用词：\"jwy-topRisk\"，敏感词：\"jwy-lowRisk\"）")
    private String resContent;

    @Schema(description = "禁用词汇总")
    private String topRiskStr;

    @Schema(description = "敏感词汇总")
    private String lowRiskStr;

    @Schema(description = "当前消耗文字字数")
    private Long contentLength;

    @Schema(description = "违禁词列表详情")
    private List<RiskWord> riskList;
}
