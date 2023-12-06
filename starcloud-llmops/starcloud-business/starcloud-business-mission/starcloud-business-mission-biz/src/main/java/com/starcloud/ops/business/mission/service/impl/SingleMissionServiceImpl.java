package com.starcloud.ops.business.mission.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionImportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import com.starcloud.ops.business.dto.PostingContentDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
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
    private CreativeContentService creativeContentService;

    @Resource
    private NotificationCenterService notificationCenterService;

    @Resource
    private XhsNoteDetailService noteDetailService;

    @Resource
    private DictDataService dictDataService;


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

        List<CreativeContentRespVO> claimList = creativeContentService.bound(toBeBound);
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
        PageResult<SingleMissionRespVO> result = new PageResult<>(SingleMissionConvert.INSTANCE.pageConvert(singleMissionDTOList), count);
//        SinglePageQueryReqVO countVo = new SinglePageQueryReqVO();
//        countVo.setNotificationUid(reqVO.getNotificationUid());
//        countVo.setStatus(SingleMissionStatusEnum.stay_claim.getCode());
//        result.setStayClaimCount(singleMissionMapper.pageCount(countVo));
//        countVo.setStatus(SingleMissionStatusEnum.claimed.getCode());
//        result.setClaimCount(singleMissionMapper.pageCount(countVo));
//        countVo.setStatus(SingleMissionStatusEnum.settlement.getCode());
//        result.setSettlementCount(singleMissionMapper.pageCount(countVo));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
            Optional.ofNullable(reqVO.getClaimUsername()).orElseThrow(() -> exception(new ErrorCode(500, "认领人不能为空")));
            Assert.notBlank(reqVO.getClaimUsername(), "认领人不能为空");
            missionDO.setClaimUsername(reqVO.getClaimUsername());
            missionDO.setClaimUserId(Optional.ofNullable(reqVO.getClaimUserId()).orElse("0"));
            LocalDateTime claimTime = Optional.ofNullable(reqVO.getClaimTime()).orElse(LocalDateTime.now());
            missionDO.setClaimTime(claimTime);
        } else if (SingleMissionStatusEnum.published.getCode().equals(reqVO.getStatus())) {
            XhsDetailConstants.validNoteUrl(reqVO.getPublishUrl());
            missionDO.setPublishUrl(reqVO.getPublishUrl());
            LocalDateTime publishTime = Optional.ofNullable(reqVO.getPublishTime()).orElse(LocalDateTime.now());
            missionDO.setClaimTime(publishTime);
        } else if (SingleMissionStatusEnum.pre_settlement.getCode().equals(reqVO.getStatus())) {
            LocalDateTime preSettlementTime = Optional.ofNullable(reqVO.getPreSettlementTime()).orElse(LocalDateTime.now());
            missionDO.setPreSettlementTime(preSettlementTime);
            XhsNoteDetailRespVO noteDetail = noteDetailService.preSettlement(missionDO.getUid(), reqVO.getLikedCount(),
                    reqVO.getCommentCount(), SingleMissionConvert.INSTANCE.toPriceDTO(missionDO.getUnitPrice()));
            missionDO.setEstimatedAmount(noteDetail.getAmount());
            missionDO.setNoteDetailId(noteDetail.getId());
        } else if (SingleMissionStatusEnum.settlement.getCode().equals(reqVO.getStatus())) {
            missionDO.setSettlementAmount(reqVO.getSettlementAmount());
            LocalDateTime settlementTime = Optional.ofNullable(reqVO.getSettlementTime()).orElse(LocalDateTime.now());
            missionDO.setSettlementTime(settlementTime);
        } else if (SingleMissionStatusEnum.close.getCode().equals(reqVO.getStatus())) {
            missionDO.setCloseMsg(reqVO.getCloseMsg());
        } else if (SingleMissionStatusEnum.settlement_error.getCode().equals(reqVO.getStatus())) {
            missionDO.setSettlementMsg(reqVO.getSettlementMsg());
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
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uid) {
        SingleMissionDO missionDO = getByUid(uid);
        if (!SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.stay_claim.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.close.getCode().equals(missionDO.getStatus())) {
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        creativeContentService.unBound(Collections.singletonList(missionDO.getCreativeUid()));
        singleMissionMapper.deleteById(missionDO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<String> uids) {
        List<SingleMissionDO> singleMissionDOList = singleMissionMapper.listByUids(uids);
        boolean unAllowed = singleMissionDOList.stream().anyMatch(missionDO -> {
            return !SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())
                    && !SingleMissionStatusEnum.stay_claim.getCode().equals(missionDO.getStatus())
                    && !SingleMissionStatusEnum.close.getCode().equals(missionDO.getStatus());
        });
        if (unAllowed) {
            throw exception(new ErrorCode(500, "只允许删除 待发布 待认领 关闭状态的任务"));
        }
        List<String> creativeUids = singleMissionDOList.stream().map(SingleMissionDO::getCreativeUid).collect(Collectors.toList());
        creativeContentService.unBound(creativeUids);
        singleMissionMapper.batchDelete(uids);
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
    public void publish(NotificationCenterDO notificationCenterDO, Boolean publish) {
        List<SingleMissionDO> singleMissionList = singleMissionMapper.getByNotificationUid(notificationCenterDO.getUid());
        if (publish && CollectionUtils.isEmpty(singleMissionList)) {
            throw exception(NOTIFICATION_NOT_BOUND_MISSION, notificationCenterDO.getUid());
        }
        if (BooleanUtils.isTrue(publish)) {
            // 发布 只修改未发布的状态 更新单价明细
            for (SingleMissionDO missionDO : singleMissionList) {
                if (SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())) {
                    missionDO.setStatus(SingleMissionStatusEnum.stay_claim.getCode());
                }
                SingleMissionPostingPriceDTO priceDTO = SingleMissionConvert.INSTANCE.convert(notificationCenterDO);
                missionDO.setUnitPrice(SingleMissionConvert.INSTANCE.toStr(priceDTO));
            }
            singleMissionMapper.updateBatch(singleMissionList, singleMissionList.size());
        } else {
            // 取消发布 已认领的任务状态不变
            List<SingleMissionDO> stayClaimList = singleMissionList.stream().filter(singleMissionDO -> {
                return SingleMissionStatusEnum.stay_claim.getCode().equals(singleMissionDO.getStatus());
            }).collect(Collectors.toList());
            stayClaimList.forEach(missionDO -> {
                missionDO.setStatus(SingleMissionStatusEnum.init.getCode());
            });
            if (CollectionUtils.isEmpty(stayClaimList)) {
                return;
            }
            singleMissionMapper.updateBatch(stayClaimList, stayClaimList.size());
        }
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
        List<SingleMissionExportVO> exportVOList = singleMissionMapper.export(reqVO);
        DictDataDO dictDataDO = dictDataService.parseDictData("notification_config", "claim_url");
        exportVOList.forEach(exportVo -> {
            exportVo.setStatus(SingleMissionStatusEnum.valueOfCode(exportVo.getStatus()).getDesc());
            exportVo.setClaimUrl(dictDataDO.getValue() + exportVo.getUid());
        });
        return exportVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshNote(String uid) {
        SingleMissionDO missionDO = getByUid(uid);

        if (!SingleMissionStatusEnum.published.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.pre_settlement.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.pre_settlement_error.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.settlement_error.getCode().equals(missionDO.getStatus())) {
            // 状态不允许刷新
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        XhsDetailConstants.validNoteUrl(missionDO.getPublishUrl());
        preSettlement(SingleMissionConvert.INSTANCE.convert(missionDO));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settlement(SingleMissionRespVO singleMissionRespVO) {
        try {
            preSettlement(singleMissionRespVO);
        } catch (Exception e) {
            log.warn("结算异常 {}", singleMissionRespVO.getUid(), e);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setStatus(SingleMissionStatusEnum.pre_settlement_error.getCode());
            modifyReqVO.setUid(singleMissionRespVO.getUid());
            modifyReqVO.setRunTime(LocalDateTime.now());
            modifyReqVO.setPreSettlementMsg(e.getMessage());
            update(modifyReqVO);
        }
    }

    @Override
    public void deleteNotification(String notificationUid) {
        List<SingleMissionDO> singleMissionDOList = singleMissionMapper.listByNotification(notificationUid);
        if (CollectionUtils.isEmpty(singleMissionDOList)) {
            return;
        }

        for (SingleMissionDO missionDO : singleMissionDOList) {
            if (SingleMissionStatusEnum.published.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.pre_settlement.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.pre_settlement_error.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.settlement_error.getCode().equals(missionDO.getStatus())) {
                throw exception(DONT_ALLOW_DELETE);
            }
        }
        List<String> creativeUids = singleMissionDOList.stream().map(SingleMissionDO::getCreativeUid).collect(Collectors.toList());
        creativeContentService.unBound(creativeUids);
        singleMissionMapper.batchDelete(singleMissionDOList.stream().map(SingleMissionDO::getUid).collect(Collectors.toList()));
    }

    @Override
    public void importSettlement(List<SingleMissionImportVO> importVOList) {
        if (CollectionUtils.isEmpty(importVOList)) {
            throw exception(EXCEL_IS_EMPTY);
        }
        List<String> uidList = importVOList.stream().map(SingleMissionImportVO::getUid).collect(Collectors.toList());
        List<SingleMissionDO> singleMissionDOList = singleMissionMapper.listByUids(uidList);
        if (singleMissionDOList.size() < importVOList.size()) {
            Collection<String> subtract = CollUtil.subtract(uidList, singleMissionDOList.stream().map(SingleMissionDO::getUid).collect(Collectors.toList()));
            throw exception(NOT_EXIST_UID, subtract.toString());
        }
        boolean unStayClaim = singleMissionDOList.stream().anyMatch(mission -> {
            return !SingleMissionStatusEnum.stay_claim.getCode().equals(mission.getStatus());
        });

        if (unStayClaim) {
            throw exception(ONLY_STAY_CLAIM);
        }

        Map<String, Long> doMap = singleMissionDOList.stream().collect(Collectors.toMap(SingleMissionDO::getUid, SingleMissionDO::getId));
        List<SingleMissionDO> updateList = new ArrayList<>(importVOList.size());
        String userId = WebFrameworkUtils.getLoginUserId().toString();
        LocalDateTime now = LocalDateTime.now();
        for (SingleMissionImportVO importVO : importVOList) {
            importVO.valid();
            SingleMissionDO missionDO = new SingleMissionDO();
            missionDO.setClaimUsername(importVO.getClaimUsername());
            missionDO.setPublishUrl(importVO.getPublishUrl());
            missionDO.setUid(importVO.getUid());
            missionDO.setStatus(SingleMissionStatusEnum.published.getCode());
            missionDO.setUpdater(userId);
            missionDO.setUpdateTime(now);
            missionDO.setPublishTime(now);
            missionDO.setId(doMap.get(importVO.getUid()));
            updateList.add(missionDO);
        }
        singleMissionMapper.updateBatch(updateList, updateList.size());
    }

    private void preSettlement(SingleMissionRespVO singleMissionRespVO) {
        XhsNoteDetailRespVO noteDetail = noteDetailService.preSettlementByUrl(singleMissionRespVO.getUid(), singleMissionRespVO.getPublishUrl(), singleMissionRespVO.getUnitPrice());
        // 校验note内容
        validPostingContent(singleMissionRespVO.getContent(), noteDetail);
        updateSingleMission(singleMissionRespVO.getUid(), noteDetail.getAmount(), noteDetail.getId());
    }

    private void updateSingleMission(String uid, BigDecimal amount, Long noteDetailId) {
        SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
        modifyReqVO.setUid(uid);
        modifyReqVO.setPreSettlementTime(LocalDateTime.now());
        modifyReqVO.setEstimatedAmount(amount);
        modifyReqVO.setStatus(SingleMissionStatusEnum.pre_settlement.getCode());
        modifyReqVO.setRunTime(LocalDateTime.now());
        modifyReqVO.setNoteDetailId(noteDetailId);
        update(modifyReqVO);
    }

    private void validPostingContent(PostingContentDTO content, XhsNoteDetailRespVO noteDetail) {
        if (content != null && noteDetail != null
                && StringUtils.equals(content.getTitle(), noteDetail.getTitle())
                && StringUtils.equals(content.getText(), noteDetail.getDesc())) {
            return;
        }
        throw exception(CONTENT_INCONSISTENT);
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
