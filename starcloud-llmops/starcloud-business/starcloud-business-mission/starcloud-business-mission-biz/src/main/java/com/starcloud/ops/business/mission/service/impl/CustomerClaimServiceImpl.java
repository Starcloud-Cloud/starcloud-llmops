package com.starcloud.ops.business.mission.service.impl;

import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingContentDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.ClaimReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionDetailVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.dal.dataobject.MissionNotificationDTO;
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
    public SingleMissionDetailVO missionDetail(String uid) {
        MissionNotificationDTO detail = missionService.missionDetail(uid);
        SingleMissionDetailVO detailVO = SingleMissionConvert.INSTANCE.convertDetail(detail);
        return detailVO;
    }

    @Override
    public void claim(ClaimReqVO reqVO) {
        RLock lock = redissonClient.getLock(reqVO.getUid());
        if (!lock.tryLock()) {
            throw exception(MISSION_IS_CHANGE);
        }
        try {
            MissionNotificationDTO detail = missionService.missionDetail(reqVO.getUid());
            if (!SingleMissionStatusEnum.stay_claim.getCode().equals(detail.getStatus())) {
                throw exception(MISSION_CAN_NOT_CLAIM, SingleMissionStatusEnum.valueOfCode(detail.getStatus()).getDesc());
            }
            XhsNoteDetailRespVO xhsNoteDetailRespVO = noteDetailService.remoteDetail(reqVO.getPublishUrl());
            PostingContentDTO postingContent = SingleMissionConvert.INSTANCE.toPostingContent(detail.getContent());
            postingContent.validPostingContent(xhsNoteDetailRespVO);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setUid(reqVO.getUid());
            modifyReqVO.setClaimUsername(reqVO.getClaimUsername());
            modifyReqVO.setPublishUrl(reqVO.getPublishUrl());
            modifyReqVO.setStatus(SingleMissionStatusEnum.published.getCode());
            modifyReqVO.setClaimTime(LocalDateTime.now());
            modifyReqVO.setPublishTime(LocalDateTime.now());
            missionService.update(modifyReqVO);
        } finally {
            lock.unlock();
        }
    }
}
