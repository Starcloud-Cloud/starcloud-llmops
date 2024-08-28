package com.starcloud.ops.business.job.biz.powerjob;

import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import com.starcloud.ops.business.job.biz.enums.BusinessJobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.enums.DispatchStrategy;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.model.LifeCycle;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.ResultDTO;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.job.biz.enums.JobErrorCodeConstants.REQUEST_POWERJOB_ERROR;

@Slf4j
@Component
public class PowerjobManager {

    @Resource
    private PowerJobClient powerJobClient;

    /**
     * 新建powerJob任务 默认启用
     *
     * @param jobId 不为空则修改任务
     * @return
     */
    public Long saveJob(BusinessJobBaseVO businessJob, Long jobId) {
        SaveJobInfoRequest request = new SaveJobInfoRequest();
        request.setJobName(businessJob.getName());
        BusinessJobTypeEnum businessJobTypeEnum = BusinessJobTypeEnum.valueOf(StringUtils.isBlank(businessJob.getBusinessJobType()) ? BusinessJobTypeEnum.coze_standalone.name() : businessJob.getBusinessJobType());

        String desc = String.format("ForeignKey=%s \n BusinessJobType=%s", businessJob.getForeignKey(), businessJobTypeEnum.getDesc());
        request.setJobDescription(desc);
        request.setTimeExpression(businessJob.getTimeExpression().trim());
        request.setTimeExpressionType(TimeExpressionType.of(businessJob.getTimeExpressionType()));

        LifeCycle lifeCycle = new LifeCycle();
        lifeCycle.setStart(businessJob.getLifecycleStart());
        lifeCycle.setEnd(businessJob.getLifecycleEnd());
        request.setLifeCycle(lifeCycle);

        request.setExecuteType(businessJobTypeEnum.getExecuteType());
        request.setProcessorType(ProcessorType.BUILT_IN);
        request.setProcessorInfo(businessJobTypeEnum.getReference().getName());
        request.setDispatchStrategy(DispatchStrategy.HEALTH_FIRST);
        request.setEnable(BooleanUtils.isNotFalse(businessJob.getEnable()));
        // 最大实例个数
        request.setMaxInstanceNum(1);
        request.setInstanceTimeLimit(310000L);
        request.setTaskRetryNum(0);
        request.setId(jobId);

        ResultDTO<Long> result = powerJobClient.saveJob(request);
        if (!result.isSuccess()) {
            throw exception(REQUEST_POWERJOB_ERROR, "保存powerJob任务", result.getMessage());
        }
        return result.getData();
    }

    /**
     * 删除powerjob任务
     */
    public void deleteJob(Long jobId) {
        ResultDTO<Void> result = powerJobClient.deleteJob(jobId);
        if (!result.isSuccess()) {
            throw exception(REQUEST_POWERJOB_ERROR, "删除powerjob任务", result.getMessage());
        }
    }

    /**
     * 禁用任务
     */
    public void disable(Long jobId) {
        ResultDTO<Void> result = powerJobClient.disableJob(jobId);
        if (!result.isSuccess()) {
            throw exception(REQUEST_POWERJOB_ERROR, "禁用任务", result.getMessage());
        }
    }

    /**
     * 启用任务
     */
    public void enable(Long jobId) {
        ResultDTO<Void> result = powerJobClient.enableJob(jobId);
        if (!result.isSuccess()) {
            throw exception(REQUEST_POWERJOB_ERROR, "启用任务", result.getMessage());
        }
    }

    /**
     * 触发任务执行
     *
     * @param params 执行参数
     * @param delay  延迟
     */
    public void runJob(Long jobId, String params, long delay) {
        ResultDTO<Long> result = powerJobClient.runJob(jobId, params, delay);
        if (!result.isSuccess()) {
            throw exception(REQUEST_POWERJOB_ERROR, "执行任务", result.getMessage());
        }
    }

}
