package com.starcloud.ops.business.mission.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.common.util.object.PageUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.member.dal.dataobject.user.MemberUserDO;
import cn.iocoder.yudao.module.member.enums.point.MemberPointBizTypeEnum;
import cn.iocoder.yudao.module.member.service.point.MemberPointRecordService;
import cn.iocoder.yudao.module.member.service.user.MemberUserService;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.starcloud.ops.business.app.model.content.ImageContent;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.dto.SingleMissionPostingPriceDTO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.RefreshNoteDetailReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionImportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SinglePageQueryReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.PageResult;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionExportVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.dal.dataobject.MissionNotificationDTO;
import com.starcloud.ops.business.mission.dal.dataobject.NotificationCenterDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDO;
import com.starcloud.ops.business.mission.dal.dataobject.SingleMissionDTO;
import com.starcloud.ops.business.mission.dal.mysql.SingleMissionMapper;
import com.starcloud.ops.business.mission.service.NotificationCenterService;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import com.starcloud.ops.business.mission.service.XhsNoteDetailService;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.BUDGET_ERROR;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.CAN_NOT_REFRESH;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.DONT_ALLOW_DELETE;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.EXCEL_IS_EMPTY;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.EXISTING_BOUND_CREATIVE;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.MISSION_NOT_EXISTS;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.MISSION_STATUS_NOT_SUPPORT;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.NOTIFICATION_NOT_BOUND_MISSION;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.NOTIFICATION_STATUS_NOT_SUPPORT;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.NOT_EXIST_UID;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.ONLY_STAY_CLAIM;
import static com.starcloud.ops.business.enums.ErrorCodeConstant.TOO_MANY_MISSION;


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

    @Resource
    private MemberPointRecordService memberPointRecordService;

    @Resource
    private MemberUserService memberUserService;

    @Resource
    private DeptPermissionApi deptPermissionApi;


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
        List<CreativeContentRespVO> claimList = creativeContentService.batchBind(toBeBound);
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
        DictDataDO dictDataDO = dictDataService.parseDictData("notification_config", "claim_url");
        result.getList().forEach(missionRespVO -> {
            missionRespVO.setClaimUrl(dictDataDO.getValue() + missionRespVO.getUid());
        });
        return result;
    }

    @Override
    public MissionNotificationDTO missionDetail(String uid) {
        MissionNotificationDTO detail = singleMissionMapper.detail(uid);
        if (detail == null) {
            throw exception(MISSION_NOT_EXISTS, uid);
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySelective(SingleMissionModifyReqVO reqVO) {
        SingleMissionDO missionDO = getByUid(reqVO.getUid());
        deptPermissionApi.checkPermission(DeptPermissionEnum.mission_edit, Long.valueOf(missionDO.getCreator()));

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
            Assert.notBlank(reqVO.getClaimUserId(), "认领人不能为空");
            missionDO.setClaimUserId(reqVO.getClaimUserId());
            LocalDateTime claimTime = Optional.ofNullable(reqVO.getClaimTime()).orElse(LocalDateTime.now());
            missionDO.setClaimTime(claimTime);
        } else if (SingleMissionStatusEnum.published.getCode().equals(reqVO.getStatus())) {
            XhsDetailConstants.validNoteUrl(reqVO.getPublishUrl());
            Assert.notBlank(missionDO.getClaimUserId(), "认领人不能为空");
            missionDO.setPublishUrl(reqVO.getPublishUrl());
            LocalDateTime publishTime = Optional.ofNullable(reqVO.getPublishTime()).orElse(LocalDateTime.now());
            missionDO.setPublishTime(publishTime);
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
        deptPermissionApi.checkPermission(DeptPermissionEnum.mission_delete, Long.valueOf(missionDO.getCreator()));

        if (!SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.stay_claim.getCode().equals(missionDO.getStatus())
                && !SingleMissionStatusEnum.close.getCode().equals(missionDO.getStatus())) {
            throw exception(MISSION_STATUS_NOT_SUPPORT);
        }
        creativeContentService.batchUnbind(Collections.singletonList(missionDO.getCreativeUid()));
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
        // 只有当前用户有删除权限才能批量删除
        deptPermissionApi.checkPermission(DeptPermissionEnum.mission_delete, null);

        List<String> creativeUids = singleMissionDOList.stream().map(SingleMissionDO::getCreativeUid).collect(Collectors.toList());
        creativeContentService.batchUnbind(creativeUids);
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
        // 校验金额
        validBudget(notificationCenterDO.getSingleBudget(), notificationCenterDO.getNotificationBudget(), singleMissionList.size());
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
    public List<Long> selectSettlementIds(SingleMissionQueryReqVO reqVO) {
        return singleMissionMapper.selectSettlementIds(reqVO);
    }

    @Override
    public List<Long> retryIds(SingleMissionQueryReqVO reqVO) {
        return singleMissionMapper.retryIds(reqVO);
    }

    @Override
    public List<Long> executeIds(SingleMissionQueryReqVO reqVO) {
        switch (reqVO.getExecuteType()) {
            case "pre-settlement":
                return selectIds(reqVO);
            case "settlement":
                return selectSettlementIds(reqVO);
            case "retry":
                return retryIds(reqVO);
        }
        return null;
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
            exportVo.setContentPicture(pictureConvert(exportVo.getContentPicture()));
        });
        return exportVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SingleMissionRespVO refreshNote(RefreshNoteDetailReqVO reqVO) {
        SingleMissionDO missionDO = getByUid(reqVO.getUid());
        if (SingleMissionStatusEnum.init.getCode().equals(missionDO.getStatus())) {
            throw exception(CAN_NOT_REFRESH);
        }
        SingleMissionRespVO missionRespVO = SingleMissionConvert.INSTANCE.convert(missionDO);
        XhsNoteDetailRespVO noteDetail = noteDetailService.remoteDetail(reqVO.getPublishUrl());
        missionRespVO.getContent().validPostingContent(noteDetail);
        SingleMissionRespVO respVO = SingleMissionConvert.INSTANCE.convert(missionDO);
        BigDecimal amount = missionRespVO.getUnitPrice().calculationAmount(noteDetail.getLikedCount(), noteDetail.getCommentCount());
        respVO.setLikedCount(noteDetail.getLikedCount());
        respVO.setCommentCount(noteDetail.getCommentCount());
        respVO.setEstimatedAmount(amount);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void preSettlement(SingleMissionRespVO singleMissionRespVO) {
        try {
            preSettlement0(singleMissionRespVO);
        } catch (Exception e) {
            log.warn("预结算异常 {}", singleMissionRespVO.getUid(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settlement(SingleMissionRespVO singleMissionRespVO) {
        try {
            if (SingleMissionStatusEnum.published.getCode().equals(singleMissionRespVO.getStatus())
                    && SingleMissionStatusEnum.pre_settlement_error.getCode().equals(singleMissionRespVO.getStatus())) {
                // 预结算
                preSettlement(singleMissionRespVO);
                return;
            }
            int amount = singleMissionRespVO.getEstimatedAmount().intValue();
            String claimUserId = singleMissionRespVO.getClaimUserId();
            Assert.notBlank(claimUserId, "认领人id不存在");
            MemberUserDO user = memberUserService.getUser(Long.valueOf(claimUserId));
            Assert.notNull(user, "认领人不存在");
            TenantContextHolder.setIgnore(false);
            TenantContextHolder.setTenantId(user.getTenantId());
            memberPointRecordService.createPointRecord(user.getId(), amount, MemberPointBizTypeEnum.MISSION_SETTLEMENT, singleMissionRespVO.getUid());
            TenantContextHolder.clear();
            TenantContextHolder.setIgnore(true);
            updateSettlement(singleMissionRespVO.getUid(), singleMissionRespVO.getEstimatedAmount());
        } catch (Exception e) {
            log.warn("结算异常 {}", singleMissionRespVO.getUid(), e);
            throw e;
        } finally {
            TenantContextHolder.clear();
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
                    || SingleMissionStatusEnum.claimed.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.pre_settlement.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.settlement.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.pre_settlement_error.getCode().equals(missionDO.getStatus())
                    || SingleMissionStatusEnum.settlement_error.getCode().equals(missionDO.getStatus())) {
                throw exception(DONT_ALLOW_DELETE);
            }
        }
        List<String> creativeUids = singleMissionDOList.stream().map(SingleMissionDO::getCreativeUid).collect(Collectors.toList());
        creativeContentService.batchUnbind(creativeUids);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retry(Long singleMissionId) {
        SingleMissionRespVO singleMissionRespVO = getById(singleMissionId);
        NotificationCenterDO notificationCenterDO = notificationCenterService.getByUid(singleMissionRespVO.getNotificationUid());
        if (LocalDateTimeUtils.beforeNow(notificationCenterDO.getEndTime())) {
            // 结算
            settlement(singleMissionRespVO);
        } else {
            // 预结算
            preSettlement(singleMissionRespVO);
        }
    }

    private String pictureConvert(String str) {
        if (StringUtils.isBlank(str)) {
            return StringUtils.EMPTY;
        }
        List<ImageContent> list = JSONUtil.parseArray(str).toList(ImageContent.class);
        if (CollectionUtils.isEmpty(list)) {
            return StringUtils.EMPTY;
        }
        StringJoiner sj = new StringJoiner(StringUtils.LF);
        for (ImageContent creativeImageDTO : list) {
            sj.add(creativeImageDTO.getUrl());
        }
        return sj.toString();
    }

    private void preSettlement0(SingleMissionRespVO singleMissionRespVO) {
        XhsNoteDetailRespVO noteDetail = noteDetailService.preSettlementByUrl(singleMissionRespVO.getUid(), singleMissionRespVO.getPublishUrl(), singleMissionRespVO.getUnitPrice());
        // 校验note内容
        singleMissionRespVO.getContent().validPostingContent(noteDetail);
        updatePreSettlement(singleMissionRespVO.getUid(), noteDetail.getAmount(), noteDetail.getId());
    }

    private void updateSettlement(String uid, BigDecimal amount) {
        SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
        modifyReqVO.setUid(uid);
        modifyReqVO.setSettlementTime(LocalDateTime.now());
        modifyReqVO.setSettlementAmount(amount);
        modifyReqVO.setStatus(SingleMissionStatusEnum.complete.getCode());
        modifyReqVO.setRunTime(LocalDateTime.now());
        modifyReqVO.setSettlementMsg(StringUtils.EMPTY);
        update(modifyReqVO);
    }

    private void updatePreSettlement(String uid, BigDecimal amount, Long noteDetailId) {
        SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
        modifyReqVO.setUid(uid);
        modifyReqVO.setPreSettlementTime(LocalDateTime.now());
        modifyReqVO.setEstimatedAmount(amount);
        modifyReqVO.setStatus(SingleMissionStatusEnum.pre_settlement.getCode());
        modifyReqVO.setRunTime(LocalDateTime.now());
        modifyReqVO.setNoteDetailId(noteDetailId);
        modifyReqVO.setPreSettlementMsg(StringUtils.EMPTY);
        update(modifyReqVO);
    }

    private void validBudget(BigDecimal singleBudget, BigDecimal notificationBudget, Integer missionSize) {
        if (notificationBudget == null) {
            return;
        }

        if (singleBudget == null) {
            throw exception(BUDGET_ERROR);
        }

        if (singleBudget != null
                && NumberUtil.isLess(notificationBudget, singleBudget)) {
            throw exception(BUDGET_ERROR);
        }

        if (singleBudget != null
                && NumberUtil.isGreater(singleBudget.multiply(BigDecimal.valueOf(missionSize)), notificationBudget)) {
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
