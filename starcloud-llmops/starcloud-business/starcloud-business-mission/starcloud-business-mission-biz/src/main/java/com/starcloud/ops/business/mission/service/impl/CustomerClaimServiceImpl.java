package com.starcloud.ops.business.mission.service.impl;

import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.controller.app.vo.ClaimReqVO;
import com.starcloud.ops.business.mission.service.CustomerClaimService;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.MISSION_CAN_NOT_CLAIM;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.MISSION_IS_CHANGE;

@Slf4j
@Service
public class CustomerClaimServiceImpl implements CustomerClaimService {

    @Resource
    private SingleMissionService missionService;

    @Resource
    private XhsNoteDetailService noteDetailService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public SingleMissionRespVO missionDetail(String uid) {
        return missionService.missionDetail(uid);
    }

    @Override
    public void claim(ClaimReqVO reqVO) {
        RLock lock = redissonClient.getLock(reqVO.getUid());
        if (!lock.tryLock()) {
            throw exception(MISSION_IS_CHANGE);
        }
        try {
            SingleMissionRespVO singleMissionRespVO = missionService.missionDetail(reqVO.getUid());
            if (!SingleMissionStatusEnum.stay_claim.getCode().equals(singleMissionRespVO.getStatus())) {
                throw exception(MISSION_CAN_NOT_CLAIM, SingleMissionStatusEnum.valueOfCode(singleMissionRespVO.getStatus()).getDesc());
            }
            XhsNoteDetailRespVO xhsNoteDetailRespVO = noteDetailService.remoteDetail(reqVO.getPublishUrl());
            singleMissionRespVO.getContent().validPostingContent(xhsNoteDetailRespVO);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setUid(reqVO.getUid());
            modifyReqVO.setClaimUsername(reqVO.getClaimUsername());
            modifyReqVO.setPublishUrl(reqVO.getPublishUrl());
            modifyReqVO.setStatus(SingleMissionStatusEnum.claimed.getCode());
            modifyReqVO.setClaimTime(LocalDateTime.now());
            missionService.update(modifyReqVO);
        } finally {
            lock.unlock();
        }
    }
}
