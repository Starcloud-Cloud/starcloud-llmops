package com.starcloud.ops.business.mission.api.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "预结算记录")
public class PreSettlementRecordRespVO {

    @Schema(description = "点赞数")
    private Integer likedCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "佣金")
    private BigDecimal amount;

}
