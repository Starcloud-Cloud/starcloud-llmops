package com.starcloud.ops.business.user.job.notify;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import com.starcloud.ops.business.user.controller.admin.notify.vo.CreateNotifyReqVO;
import com.starcloud.ops.business.user.service.notify.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class NotifyTaskMapReduce extends BaseMapReduceTask {

    @Resource
    private NotifyService notifyService;

    @Override
    protected BaseTaskResult execute(PowerJobTaskContext baseTaskContext) {
//        TenantContextHolder.setIgnore(true);
        if (isRootTask()) {
            return runRoot(baseTaskContext);
        }
        if (baseTaskContext.getTaskContext().getSubTask() instanceof CreateNotifyReqVO) {
            CreateNotifyReqVO subTask = (CreateNotifyReqVO) baseTaskContext.getTaskContext().getSubTask();
            return runSub(subTask);
        }
        return new BaseTaskResult(false, "UNKNOWN_BUG_taskName_" + baseTaskContext.getTaskContext().getTaskName());
    }

    @Override
    protected BaseTaskResult runRoot(PowerJobTaskContext baseTaskContext) {
        String params = baseTaskContext.getParams();
        if (StringUtils.isBlank(params)) {
            return new BaseTaskResult(true, "ROOT_PROCESS_SUCCESS");
        }
        try {
            List<CreateNotifyReqVO> list = JSONUtil.parseArray(params).toList(CreateNotifyReqVO.class);
            String batchCode = String.valueOf(System.currentTimeMillis());
            for (CreateNotifyReqVO createNotifyReqVO : list) {
                createNotifyReqVO.setBatchCode(batchCode);
            }
            map(list, "sub_notify_task");
        } catch (Exception e) {
            log.warn("ROOT_PROCESS_FILE is fail: {}", e.getMessage(), e);
            return new BaseTaskResult(false, "ROOT_PROCESS_FILE:" + e.getMessage());
        }
        return new BaseTaskResult(true, "ROOT_PROCESS_SUCCESS");
    }

    public BaseTaskResult runSub(CreateNotifyReqVO createNotifyReqVO) {
        notifyService.createMsgTask(createNotifyReqVO);
        return new BaseTaskResult(true, "SUB_PROCESS_SUCCESS");
    }

    @Override
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {
        TenantContextHolder.setIgnore(true);
        return super.reduce(taskContext, taskResults);
    }
}
