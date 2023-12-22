package com.starcloud.ops.business.mission.dal.dataobject;

import lombok.Data;

@Data
public class SingleMissionDTO extends SingleMissionDO{

    /**
     * 点赞数
     */
    private Integer likedCount;

    /**
     * 收藏数
     */
    private Integer collectedCount;

    /**
     * 评论数
     */
    private Integer commentCount;

    /**
     * 创作计划
     */
    private String planName;

    /**
     * 认领人
     */
    private String claimUsername;
}
