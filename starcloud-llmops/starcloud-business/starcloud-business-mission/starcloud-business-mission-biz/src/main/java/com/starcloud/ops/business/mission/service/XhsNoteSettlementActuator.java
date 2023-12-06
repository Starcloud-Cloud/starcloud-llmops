package com.starcloud.ops.business.mission.service;

import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import com.starcloud.ops.business.enums.SingleMissionStatusEnum;
import com.starcloud.ops.business.mission.controller.admin.vo.response.SingleMissionRespVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class XhsNoteSettlementActuator {


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
        try {
            SingleMissionRespVO singleMissionRespVO = singleMissionService.getById(singleMissionId);
            if (!SingleMissionStatusEnum.published.getCode().equals(singleMissionRespVO.getStatus())
                    && !SingleMissionStatusEnum.pre_settlement_error.getCode().equals(singleMissionRespVO.getStatus())
                    && !SingleMissionStatusEnum.pre_settlement.getCode().equals(singleMissionRespVO.getStatus())) {
                log.info("{} 状态不允许结算 {}", singleMissionId, singleMissionRespVO.getStatus());
                return;
            }

            if (LocalDateTimeUtils.afterYesterday(singleMissionRespVO.getPreSettlementTime())
                    || LocalDateTimeUtils.afterYesterday(singleMissionRespVO.getSettlementTime())) {
                log.info("{} 今天已结算", singleMissionId);
                return;
            }
            singleMissionService.settlement(singleMissionRespVO);
        } catch (Exception e) {
            log.warn("结算异常", e);
        } finally {
            lock.unlock();
        }
    }


}
