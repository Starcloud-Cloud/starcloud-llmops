package com.starcloud.ops.business.user.api.rights.dto;


import lombok.Data;

/**
 * 用户权益统计
 */

@Data
public class StatisticsUserRightReqDTO {

    private Long userId;
    private Long teamId;
    private Long magicBeanCounts;
    private Long imageCounts;
    private Long matrixBeanCounts;

    public void addMagicBeanCount(int count) {
        this.magicBeanCounts += count;
    }

    public void addImageCount(int count) {
        this.imageCounts += count;
    }

    public void addMatrixBeanCounts(int count) {
        this.imageCounts += count;
    }
}
