package com.starcloud.ops.business.mission.api.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import com.starcloud.ops.business.enums.NotificationSortFieldEnum;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.api.vo.request.*;
import com.starcloud.ops.business.mission.api.vo.response.AppNotificationRespVO;
import com.starcloud.ops.business.mission.api.vo.response.AppSingleMissionRespVO;
import com.starcloud.ops.business.mission.api.vo.response.PreSettlementRecordRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.ClaimLimitDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.convert.NotificationCenterConvert;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.convert.XhsNoteDetailConvert;
import com.starcloud.ops.business.mission.dal.dataobject.*;
import com.starcloud.ops.business.mission.dal.mysql.NotificationCenterMapper;
import com.starcloud.ops.business.mission.dal.mysql.SingleMissionMapper;
import com.starcloud.ops.business.mission.api.WechatAppApi;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.*;

@Slf4j
@Service
public class WechatAppApiImpl implements WechatAppApi {

    @Resource
    private SingleMissionMapper singleMissionMapper;

    @Resource
    private XhsNoteDetailService xhsNoteDetailService;

    @Resource
    private NotificationCenterMapper notificationCenterMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private XhsNoteDetailService noteDetailService;


    @Override
    public PageResult<AppNotificationRespVO> notifyPage(AppNotificationQueryReqVO reqVO) {
        reqVO.setOpen(BooleanUtils.isNotFalse(reqVO.getOpen()));
        Long count = notificationCenterMapper.appPageCount(reqVO);
        if (count == null || count <= 0) {
            return PageResult.empty();
        }
        List<AppNotificationDTO> notificationCenterDOList = notificationCenterMapper.appPage(reqVO,
                PageUtils.getStart(reqVO), reqVO.getPageSize(),
                NotificationSortFieldEnum.getColumn(reqVO.getSortField()), BooleanUtil.isTrue(reqVO.getAsc()) ? "ASC" : "DESC");
        List<AppNotificationRespVO> respVOList = NotificationCenterConvert.INSTANCE.appConvert(notificationCenterDOList);
        return new PageResult<>(respVOList, count);
    }

