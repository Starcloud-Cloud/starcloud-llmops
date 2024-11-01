package com.starcloud.ops.business.job.biz.processor;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.exception.ServerException;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.job.biz.controller.admin.vo.JobLogBaseVO;
import com.starcloud.ops.business.job.biz.controller.admin.vo.response.BusinessJobRespVO;
import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import com.starcloud.ops.business.job.biz.powerjob.PowerjobManager;
import com.starcloud.ops.business.job.biz.processor.dto.CozeProcessResultDTO;
import com.starcloud.ops.business.job.biz.processor.dto.TaskContextDTO;
import com.starcloud.ops.business.job.biz.service.BusinessJobLogService;
import com.starcloud.ops.business.job.biz.service.BusinessJobService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 单机执行BasicProcessor
 * 日志 异常处理
 */
@Slf4j
public abstract class StandaloneBasicProcessor implements BasicProcessor {


    @Resource
    private BusinessJobService businessJobService;

    @Resource
    private BusinessJobLogService jobLogService;

    @Resource
    private RedissonClient redissonClient;

    @Autowired(required = false)
    private PowerjobManager powerjobManager;

    @Override
    @DataPermission(enable = false)
    @TenantIgnore
    public ProcessResult process(TaskContext context) {
        LocalDateTime now = LocalDateTime.now();
        long start = System.currentTimeMillis();
        OmsLogger omsLogger = context.getOmsLogger();
        Long jobId = context.getJobId();
        BusinessJobDO businessJobDO = businessJobService.getByJobId(jobId);

        RLock lock = redissonClient.getLock("job_lock" + jobId);
        if (BooleanUtils.isNotTrue(businessJobDO.getEnable())) {
            // 关闭powerjob task
            powerjobManager.disable(jobId);
            logError(omsLogger, "任务已关闭，businessJobId={}, jobId={}", businessJobDO.getId(), jobId);
            return new ProcessResult(true, "任务已关闭，businessJobId=" + businessJobDO.getId() + ", jobId=" + jobId);
        }

        try {
            if (lock.tryLock(2, 2, TimeUnit.SECONDS)) {
                BusinessJobRespVO byForeignKey = businessJobService.getByForeignKey(businessJobDO.getForeignKey());
                Integer remainCount = businessJobDO.getRemainCount();
                if (Objects.isNull(remainCount) || remainCount < 1) {
                    //  关闭任务
                    businessJobService.stop(byForeignKey.getUid());
                    logError(omsLogger, "任务超过调用次数限制，businessJobId={}, jobId={}", businessJobDO.getId(), jobId);
                    return new ProcessResult(true, "任务超过调用次数限制，businessJobId=" + businessJobDO.getId() + ", jobId=" + jobId);
                } else if (remainCount == 1) {
                    businessJobService.stop(byForeignKey.getUid());
                    logInfo(omsLogger, "最后一次调用");
                }
                // 更新次数
                businessJobService.decreaseNum(byForeignKey.getUid());
            }
        } catch (Exception e) {
            logError(omsLogger, "任务调用异常，businessJobId={}, jobId={}", businessJobDO.getId(), jobId);
            return new ProcessResult(false, "启动任务异常：" + e.getMessage() + "，businessJobId=" + businessJobDO.getId() + ", jobId=" + jobId);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        logInfo(omsLogger, "start execute businessJobId={}, retryTime={}", businessJobDO.getId(), context.getCurrentRetryTimes());

        String instanceParams = context.getInstanceParams();
        if (StringUtils.isNotBlank(instanceParams)) {
            // 手动触发 使用临时运行参数做为执行参数
            businessJobDO.setConfig(instanceParams);
        }
        try {
            TenantContextHolder.setTenantId(businessJobDO.getTenantId());
            TenantContextHolder.setIgnore(false);
            UserContextHolder.setUserId(Long.valueOf(businessJobDO.getCreator()));
            CozeProcessResultDTO result = actualProcess(new TaskContextDTO(businessJobDO, context));
            long end = System.currentTimeMillis();
            JobLogBaseVO logBaseVO = new JobLogBaseVO();
            logBaseVO.setJobId(jobId);
            logBaseVO.setBusinessJobUid(businessJobDO.getUid());
            logBaseVO.setExecuteConfig(businessJobDO.getConfig());
            logBaseVO.setSuccess(true);
            logBaseVO.setExecuteResult(JSONUtil.toJsonStr(result));
            logBaseVO.setExecuteTime(end - start);
            logBaseVO.setTriggerTime(now);
            logBaseVO.setTriggerType(businessJobDO.getTimeExpressionType());
            Long logId = jobLogService.recordLog(logBaseVO);
            logInfo(omsLogger, "execute success logId={} , {} ms", logId, end - start);
            return result;
        } catch (Exception e) {
            JobLogBaseVO logBaseVO = new JobLogBaseVO();
            logBaseVO.setJobId(jobId);
            logBaseVO.setBusinessJobUid(businessJobDO.getUid());
            logBaseVO.setExecuteConfig(businessJobDO.getConfig());
            logBaseVO.setSuccess(false);
            logBaseVO.setExecuteResult(e.getMessage());
            logBaseVO.setTriggerTime(now);
            logBaseVO.setTriggerType(businessJobDO.getTimeExpressionType());
            Long logId = jobLogService.recordLog(logBaseVO);
            logError(omsLogger, "执行coze插件失败 logId={}, businessJobId={}", logId, businessJobDO.getId(), e);
            return new ProcessResult(false, e.getMessage());
        } finally {
            TenantContextHolder.clear();
            UserContextHolder.clear();
        }
    }

    /**
     * 执行单机任务
     */
    abstract CozeProcessResultDTO actualProcess(TaskContextDTO taskContextDTO);

    public void logInfo(OmsLogger omsLogger, String messagePattern, Object... args) {
        log.info(messagePattern, args);
        omsLogger.info(messagePattern, args);
    }

    public void logError(OmsLogger omsLogger, String messagePattern, Object... args) {
        log.error(messagePattern, args);
        omsLogger.error(messagePattern, args);
    }

}
