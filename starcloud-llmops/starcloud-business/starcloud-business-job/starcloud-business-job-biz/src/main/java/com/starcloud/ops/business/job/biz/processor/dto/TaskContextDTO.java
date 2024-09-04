package com.starcloud.ops.business.job.biz.processor.dto;

import com.starcloud.ops.business.job.biz.dal.dataobject.BusinessJobDO;
import lombok.Data;
import tech.powerjob.worker.core.processor.TaskContext;

@Data
public class TaskContextDTO extends TaskContext {

    private BusinessJobDO businessJobDO;


    public TaskContextDTO(BusinessJobDO businessJobDO, TaskContext taskContext) {
        this.businessJobDO = businessJobDO;
        this.setOmsLogger(taskContext.getOmsLogger());
        this.setJobId(taskContext.getJobId());
        this.setInstanceId(taskContext.getInstanceId());
        this.setInstanceParams(taskContext.getInstanceParams());
        this.setUserContext(taskContext.getUserContext());
    }
}
