package com.starcloud.ops.business.mission.service;

import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.controller.app.vo.ClaimReqVO;

public interface CustomerClaimService {

    /**
     * 任务详情
     */
    SingleMissionRespVO missionDetail(String uid);

    /**
     * 认领任务
     */
    void claim(ClaimReqVO reqVO);
}
