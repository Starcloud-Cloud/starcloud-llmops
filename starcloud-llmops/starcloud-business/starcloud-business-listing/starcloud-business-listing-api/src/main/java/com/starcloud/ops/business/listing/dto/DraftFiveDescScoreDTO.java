package com.starcloud.ops.business.listing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "五要点打分")
public class DraftFiveDescScoreDTO {

    @Schema(description = "包含150到200个字符")
    private Boolean fiveDescLength;

    @Schema(description = "首字母大写")
    private Boolean starUppercase;

    @Schema(description = "不全是大写 -- 存在小写 true")
    private Boolean hasLowercase;
}
