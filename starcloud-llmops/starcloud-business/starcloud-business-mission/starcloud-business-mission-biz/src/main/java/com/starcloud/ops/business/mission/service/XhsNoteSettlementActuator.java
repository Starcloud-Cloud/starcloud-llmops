package com.starcloud.ops.business.mission.service;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsNoteDetailRespVO;
import com.starcloud.ops.business.app.service.xhs.XhsNoteDetailService;
import com.starcloud.ops.business.dto.PostingContentDTO;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.NotificationRespVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class XhsNoteSettlementActuator {

    @Resource
    private XhsNoteDetailService noteDetailService;

    @Resource
    private NotificationCenterService notificationCenterService;

    @Resource
    private SingleMissionService singleMissionService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 小红书发帖任务结算
     *
     * @param singleMissionId 单个任务Id
     */
    public void settlement(Long singleMissionId) {
        RLock lock = redissonClient.getLock("settlement-" + singleMissionId);
        if (!lock.tryLock()) {
            log.warn("{} 正在结算中", singleMissionId);
            return;
        }
        String missionUid = StringUtils.EMPTY;
        try {
            SingleMissionRespVO singleMissionRespVO = singleMissionService.getById(singleMissionId);
            if (!SingleMissionStatusEnum.published.getCode().equals(singleMissionRespVO.getStatus())
                    && !SingleMissionStatusEnum.pre_settlement.getCode().equals(singleMissionRespVO.getStatus())) {
                log.info("{} 状态不允许结算 {}", singleMissionId, singleMissionRespVO.getSettlementAmount());
                return;
            }

            if (LocalDateTimeUtils.afterYesterday(singleMissionRespVO.getPreSettlementTime())
                    || LocalDateTimeUtils.afterYesterday(singleMissionRespVO.getSettlementTime())) {
                log.info("{} 今天已结算", singleMissionId);
                return;
            }

            missionUid = singleMissionRespVO.getUid();
            NotificationRespVO notificationRespVO = notificationCenterService.selectByUid(singleMissionRespVO.getNotificationUid());
            String publishUrl = singleMissionRespVO.getPublishUrl();
            XhsNoteDetailRespVO noteDetail = noteDetailService.refreshByNoteUrl(publishUrl);
            // 校验note内容
            validPostingContent(singleMissionRespVO.getContent(), noteDetail);

            BigDecimal amount = calculationAmount(noteDetail, notificationRespVO);
            updateSingleMission(singleMissionRespVO.getUid(), amount, notificationRespVO.getEndTime());
        } catch (Exception e) {
            log.warn("结算异常", e);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setStatus(SingleMissionStatusEnum.settlement_error.getCode());
            modifyReqVO.setUid(missionUid);
            modifyReqVO.setRunTime(LocalDateTime.now());
            singleMissionService.update(modifyReqVO);
        } finally {
            lock.unlock();
        }
    }

    private void updateSingleMission(String uid, BigDecimal amount, LocalDateTime endTime) {
        SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
        modifyReqVO.setUid(uid);
        // 结束时间是昨天 做最终结算
        if (LocalDateTimeUtils.isYesterday(endTime)) {
            modifyReqVO.setSettlementTime(LocalDateTime.now());
            modifyReqVO.setSettlementAmount(amount);
            modifyReqVO.setStatus(SingleMissionStatusEnum.settlement.getCode());
        } else {
            modifyReqVO.setPreSettlementTime(LocalDateTime.now());
            modifyReqVO.setEstimatedAmount(amount);
            modifyReqVO.setStatus(SingleMissionStatusEnum.pre_settlement.getCode());
        }
        modifyReqVO.setRunTime(LocalDateTime.now());
        singleMissionService.update(modifyReqVO);
    }

    private void validPostingContent(PostingContentDTO content, XhsNoteDetailRespVO noteDetail) {

    }

    private BigDecimal calculationAmount(XhsNoteDetailRespVO noteDetail, NotificationRespVO notificationRespVO) {
        if (notificationRespVO.getUnitPrice() == null) {
            return BigDecimal.ZERO;
        }
        return notificationRespVO.getUnitPrice().calculationAmount(noteDetail, notificationRespVO.getSingleBudget());
    }

}
