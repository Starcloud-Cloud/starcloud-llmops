package com.starcloud.ops.business.app.service.appinfrajob;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.Scheduler;
import cn.hutool.cron.pattern.CronPattern;
import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.quartz.core.scheduler.SchedulerManager;
import cn.iocoder.yudao.framework.quartz.core.util.CronUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobPageReqVO;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.appinfrajob.AppInfraJobDO;
import com.starcloud.ops.business.app.dal.mysql.appinfrajob.AppInfraJobMapper;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.JOB_CRON_EXPRESSION_VALID;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.APP_INFRA_JOB_ADD_FAIL_EXISTS;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.APP_INFRA_JOB_NOT_EXISTS;

/**
 * 应用定时执行任务 Service 实现类
 *
 * @author starcloudadmin
 */
@Slf4j
@Service
@Validated
public class AppInfraJobServiceImpl implements AppInfraJobService {

    @Resource
    private CreativePlanService creativePlanService;

    @Resource
    private AppInfraJobMapper appInfraJobMapper;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Long createAppInfraJob(AppInfraJobSaveReqVO createReqVO) {
        // 校验 CRON 表达式是否有效
        validateCronExpression(createReqVO.getCronExpression());

        //校验 任务是否存在
        validateAppInfraJob(createReqVO);
        // 插入
        AppInfraJobDO appInfraJob = BeanUtils.toBean(createReqVO, AppInfraJobDO.class);
        appInfraJobMapper.insert(appInfraJob);

        // 添加任务到 scheduleJob
        scheduleJob(appInfraJob);

        // 返回
        return appInfraJob.getId();
    }



    @Override
    public void updateAppInfraJob(AppInfraJobSaveReqVO updateReqVO) {
        // 校验存在
        validateAppInfraJobExists(updateReqVO.getId());
        // 更新
        AppInfraJobDO updateObj = BeanUtils.toBean(updateReqVO, AppInfraJobDO.class);
        appInfraJobMapper.updateById(updateObj);

        if (Objects.nonNull(updateObj.getCronExpression()) && Objects.nonNull(updateObj.getCreativePlanUid()))
            // 更新任务
            CronUtil.updatePattern(updateObj.getCreativePlanUid(), CronPattern.of(updateObj.getCronExpression()));
    }

    @Override
    public void deleteAppInfraJob(Long id) {
        // 校验存在
        validateAppInfraJobExists(id);
        // 移除任务
        boolean remove = CronUtil.remove(this.getAppInfraJob(id).getCreativePlanUid());
        log.info("【创作计划定时执行任务删除{}】", remove ? "成功" : "失败，任务不存在");
        // 删除
        appInfraJobMapper.deleteById(id);

        CronUtil.restart();
    }



    @Override
    public AppInfraJobDO getAppInfraJob(Long id) {
        return appInfraJobMapper.selectById(id);
    }

    @Override
    public PageResult<AppInfraJobDO> getAppInfraJobPage(AppInfraJobPageReqVO pageReqVO) {
        return appInfraJobMapper.selectPage(pageReqVO);
    }

    /**
     * 定时任务检测
     */
    @Override
    public void infraJobCheck() {
        // 1.当前启动器是否启动；
        Scheduler scheduler = CronUtil.getScheduler();
        // 未启动 启动
        if (!scheduler.isStarted()) {
            scheduler.start(true);
        }
        // 2.判断当前定时器是否因为重启被清空
        List<AppInfraJobDO> appInfraJobDOS = appInfraJobMapper.selectEnableList();
        if (appInfraJobDOS.isEmpty()){
            return;
        }
        if (scheduler.isEmpty()){
            appInfraJobDOS.forEach(this::scheduleJob);
        }
    }

    /**
     * 根据创作计划编号验证任务是否存在
     * @param createReqVO createReqVO
     */
    private void validateAppInfraJob(AppInfraJobSaveReqVO createReqVO) {
        if (appInfraJobMapper.selectByCreativePlanUid(createReqVO.getCreativePlanUid()) != null) {
            throw exception(APP_INFRA_JOB_ADD_FAIL_EXISTS);
        }
    }

    private void validateAppInfraJobExists(Long id) {
        if (appInfraJobMapper.selectById(id) == null) {
            throw exception(APP_INFRA_JOB_NOT_EXISTS);
        }
    }

    private void scheduleJob(AppInfraJobDO appInfraJobDO) {
        // CronUtil.stop();
        log.info("【添加新的应用定时任务，开始添加到计划执行定时任务中】");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Long userId = Long.valueOf(appInfraJobDO.getCreator());
        Long tenantId = appInfraJobDO.getTenantId();

        Authentication authentication = SecurityFrameworkUtils.getAuthentication();

        //        Scheduler scheduler = CronUtil.getScheduler();
        //         boolean empty = scheduler.isEmpty();

        CronUtil.schedule(appInfraJobDO.getCreativePlanUid(), appInfraJobDO.getCronExpression(), () -> {
            try {
                TenantContextHolder.setIgnore(false);
                TenantContextHolder.setTenantId(tenantId);

                RequestContextHolder.setRequestAttributes(requestAttributes, true);
                SecurityFrameworkUtils.setAuthentication(authentication);

                UserContextHolder.setUserId(userId);
                WebFrameworkUtils.setLoginUserId(httpServletRequest, userId);
                creativePlanService.execute(appInfraJobDO.getCreativePlanUid());
                UserContextHolder.clear();
                TenantContextHolder.clear();
                RequestContextHolder.resetRequestAttributes();

                log.info("【创作计划编号为{}的定时执行，执行成功，】", appInfraJobDO.getCreativePlanUid());
            } catch (RuntimeException e) {
                log.error("【创作计划编号为{}的定时执行，执行失败，失败原因为:{}】", appInfraJobDO.getCreativePlanUid(), e.getMessage());
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);

        // CronUtil.start(true);
        log.info("【添加新的创作计划编号为定时任务，添加成功，按照设定规则开始执行】");
    }

    /**
     * 校验 CRON 表达式是否有效
     * @param cronExpression CRON 表达式
     */
    private void validateCronExpression(String cronExpression) {
        if (!CronUtils.isValid(cronExpression)) {
            throw exception(JOB_CRON_EXPRESSION_VALID);
        }
    }

}