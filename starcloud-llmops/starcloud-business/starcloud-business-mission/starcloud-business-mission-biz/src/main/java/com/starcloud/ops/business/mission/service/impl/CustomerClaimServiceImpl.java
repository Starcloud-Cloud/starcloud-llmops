package com.starcloud.ops.business.mission.service.impl;

import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigDTO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.PostingContentDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionDetailVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.ClaimReqVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.dal.dataobject.MissionNotificationDTO;
import com.starcloud.ops.business.mission.service.CustomerClaimService;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Resource
    private CreativePlanService creativePlanService;

    @Resource
    private CreativeSchemeService creativeSchemeService;

    @Override
    public SingleMissionDetailVO missionDetail(String uid) {
        MissionNotificationDTO detail = missionService.missionDetail(uid);
        SingleMissionDetailVO detailVO = SingleMissionConvert.INSTANCE.convertDetail(detail);
        CreativePlanRespVO creativePlan = creativePlanService.get(detail.getCreativePlanUid());
        List<String> schemeUidList = Optional.ofNullable(creativePlan.getConfig()).map(CreativePlanConfigDTO::getSchemeUidList).orElse(Collections.emptyList());
        if (CollectionUtils.isNotEmpty(schemeUidList)) {
            List<CreativeSchemeRespVO> schemeRespVOS = creativeSchemeService.list(schemeUidList);
            List<String> tags = schemeRespVOS.stream().map(CreativeSchemeRespVO::getTags).reduce(new ArrayList<>(), (a, b) -> {
                a.addAll(b);
                return a;
            });
            tags = tags.stream().distinct().collect(Collectors.toList());
            detailVO.setTags(tags);
        }
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
//            postingContent.validPostingContent(xhsNoteDetailRespVO);
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
