package com.starcloud.ops.business.app.service.appinfrajob;

import javax.validation.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobPageReqVO;
import com.starcloud.ops.business.app.controller.admin.appinfrajob.vo.AppInfraJobSaveReqVO;
import com.starcloud.ops.business.app.dal.databoject.appinfrajob.AppInfraJobDO;
import org.quartz.SchedulerException;

/**
 * 应用定时执行任务 Service 接口
 *
 * @author starcloudadmin
 */
public interface AppInfraJobService {

    /**
     * 创建应用定时执行任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAppInfraJob(@Valid AppInfraJobSaveReqVO createReqVO);

    /**
     * 更新应用定时执行任务
     *
     * @param updateReqVO 更新信息
     */
    void updateAppInfraJob(@Valid AppInfraJobSaveReqVO updateReqVO);

    /**
     * 删除应用定时执行任务
     *
     * @param id 编号
     */
    void deleteAppInfraJob(Long id);

    /**
     * 获得应用定时执行任务
     *
     * @param id 编号
     * @return 应用定时执行任务
     */
    AppInfraJobDO getAppInfraJob(Long id);

    /**
     * 获得应用定时执行任务分页
     *
     * @param pageReqVO 分页查询
     * @return 应用定时执行任务分页
     */
    PageResult<AppInfraJobDO> getAppInfraJobPage(AppInfraJobPageReqVO pageReqVO);



    /**
     * 定时任务检测
     *
     */
   void infraJobCheck();



}