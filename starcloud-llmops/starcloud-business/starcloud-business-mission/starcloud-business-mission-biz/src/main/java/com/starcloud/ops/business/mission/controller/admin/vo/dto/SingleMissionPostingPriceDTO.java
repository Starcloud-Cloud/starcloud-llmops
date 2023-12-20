package com.starcloud.ops.business.mission.controller.admin.vo.dto;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleMissionPostingPriceDTO extends PostingUnitPriceDTO {

    /**
     * 任务总预算
     */
    private BigDecimal notificationBudget;

    /**
     * 单个任务预算
     */
    private BigDecimal singleBudget;

    public BigDecimal calculationAmount(Integer likeCount, Integer commentCount) {
        return super.calculationAmount(likeCount, commentCount, singleBudget);
    }
}
