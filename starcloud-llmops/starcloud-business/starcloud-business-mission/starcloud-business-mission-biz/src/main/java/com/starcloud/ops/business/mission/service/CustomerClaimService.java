package com.starcloud.ops.business.mission.service;

import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionDetailVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.ClaimReqVO;

public interface CustomerClaimService {

    /**
     * 任务详情
     */
    SingleMissionDetailVO missionDetail(String uid);

    /**
     * 认领任务
     */
    void claim(ClaimReqVO reqVO);
}
