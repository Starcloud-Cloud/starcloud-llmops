package com.starcloud.ops.business.app.service.xhs.batch.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.convert.xhs.batch.CreativePlanBatchConvert;
import com.starcloud.ops.business.app.dal.databoject.xhs.batch.CreativePlanBatchDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.xhs.batch.CreativePlanBatchMapper;
import com.starcloud.ops.business.app.enums.xhs.batch.CreativePlanBatchStatusEnum;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.PLAN_BATCH_NOT_EXIST;

@Slf4j
@Service
public class CreativePlanBatchServiceImpl implements CreativePlanBatchService {

    @Resource
    private CreativePlanBatchMapper batchMapper;

    @Resource
    private CreativeContentService contentService;

    @Resource
    private CreativeSchemeService schemeService;

    @Override
    public void createBatch(Long batch, CreativePlanRespVO creativePlan) {
        String schemeUidList = creativePlan.getConfiguration().getSchemeUid();
        CreativeSchemeRespVO schemeRespVOList = schemeService.get(schemeUidList);

        CreativePlanBatchDO batchDO = new CreativePlanBatchDO();
        batchDO.setPlanUid(creativePlan.getUid());
        batchDO.setBatch(batch);
        batchDO.setCreativePlan(creativePlan);
        batchDO.setSchemeConfig(schemeRespVOList);
        batchDO.setStartTime(LocalDateTime.now());
        batchDO.setStatus(CreativePlanBatchStatusEnum.RUNNING.name());
        batchDO.setTotalCount(creativePlan.getTotal());
        batchMapper.insert(batchDO);
    }

    @Override
    public void updateCompleteStatus(String planUid, Long batch) {
        CreativePlanBatchDO creativePlanBatchDO = selectBatch(planUid, batch);
        List<CreativeContentDO> creativeContentDOList = contentService.listByPlanUid(planUid, batch);

        int successCount = 0, failureCount = 0;
        boolean complete = true;
        for (CreativeContentDO creativeContentDO : creativeContentDOList) {
            if (CreativeContentStatusEnum.EXECUTE_SUCCESS.getCode().equals(creativeContentDO.getStatus())) {
                successCount++;
            } else if (CreativeContentStatusEnum.EXECUTE_ERROR_FINISHED.getCode().equals(creativeContentDO.getStatus())) {
                failureCount++;
            } else {
                // 批次未全部执行结束
                complete = false;
            }
        }
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(creativePlanBatchDO.getStartTime(), now);
        creativePlanBatchDO.setSuccessCount(successCount);
        creativePlanBatchDO.setFailureCount(failureCount);
        if (!complete) {
            creativePlanBatchDO.setStatus(CreativePlanBatchStatusEnum.RUNNING.name());
        } else if (failureCount == 0) {
            creativePlanBatchDO.setStatus(CreativePlanBatchStatusEnum.SUCCESS.name());
        } else {
            creativePlanBatchDO.setStatus(CreativePlanBatchStatusEnum.FAILURE.name());
        }
        creativePlanBatchDO.setEndTime(now);
        creativePlanBatchDO.setElapsed(duration.toMillis());
        creativePlanBatchDO.setUpdateTime(now);
        batchMapper.updateById(creativePlanBatchDO);
    }

    @Override
    public PageResult<CreativePlanBatchRespVO> page(CreativePlanBatchPageReqVO pageReqVO) {
        PageResult<CreativePlanBatchDO> page = batchMapper.page(pageReqVO);
        return CreativePlanBatchConvert.INSTANCE.convert(page);
    }

    @Override
    public List<CreativePlanBatchRespVO> latestBatch(List<String> planUidList) {
        List<CreativePlanBatchDO> creativePlanBatchDOList = batchMapper.latestBatch(planUidList);
        return CreativePlanBatchConvert.INSTANCE.convert(creativePlanBatchDOList);
    }

    private CreativePlanBatchDO selectBatch(String planUid, Long batch) {
        CreativePlanBatchDO creativePlanBatchDO = batchMapper.selectBatch(planUid, batch);
        if (Objects.isNull(creativePlanBatchDO)) {
            throw exception(PLAN_BATCH_NOT_EXIST, planUid, batch);
        }
        return creativePlanBatchDO;
    }
}
