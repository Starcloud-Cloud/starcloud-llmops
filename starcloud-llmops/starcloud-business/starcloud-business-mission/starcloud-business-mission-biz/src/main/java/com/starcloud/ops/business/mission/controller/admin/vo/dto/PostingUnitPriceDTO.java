package com.starcloud.ops.business.mission.controller.admin.vo.dto;

import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Optional;

@Data
@Schema(description = "任务单价明细")
public class PostingUnitPriceDTO {

    @Schema(description = "发帖单价")
    @Min(value = 0, message = "发帖单价要大于0")
    private BigDecimal postingUnitPrice;

    @Schema(description = "回复单价")
    @Min(value = 0, message = "回复单价要大于0")
    private BigDecimal replyUnitPrice;

    @Schema(description = "点赞单价")
    @Min(value = 0, message = "点赞单价要大于0")
    private BigDecimal likeUnitPrice;

    /**
     * 结算
     *
     * @param noteDetailRespVO 互动统计
     * @return 结算金额
     */
    public BigDecimal calculationAmount(XhsNoteDetailRespVO noteDetailRespVO,
                                        BigDecimal singleBudget) {
        Integer likeCount = Optional.ofNullable(noteDetailRespVO).map(XhsNoteDetailRespVO::getLikedCount).orElse(0);
        Integer commentCount = Optional.ofNullable(noteDetailRespVO).map(XhsNoteDetailRespVO::getCommentCount).orElse(0);
        return calculationAmount(likeCount, commentCount, singleBudget);
    }

    public BigDecimal calculationAmount(Integer likeCount, Integer commentCount,
                                        BigDecimal singleBudget) {
        likeCount = Optional.ofNullable(likeCount).orElse(0);
        commentCount = Optional.ofNullable(commentCount).orElse(0);
        this.likeUnitPrice = Optional.ofNullable(likeUnitPrice).orElse(BigDecimal.ZERO);
        this.replyUnitPrice = Optional.ofNullable(replyUnitPrice).orElse(BigDecimal.ZERO);
        this.postingUnitPrice = Optional.ofNullable(postingUnitPrice).orElse(BigDecimal.ZERO);
        BigDecimal totalAmount = likeUnitPrice.multiply(BigDecimal.valueOf(likeCount)).add(replyUnitPrice.multiply(BigDecimal.valueOf(commentCount))).add(postingUnitPrice);
        return singleBudget == null ? totalAmount : totalAmount.min(singleBudget);
    }

    public BigDecimal addPrice() {
        this.likeUnitPrice = Optional.ofNullable(likeUnitPrice).orElse(BigDecimal.ZERO);
        this.replyUnitPrice = Optional.ofNullable(replyUnitPrice).orElse(BigDecimal.ZERO);
        this.postingUnitPrice = Optional.ofNullable(postingUnitPrice).orElse(BigDecimal.ZERO);
        return likeUnitPrice.add(replyUnitPrice).add(postingUnitPrice);
    }
}
