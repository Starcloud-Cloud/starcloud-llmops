package com.starcloud.ops.business.listing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    private Map<String, DraftFiveDescScoreDTO> fiveDescScore;

    // 描述
    @Schema(description = "产品描述1500到2000个字符")
    private Boolean productLength;

    @Schema(description = "产品描述不能包含，E-mail地址，网站网址，图片")
    private Boolean withoutUrl;

    // 搜索词
    @Schema(description = "搜索词250个字符以内")
    private Boolean searchTermLength;


    public Double totalScore() {
        double d = 0;
        d += BooleanUtils.isTrue(withoutSpecialChat) ? 1 : 0;
        d += BooleanUtils.isTrue(titleLength) ? 1 : 0;
        d += BooleanUtils.isTrue(titleUppercase) ? 1 : 0;
        d += BooleanUtils.isTrue(fiveDescLength) ? 1 : 0;
        d += BooleanUtils.isTrue(allUppercase) ? 1 : 0;
        d += BooleanUtils.isTrue(partUppercase) ? 1 : 0;
        d += BooleanUtils.isTrue(productLength) ? 1 : 0;
        d += BooleanUtils.isTrue(withoutUrl) ? 1 : 0;
        d += BooleanUtils.isTrue(searchTermLength) ? 1 : 0;
        return d;
    }

    public Double scoreProportion() {
        BigDecimal bigDecimal = BigDecimal.valueOf(totalScore()).multiply(BigDecimal.valueOf(100));
        return bigDecimal.divide(new BigDecimal(9),0, RoundingMode.HALF_UP).doubleValue();
    }

}
