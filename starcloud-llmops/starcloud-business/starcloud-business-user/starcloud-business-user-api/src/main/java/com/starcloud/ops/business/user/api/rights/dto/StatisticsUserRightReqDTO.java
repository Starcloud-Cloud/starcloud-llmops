package com.starcloud.ops.business.user.api.rights.dto;


import lombok.Data;

/**
 * 用户权益统计
 */

@Data
public class StatisticsUserRightReqDTO {

    private Long userId;
    private Long teamId;
    private String bizId;
    private Integer rightsType;
    private Long magicBeanCounts;
    private Long imageCounts;
    private Long matrixBeanCounts;
}
