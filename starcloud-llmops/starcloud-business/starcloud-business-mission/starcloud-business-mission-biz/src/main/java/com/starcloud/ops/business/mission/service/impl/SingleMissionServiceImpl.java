package com.starcloud.ops.business.mission.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import com.starcloud.ops.business.app.service.xhs.XhsNoteDetailService;
import com.starcloud.ops.business.dto.PostingContentDTO;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDTO;
import com.starcloud.ops.business.mission.dal.mysql.SingleMissionMapper;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.NumberUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    @Resource
    private XhsNoteDetailService noteDetailService;


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
        List<String> toBeBound = new ArrayList<>(CollUtil.subtract(creativeUids, boundCreativeUidList));
        if (CollectionUtils.isEmpty(toBeBound)) {
            return;
        }
        validBudget(notificationCenterDO.getSingleBudget(), notificationCenterDO.getNotificationBudget(), boundCreativeUidList.size() + creativeUids.size());

        List<XhsCreativeContentResp> claimList = creativeContentService.bound(toBeBound);
        List<SingleMissionDO> singleMissions = claimList.stream().map(contentDO -> SingleMissionConvert.INSTANCE.convert(contentDO, notificationCenterDO)).collect(Collectors.toList());
        singleMissionMapper.insertBatch(singleMissions);
    }

    @Override
    public PageResult<SingleMissionRespVO> page(SinglePageQueryReqVO reqVO) {
        Long count = singleMissionMapper.pageCount(reqVO);
        if (count == null || count <= 0) {
            return PageResult.empty();
        }
        List<SingleMissionDTO> singleMissionDTOList = singleMissionMapper.pageDetail(reqVO, PageUtils.getStart(reqVO), reqVO.getPageSize());
        PageResult<SingleMissionRespVO> result = new PageResult<>( SingleMissionConvert.INSTANCE.pageConvert(singleMissionDTOList), count);
        SinglePageQueryReqVO countVo = new SinglePageQueryReqVO();
        countVo.setNotificationUid(reqVO.getNotificationUid());
        countVo.setStatus(SingleMissionStatusEnum.stay_claim.getCode());
        result.setStayClaimCount(singleMissionMapper.pageCount(countVo));
        countVo.setStatus(SingleMissionStatusEnum.claimed.getCode());
        result.setClaimCount(singleMissionMapper.pageCount(countVo));
        countVo.setStatus(SingleMissionStatusEnum.settlement.getCode());
        result.setSettlementCount(singleMissionMapper.pageCount(countVo));
        return result;
    }

    @Override
    public void modifySelective(SingleMissionModifyReqVO reqVO) {
        SingleMissionDO missionDO = getByUid(reqVO.getUid());
        if (SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())) {
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        if (SingleMissionStatusEnum.init.getCode().equals(reqVO.getStatus())) {
            return;
        } else if (SingleMissionStatusEnum.stay_claim.getCode().equals(reqVO.getStatus())) {
            update(missionDO);
        } else if (SingleMissionStatusEnum.claimed.getCode().equals(reqVO.getStatus())) {
            Optional.ofNullable(reqVO.getClaimUsername()).orElseThrow(() -> exception(500, "认领人不能为空"));
            missionDO.setClaimUsername(reqVO.getClaimUsername());
            missionDO.setClaimUserId(Optional.ofNullable(reqVO.getClaimUserId()).orElse("0"));
            LocalDateTime claimTime = Optional.ofNullable(reqVO.getClaimTime()).orElse(LocalDateTime.now());
            missionDO.setClaimTime(claimTime);
        } else if (SingleMissionStatusEnum.published.getCode().equals(reqVO.getStatus())) {
            XhsDetailConstants.validNoteUrl(reqVO.getPublishUrl());
            missionDO.setPublishUrl(reqVO.getPublishUrl());
            String noteId = ReUtil.delAll(XhsDetailConstants.DOMAIN, reqVO.getPublishUrl());
            missionDO.setNoteId(noteId);
            LocalDateTime publishTime = Optional.ofNullable(reqVO.getPublishTime()).orElse(LocalDateTime.now());
            missionDO.setClaimTime(publishTime);
        } else if (SingleMissionStatusEnum.pre_settlement.getCode().equals(reqVO.getStatus())) {
            NotificationRespVO respVO = notificationCenterService.selectByUid(missionDO.getNotificationUid());
            BigDecimal estimatedAmount = respVO.getUnitPrice().calculationAmount(reqVO.getLikedCount(), reqVO.getCommentCount(), respVO.getSingleBudget());
            missionDO.setEstimatedAmount(estimatedAmount);
            LocalDateTime preSettlementTime = Optional.ofNullable(reqVO.getPreSettlementTime()).orElse(LocalDateTime.now());
            missionDO.setPreSettlementTime(preSettlementTime);
        } else if (SingleMissionStatusEnum.settlement.getCode().equals(reqVO.getStatus())) {
            missionDO.setSettlementAmount(reqVO.getSettlementAmount());
            LocalDateTime settlementTime = Optional.ofNullable(reqVO.getSettlementTime()).orElse(LocalDateTime.now());
            missionDO.setSettlementTime(settlementTime);
        }
        missionDO.setStatus(reqVO.getStatus());
        update(missionDO);
    }

    @Override
    public SingleMissionRespVO update(SingleMissionModifyReqVO reqVO) {
        SingleMissionDO missionDO = getByUid(reqVO.getUid());
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

    @Override
    public void validBudget(NotificationCenterDO notificationCenterDO) {
        List<SingleMissionDO> missionList = singleMissionMapper.getByNotificationUid(notificationCenterDO.getUid());
        if (CollectionUtils.isEmpty(missionList)) {
            return;
        }
        validBudget(notificationCenterDO.getSingleBudget(), notificationCenterDO.getNotificationBudget(), missionList.size());
    }

    @Override
    public List<SingleMissionExportVO> exportSettlement(SinglePageQueryReqVO reqVO) {
        List<SingleMissionDO> missionList = singleMissionMapper.export(reqVO);
        if (CollectionUtils.isEmpty(missionList)) {
            return Collections.emptyList();
        }
        return SingleMissionConvert.INSTANCE.convert(missionList);
    }

    @Override
    public void refreshNote(String uid) {
        SingleMissionDO missionDO = getByUid(uid);

        if (!SingleMissionStatusEnum.published.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.pre_settlement.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.settlement_error.getCode().equals(missionDO.getStatus())) {
            // 状态不允许刷新
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        XhsDetailConstants.validNoteUrl(missionDO.getPublishUrl());
        settlement(SingleMissionConvert.INSTANCE.convert(missionDO));
    }

    @Override
    public void settlement(SingleMissionRespVO singleMissionRespVO) {
        try {
            XhsNoteDetailRespVO noteDetail = noteDetailService.refreshByNoteUrl(singleMissionRespVO.getPublishUrl());
            NotificationRespVO notificationRespVO = notificationCenterService.selectByUid(singleMissionRespVO.getNotificationUid());
            // 校验note内容
            validPostingContent(singleMissionRespVO.getContent(), noteDetail);
            BigDecimal amount = calculationAmount(noteDetail, notificationRespVO);
            updateSingleMission(singleMissionRespVO.getUid(), amount);
        } catch (Exception e) {
            log.warn("结算异常 {}", singleMissionRespVO.getUid(), e);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setStatus(SingleMissionStatusEnum.settlement_error.getCode());
            modifyReqVO.setUid(singleMissionRespVO.getUid());
            modifyReqVO.setRunTime(LocalDateTime.now());
            modifyReqVO.setErrorMsg(e.getMessage());
            update(modifyReqVO);
        }
    }


    private void updateSingleMission(String uid, BigDecimal amount) {
        SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
        modifyReqVO.setUid(uid);
        modifyReqVO.setPreSettlementTime(LocalDateTime.now());
        modifyReqVO.setEstimatedAmount(amount);
        modifyReqVO.setStatus(SingleMissionStatusEnum.pre_settlement.getCode());
        modifyReqVO.setRunTime(LocalDateTime.now());
        update(modifyReqVO);
    }

    private void validPostingContent(PostingContentDTO content, XhsNoteDetailRespVO noteDetail) {

    }

    private BigDecimal calculationAmount(XhsNoteDetailRespVO noteDetail, NotificationRespVO notificationRespVO) {
        if (notificationRespVO.getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return notificationRespVO.getUnitPrice().calculationAmount(noteDetail, notificationRespVO.getSingleBudget());
    }


    private void validBudget(BigDecimal singleBudget, BigDecimal notificationBudget, Integer missionSize) {
        if (notificationBudget == null
                || notificationBudget.equals(BigDecimal.ZERO)) {
            throw exception(NOTIFICATION_BUDGET_ERROR);
        }
        if (singleBudget == null
                || singleBudget.equals(BigDecimal.ZERO)) {
            throw exception(MISSION_BUDGET_ERROR);
        }
        NumberUtil.isGreater(singleBudget.multiply(BigDecimal.valueOf(missionSize)), notificationBudget);
        if (NumberUtil.isGreater(singleBudget.multiply(BigDecimal.valueOf(missionSize)), notificationBudget)) {
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

    private void update(SingleMissionDO singleMissionDO) {
        singleMissionDO.setUpdateTime(LocalDateTime.now());
        singleMissionDO.setUpdater(WebFrameworkUtils.getLoginUserId().toString());
        singleMissionMapper.updateById(singleMissionDO);
    }
}
