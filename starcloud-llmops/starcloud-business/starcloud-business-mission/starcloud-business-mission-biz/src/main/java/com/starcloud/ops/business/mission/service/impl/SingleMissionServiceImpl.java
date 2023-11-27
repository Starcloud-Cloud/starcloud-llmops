package com.starcloud.ops.business.mission.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import com.starcloud.ops.business.mission.dal.mysql.SingleMissionMapper;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.*;


@Slf4j
@Service
@Validated
public class SingleMissionServiceImpl implements SingleMissionService {

    @Resource
    private SingleMissionMapper singleMissionMapper;

    @Resource
    private XhsCreativeContentService creativeContentService;

    @Resource
    private NotificationCenterService notificationCenterService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSingleMission(String notificationUid, List<String> creativeUids) {
        NotificationCenterDO notificationCenterDO = notificationCenterService.getByUid(notificationUid);
        if (NotificationCenterStatusEnum.published.getCode().equals(notificationCenterDO.getStatus())) {
            throw exception(NOTIFICATION_STATUS_NOT_SUPPORT, notificationCenterDO.getStatus());
        }
        List<SingleMissionDO> missions = singleMissionMapper.listByCreativeUids(creativeUids);
        if (CollectionUtils.isNotEmpty(missions)) {
            throw exception(EXISTING_BOUND_CREATIVE);
        }
        List<String> boundCreativeUidList = singleMissionMapper.getByNotificationUid(notificationUid)
                .stream().map(SingleMissionDO::getCreativeUid).collect(Collectors.toList());
        List<String> toBeBound = new ArrayList<>(CollUtil.subtract(boundCreativeUidList, creativeUids));
        if (CollectionUtils.isEmpty(toBeBound)) {
            return;
        }
        validBudget(notificationCenterDO, boundCreativeUidList.size() + creativeUids.size());

        List<XhsCreativeContentResp> claimList = creativeContentService.bound(toBeBound);
        List<SingleMissionDO> singleMissions = claimList.stream().map(contentDO -> SingleMissionConvert.INSTANCE.convert(contentDO, notificationCenterDO)).collect(Collectors.toList());
        singleMissionMapper.insertBatch(singleMissions);
    }

    @Override
    public PageResult<SingleMissionRespVO> page(SinglePageQueryReqVO reqVO) {
        PageResult<SingleMissionDO> page = singleMissionMapper.page(reqVO);
        return SingleMissionConvert.INSTANCE.convert(page);
    }

    @Override
    public SingleMissionRespVO modifySelective(SingleMissionModifyReqVO reqVO) {
        SingleMissionDO missionDO = getByUid(reqVO.getUid());
        if (!SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.close.getCode().equals(missionDO.getStatus())) {
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        SingleMissionConvert.INSTANCE.updateSelective(reqVO, missionDO);
        missionDO.setUpdateTime(LocalDateTime.now());
        singleMissionMapper.updateById(missionDO);
        return SingleMissionConvert.INSTANCE.convert(missionDO);
    }

    @Override
    public void delete(String uid) {
        SingleMissionDO missionDO = getByUid(uid);
        if (!SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.close.getCode().equals(missionDO.getStatus())) {
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        singleMissionMapper.deleteById(missionDO.getId());
    }

    @Override
    public void pick(String uid) {
        SingleMissionDO missionDO = getByUid(uid);
        if (!SingleMissionStatusEnum.stay_claim.getCode().equals(missionDO.getStatus())) {
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        NotificationCenterDO notification = notificationCenterService.getByUid(missionDO.getNotificationUid());
        if (NotificationCenterStatusEnum.published.getCode().equals(notification.getStatus())) {
            throw exception(NOTIFICATION_STATUS_NOT_SUPPORT, notification.getStatus());
        }
        missionDO.setClaimTime(LocalDateTime.now());
        missionDO.setStatus(SingleMissionStatusEnum.claimed.getCode());
        singleMissionMapper.updateById(missionDO);
    }

    @Override
    public void publish(String notificationUid, Boolean publish) {
        List<SingleMissionDO> singleMissionList = singleMissionMapper.getByNotificationUid(notificationUid);
        if (CollectionUtils.isEmpty(singleMissionList)) {
            throw exception(NOTIFICATION_NOT_BOUND_MISSION, notificationUid);
        }
        String status = BooleanUtils.isTrue(publish) ? SingleMissionStatusEnum.stay_claim.getCode() : SingleMissionStatusEnum.init.getCode();
        singleMissionList.forEach(missionDO -> {
            missionDO.setStatus(status);
        });
        singleMissionMapper.updateBatch(singleMissionList, singleMissionList.size());
    }

    @Override
    public SingleMissionRespVO getById(Long id) {
        return SingleMissionConvert.INSTANCE.convert(singleMissionMapper.selectById(id));
    }

    @Override
    public List<Long> selectIds(SingleMissionQueryReqVO reqVO) {
        return singleMissionMapper.selectIds(reqVO);
    }

    private void validBudget(NotificationCenterDO notificationCenterDO, Integer missionSize) {
        if (notificationCenterDO.getNotificationBudget() == null
                || notificationCenterDO.getNotificationBudget().equals(BigDecimal.ZERO)) {
            throw exception(NOTIFICATION_BUDGET_ERROR);
        }
        if (notificationCenterDO.getSingleBudget() == null
                || notificationCenterDO.getSingleBudget().equals(BigDecimal.ZERO)) {
            throw exception(MISSION_BUDGET_ERROR);
        }
        int compared = notificationCenterDO.getSingleBudget().multiply(BigDecimal.valueOf(missionSize)).compareTo(notificationCenterDO.getNotificationBudget());
        if (compared > 0) {
            throw exception(TOO_MANY_MISSION);
        }
    }

    private SingleMissionDO getByUid(String uid) {
        SingleMissionDO missionDO = singleMissionMapper.getByUid(uid);
        if (missionDO == null) {
            throw exception(MISSION_NOT_EXISTS, uid);
        }
        return missionDO;
    }
}
