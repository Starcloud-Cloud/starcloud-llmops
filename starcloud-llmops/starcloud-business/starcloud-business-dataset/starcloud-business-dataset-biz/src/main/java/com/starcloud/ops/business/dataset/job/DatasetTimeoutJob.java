package com.starcloud.ops.business.dataset.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import com.starcloud.ops.business.dataset.service.segment.DocumentSegmentsService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 数据集 定时任务
 *
 * @author Alan Cusack
 * 数据源定时刷新
 */
@Component
public class DatasetTimeoutJob implements JobHandler {

    @Resource
    private DocumentSegmentsService documentSegmentsService;


    /**
     * 执行任务
     *
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Override
    public String execute(String param) throws Exception {

        return null;
    }
}
