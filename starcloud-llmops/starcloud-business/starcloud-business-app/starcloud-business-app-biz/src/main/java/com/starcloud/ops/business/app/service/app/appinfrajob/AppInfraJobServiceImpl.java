package com.starcloud.ops.business.app.service.app.appinfrajob;

import cn.hutool.cron.CronUtil;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobPageReqVO;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.appinfrajob.AppInfraJobDO;
import com.starcloud.ops.business.app.dal.mysql.appinfrajob.AppInfraJobMapper;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 应用定时执行任务 Service 实现类
 *
 * @author starcloudadmin
 */
@Service
@Validated
public class AppInfraJobServiceImpl implements AppInfraJobService {

    @Resource
    private AppInfraJobMapper appInfraJobMapper;

    @Override
    public Long createAppInfraJob(AppInfraJobSaveReqVO createReqVO) {
        // 插入
        AppInfraJobDO appInfraJob = BeanUtils.toBean(createReqVO, AppInfraJobDO.class);
        appInfraJobMapper.insert(appInfraJob);
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
    }

    @Override
    public void deleteAppInfraJob(Long id) {
        // 校验存在
        validateAppInfraJobExists(id);
        // 删除
        appInfraJobMapper.deleteById(id);
    }

    private void validateAppInfraJobExists(Long id) {
        if (appInfraJobMapper.selectById(id) == null) {
            // throw exception(APP_INFRA_JOB_NOT_EXISTS);
        }
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
     * 触发定时任务
     *
     * @param id
     * @throws SchedulerException
     */
    @Override
    public void triggerJob(Long id) throws SchedulerException {
        CronUtil.getScheduler()
    }

}