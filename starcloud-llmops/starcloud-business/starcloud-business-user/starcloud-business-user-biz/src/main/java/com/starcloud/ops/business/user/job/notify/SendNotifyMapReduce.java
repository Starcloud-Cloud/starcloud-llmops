package com.starcloud.ops.business.user.job.notify;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.dal.dataobject.notify.NotifyMessageDO;
import cn.iocoder.yudao.module.system.service.notify.NotifyMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import com.starcloud.ops.business.user.service.notify.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SendNotifyMapReduce extends BaseMapReduceTask {

    @Resource
    private NotifyService notifyService;

    @Resource
    private NotifyMessageService messageService;

    @Override
    protected BaseTaskResult execute(PowerJobTaskContext baseTaskContext) {
//        TenantContextHolder.setIgnore(true);
        if (isRootTask()) {
            return runRoot(baseTaskContext);
        }
        if (baseTaskContext.getTaskContext().getSubTask() instanceof SendSubTask) {
            SendSubTask subTask = (SendSubTask) baseTaskContext.getTaskContext().getSubTask();
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
        TypeReference<Map<String, Integer>> typeReference = new TypeReference<Map<String, Integer>>() {
        };
        Map<String, Integer> mapParams = JSON.parseObject(params, typeReference);
        Long tenantId = Long.valueOf(mapParams.get("tenantId"));
        TenantContextHolder.setIgnore(false);
        TenantContextHolder.setTenantId(tenantId);
        try {
            List<NotifyMessageDO> notifyMessageDOS = messageService.sendIds(mapParams.get("limit"));
            if (CollectionUtils.isEmpty(notifyMessageDOS)) {
                return new BaseTaskResult(true, "未找到待执行的任务");
            }
            List<SendSubTask> subTasks = new ArrayList<>(notifyMessageDOS.size());
            for (NotifyMessageDO notifyMessageDO : notifyMessageDOS) {
                SendSubTask subTask = new SendSubTask();
                subTask.setLogId(notifyMessageDO.getId());
                subTask.setTenantId(tenantId);
                subTasks.add(subTask);
            }

            map(subTasks, "sub_send_notify");
        } catch (Exception e) {
            log.warn("ROOT_PROCESS_FILE is fail: {}", e.getMessage(), e);
            return new BaseTaskResult(false, "ROOT_PROCESS_FILE:" + e.getMessage());
        }
        return new BaseTaskResult(true, "ROOT_PROCESS_SUCCESS");
    }

    public BaseTaskResult runSub(SendSubTask subTask) {
        TenantContextHolder.setIgnore(false);
        TenantContextHolder.setTenantId(subTask.getTenantId());
        notifyService.sendNotify(subTask.getLogId());
        return new BaseTaskResult(true, "SUB_PROCESS_SUCCESS");
    }

    @Override
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {
        TenantContextHolder.setIgnore(true);
        return super.reduce(taskContext, taskResults);
    }
}
