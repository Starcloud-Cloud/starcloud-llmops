package com.starcloud.ops.business.listing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "各项分数")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DraftItemScoreDTO {

    // 标题
    @Schema(description = "标题不包含符合或表情")
    private Boolean withoutSpecialChat;

    @Schema(description = "标题包含150到200个字符")
    private Boolean titleLength;

    @Schema(description = "标题中每个单词开头大写")
    private Boolean titleUppercase;

    // 五要要点
    @Schema(description = "每个要点包含150到200个字符")
    private Boolean fiveDescLength;

    @Schema(description = "五要点的第一个字母大写")
    private Boolean allUppercase;

    @Schema(description = "五要点不全是大写")
    private Boolean partUppercase;

    @Schema(description = "五要点打分")
    private Map<String,DraftFiveDescScoreDTO> fiveDescScore;

    // 描述
    @Schema(description = "产品描述1500到2000个字符")
    private Boolean productLength;

    @Schema(description = "产品描述不能包含，E-mail地址，网站网址，图片")
    private Boolean withoutUrl;

    // 搜索词
    @Schema(description = "搜索词250个字符以内")
    private Boolean searchTermLength;


}