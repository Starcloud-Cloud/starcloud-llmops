package com.starcloud.ops.business.mission.service;

import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionModifyReqVO;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class XhsNoteSettlementActuator {


    @Resource
    private SingleMissionService singleMissionService;

    @Resource
    private RedissonClient redissonClient;

    public void execute(Long singleMissionId, String executeType) {
        switch (executeType) {
            case "pre-settlement":
                preSettlement(singleMissionId);
                break;
            case "settlement":
                settlement(singleMissionId);
                break;
            case "retry":
                retry(singleMissionId);
                break;
        }
    }

    /**
     * 小红书发帖任务预结算
     *
     * @param singleMissionId 单个任务Id
     */
    public void preSettlement(Long singleMissionId) {
        RLock lock = redissonClient.getLock("pre-settlement-" + singleMissionId);
        if (!lock.tryLock()) {
            log.warn("{} 正在预结算中", singleMissionId);
            return;
        }
        String uid = StringUtils.EMPTY;
        try {
            log.info("{} 开始预结算", singleMissionId);
            SingleMissionRespVO singleMissionRespVO = singleMissionService.getById(singleMissionId);
            if (!SingleMissionStatusEnum.published.getCode().equals(singleMissionRespVO.getStatus())
                    && !SingleMissionStatusEnum.pre_settlement.getCode().equals(singleMissionRespVO.getStatus())) {
                log.warn("{} 状态不允许预结算 {}", singleMissionId, singleMissionRespVO.getStatus());
                return;
            }
            uid = singleMissionRespVO.getUid();
            singleMissionService.preSettlement(singleMissionRespVO);
        } catch (Exception e) {
            log.warn("预结算异常", e);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setStatus(SingleMissionStatusEnum.pre_settlement_error.getCode());
            modifyReqVO.setUid(uid);
            modifyReqVO.setRunTime(LocalDateTime.now());
            modifyReqVO.setPreSettlementMsg(e.getMessage());
            modifyReqVO.setPreSettlementTime(LocalDateTime.now());
            singleMissionService.update(modifyReqVO);
        } finally {
            lock.unlock();
        }
    }

    public void settlement(Long singleMissionId) {
        RLock lock = redissonClient.getLock("settlement-" + singleMissionId);
        if (!lock.tryLock()) {
            log.warn("{} 正在结算中", singleMissionId);
            return;
        }
        String uid = StringUtils.EMPTY;
        try {
            log.info("{} 开始结算", singleMissionId);
            SingleMissionRespVO singleMissionRespVO = singleMissionService.getById(singleMissionId);
            if (SingleMissionStatusEnum.claimed.getCode().equals(singleMissionRespVO.getStatus())) {
                // 过期未发布关闭
                SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
                modifyReqVO.setUid(singleMissionRespVO.getUid());
                modifyReqVO.setStatus(SingleMissionStatusEnum.close.getCode());
                modifyReqVO.setCloseMsg("超时未发布链接");
                singleMissionService.update(modifyReqVO);
                return;
            }

            if (!SingleMissionStatusEnum.published.getCode().equals(singleMissionRespVO.getStatus())
                    && !SingleMissionStatusEnum.pre_settlement.getCode().equals(singleMissionRespVO.getStatus())) {
                log.warn("{} 状态不允许结算 {}", singleMissionId, singleMissionRespVO.getStatus());
                return;
            }
            uid = singleMissionRespVO.getUid();
            singleMissionService.settlement(singleMissionRespVO);
        } catch (Exception e) {
            log.warn("结算异常", e);
            SingleMissionModifyReqVO modifyReqVO = new SingleMissionModifyReqVO();
            modifyReqVO.setStatus(SingleMissionStatusEnum.settlement_error.getCode());
            modifyReqVO.setUid(uid);
            modifyReqVO.setRunTime(LocalDateTime.now());
            modifyReqVO.setSettlementMsg(e.getMessage());
            modifyReqVO.setSettlementTime(LocalDateTime.now());
            singleMissionService.update(modifyReqVO);
        } finally {
            lock.unlock();
        }
    }

    private void retry(Long singleMissionId) {
        RLock lock = redissonClient.getLock("retry-" + singleMissionId);
        if (!lock.tryLock()) {
            log.warn("{} 正在重试中", singleMissionId);
            return;
        }
        try {
            log.info("{} 开始重试", singleMissionId);
            singleMissionService.retry(singleMissionId);
        } catch (Exception e) {
            log.warn("重试异常", e);
        } finally {
            lock.unlock();
        }
    }


}