    @Override
    public AppSingleMissionRespVO claimMission(AppClaimReqVO reqVO) {
        String lockKey = "claim_mission" + reqVO.getNotificationUid();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (!lock.tryLock(3, 3, TimeUnit.SECONDS)) {
                throw exception(RETRY);
            }
            NotificationCenterDO notificationCenterDO = notificationByUid(reqVO.getNotificationUid());
            if (!NotificationCenterStatusEnum.published.getCode().equals(notificationCenterDO.getStatus())) {
                throw exception(NOTIFICATION_CLOSED);
            }

            ClaimLimitDTO claimLimit = NotificationCenterConvert.INSTANCE.toLimit(notificationCenterDO.getClaimLimit());
            List<SingleMissionDO> missionDOList = singleMissionMapper.listByNotification(reqVO.getNotificationUid());
            long count = missionDOList.stream().filter(mission -> reqVO.getClaimUserId().equals(mission.getClaimUserId())).count();
            if (count >= claimLimit.getClaimNum()) {
                throw exception(MORE_THAN_CLAIMED_NUM, claimLimit.getClaimNum());
            }
            // 校验用户属性 todo

            Optional<SingleMissionDO> stayClaimMission = missionDOList.stream().filter(mission -> SingleMissionStatusEnum.stay_claim.getCode().equals(mission.getStatus())).findFirst();
            if (!stayClaimMission.isPresent()) {
                throw exception(ALL_CLAIMED);
            }
            SingleMissionDO singleMissionDO = stayClaimMission.get();
            singleMissionDO.setClaimUserId(reqVO.getClaimUserId());
            singleMissionDO.setClaimUsername(reqVO.getClaimUsername());
            singleMissionDO.setClaimTime(LocalDateTime.now());
            singleMissionDO.setStatus(SingleMissionStatusEnum.claimed.getCode());
            singleMissionMapper.updateById(singleMissionDO);
            return SingleMissionConvert.INSTANCE.appConvert(singleMissionDO);
        } catch (InterruptedException e) {
            log.warn("lock interrupted", e);
            throw exception(RETRY);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishMission(AppMissionPublishReqVO reqVO) {
        SingleMissionDO singleMissionDO = missionByUid(reqVO.getMissionUid());
        if (!SingleMissionStatusEnum.claimed.getCode().equals(singleMissionDO.getStatus())) {
            throw exception(MISSION_CAN_NOT_PUBLISH_STATUS, SingleMissionStatusEnum.valueOfCode(singleMissionDO.getStatus()));
        }
        if (!reqVO.getClaimUserId().equals(singleMissionDO.getClaimUserId())) {
            throw exception(MISSION_CAN_NOT_PUBLISH_USERID);
        }
        XhsNoteDetailRespVO noteDetail = xhsNoteDetailService.remoteDetail(reqVO.getPublishUrl());
        SingleMissionRespVO singleMissionRespVO = SingleMissionConvert.INSTANCE.convert(singleMissionDO);
        singleMissionRespVO.getContent().validPostingContent(noteDetail);
        singleMissionDO.setPublishUrl(reqVO.getPublishUrl());
        singleMissionDO.setPublishTime(LocalDateTime.now());
        singleMissionDO.setStatus(SingleMissionStatusEnum.published.getCode());
        singleMissionMapper.updateById(singleMissionDO);
    }

    @Override
    public void abandonMission(AppAbandonMissionReqVO reqVO) {
        SingleMissionDO singleMissionDO = missionByUid(reqVO.getMissionUid());
        if (SingleMissionStatusEnum.settlement.getCode().equals(singleMissionDO.getStatus())
                || SingleMissionStatusEnum.settlement_error.getCode().equals(singleMissionDO.getStatus())) {
            throw exception(MISSION_CAN_NOT_ABANDON_STATUS, SingleMissionStatusEnum.valueOfCode(singleMissionDO.getStatus()));
        }
        if (!reqVO.getClaimUserId().equals(singleMissionDO.getClaimUserId())) {
            throw exception(MISSION_CAN_NOT_ABANDON_USERID);
        }
        NotificationCenterDO notificationCenterDO = notificationByUid(singleMissionDO.getNotificationUid());
        singleMissionDO.setClaimUserId(StringUtils.EMPTY);
        singleMissionDO.setClaimUsername(StringUtils.EMPTY);
        singleMissionDO.setClaimTime(null);
        singleMissionDO.setPublishUrl(StringUtils.EMPTY);
        singleMissionDO.setPublishTime(null);
        if (NotificationCenterStatusEnum.published.getCode().equals(notificationCenterDO.getStatus())) {
            singleMissionDO.setStatus(SingleMissionStatusEnum.stay_claim.getCode());
        } else {
            singleMissionDO.setStatus(SingleMissionStatusEnum.init.getCode());
        }
        singleMissionDO.setPreSettlementTime(null);
        singleMissionDO.setEstimatedAmount(BigDecimal.ZERO);
        singleMissionDO.setCloseMsg(StringUtils.EMPTY);
        singleMissionDO.setPreSettlementMsg(StringUtils.EMPTY);
        singleMissionDO.setNoteDetailId(null);
        singleMissionMapper.updateMission(singleMissionDO);
    }

    @Override
    public PageResult<AppSingleMissionRespVO> claimedMission(ClaimedMissionQueryReqVO reqVO) {
        Long count = singleMissionMapper.claimedMissionCount(reqVO);
        if (count == null || count <= 0) {
            return PageResult.empty();
        }
        List<MissionNotificationDTO> missionDOList = singleMissionMapper.claimedMissionPage(reqVO, PageUtils.getStart(reqVO), reqVO.getPageSize());
        List<AppSingleMissionRespVO> respVOList = SingleMissionConvert.INSTANCE.appConvert(missionDOList);
        return new PageResult<>(respVOList, count);
    }

    @Override
    public AppSingleMissionRespVO missionDetail(String missionUid) {
        SingleMissionDO missionDO = missionByUid(missionUid);
        XhsNoteDetailDO noteDetailDO = xhsNoteDetailService.getById(missionDO.getNoteDetailId());
        return SingleMissionConvert.INSTANCE.appConvert(missionDO, noteDetailDO);
    }

    @Override
    public AppNotificationRespVO notifyDetail(String notificationUid, String userId) {
        NotificationCenterDO notificationCenterDO = notificationByUid(notificationUid);
        notificationCenterDO.setVisitNum((notificationCenterDO.getVisitNum() == null ? 0 : notificationCenterDO.getVisitNum()) + 1);
        notificationCenterMapper.updateById(notificationCenterDO);
        AppNotificationRespVO respVO = NotificationCenterConvert.INSTANCE.appConvert(notificationCenterDO);
        List<SingleMissionDO> singleMissionDOList = singleMissionMapper.listByNotification(notificationUid);
        respVO.setTotal(singleMissionDOList.size());
        Integer claimCount = 0;
        Integer currentUserNum = 0;
        for (SingleMissionDO missionDO : singleMissionDOList) {
            if (SingleMissionStatusEnum.claimed.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.published.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.pre_settlement.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.settlement.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.settlement_error.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.pre_settlement_error.getCode().equals(missionDO.getStatus())) {
                claimCount++;
                if (Objects.equals(userId, missionDO.getClaimUserId())) {
                    currentUserNum++;
                }
            }
        }
        respVO.setClaimCount(claimCount);
        respVO.setCurrentUserNum(currentUserNum);
        return respVO;
    }

    @Override
    public PageResult<PreSettlementRecordRespVO> preSettlementRecord(PreSettlementRecordReqVO reqVO) {
        SingleMissionDO missionDO = missionByUid(reqVO.getMissionUid());
        if (Objects.equals(reqVO.getClaimUserId(), missionDO.getClaimUserId())) {
            throw exception(NOT_FOR_SELF);
        }
        PageResult<XhsNoteDetailDO> result = noteDetailService.preSettlementRecord(reqVO);
        return XhsNoteDetailConvert.INSTANCE.convert(result);
    }

    private SingleMissionDO missionByUid(String uid) {
        SingleMissionDO missionDO = singleMissionMapper.getByUid(uid);
        if (missionDO == null) {
            throw exception(MISSION_NOT_EXISTS, uid);
        }
        return missionDO;
    }

    public NotificationCenterDO notificationByUid(String uid) {
        NotificationCenterDO notificationCenterDO = notificationCenterMapper.selectByUid(uid);
        if (notificationCenterDO == null) {
            throw exception(NOTIFICATION_NOT_EXISTS, uid);
        }
        return notificationCenterDO;
    }
}
