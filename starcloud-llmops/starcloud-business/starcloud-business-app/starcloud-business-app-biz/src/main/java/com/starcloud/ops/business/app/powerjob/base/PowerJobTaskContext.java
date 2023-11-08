package com.starcloud.ops.business.app.powerjob.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.log.OmsLogger;

/**
 * 基础任务 上下文
 */
@Slf4j
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerJobTaskContext extends BaseTaskContext {

    private TaskContext taskContext;

    public void setTaskContext(TaskContext taskContext) {
        this.taskContext = taskContext;
    }

    @Override
    protected OmsLogger getLog() {
        return taskContext.getOmsLogger();
    }


    @Override
    public void setParams(String str) {
        taskContext.setJobParams(str);
    }


    @Override
    public String getParams() {
        return taskContext.getJobParams();
    }

    @Override
    protected Long getInstanceId() {
        return taskContext.getInstanceId();
    }

    @Override
    protected String getInstanceParams() {
        return taskContext.getInstanceParams();
    }

}
