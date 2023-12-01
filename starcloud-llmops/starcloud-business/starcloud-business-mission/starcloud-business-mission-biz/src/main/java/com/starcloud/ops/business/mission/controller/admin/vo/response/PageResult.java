package com.starcloud.ops.business.mission.controller.admin.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = -4368767133376722880L;

    @Schema(description = "数据", required = true)
    private List<T> list;

    @Schema(description = "总量", required = true)
    private Long total;

//    @Schema(description = "待领取数")
//    private Long stayClaimCount;
//
//    @Schema(description = "领取数")
//    private Long claimCount;
//
//    @Schema(description = "完成数")
//    private Long settlementCount;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

}
