package com.starcloud.ops.business.mission.task;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import com.starcloud.ops.business.mission.controller.admin.vo.request.SingleMissionQueryReqVO;
import com.starcloud.ops.business.mission.convert.SingleMissionConvert;
import com.starcloud.ops.business.mission.service.SingleMissionService;
import com.starcloud.ops.business.mission.service.XhsNoteSettlementActuator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
//@Component
public class XhsSettlementMapReduce extends BaseMapReduceTask {

    @Resource
    private XhsNoteSettlementActuator settlementActuator;

    @Resource
    private SingleMissionService singleMissionService;

    @Override
    protected BaseTaskResult execute(PowerJobTaskContext baseTaskContext) {
        TenantContextHolder.setIgnore(true);
        if (isRootTask()) {
            return runRoot(baseTaskContext);
        }
        if (baseTaskContext.getTaskContext().getSubTask() instanceof SubTask) {
            SubTask subTask = (SubTask) baseTaskContext.getTaskContext().getSubTask();
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
        XhsTaskContentParams xhsTaskContentParams = JSON.parseObject(params, XhsTaskContentParams.class);
        SingleMissionQueryReqVO queryReqVO = SingleMissionConvert.INSTANCE.convert(xhsTaskContentParams);

        List<Long> ids = singleMissionService.selectSettlementIds(queryReqVO);
        if (CollectionUtils.isEmpty(ids)) {
            return new BaseTaskResult(true, "未找到待执行的任务");
        }

        List<SubTask> subTasks = new ArrayList<>();
        for (Long id : ids) {
            SubTask subTask = new SubTask();
            subTask.setSingleMissionIdList(Collections.singletonList(id));
            subTasks.add(subTask);
        }
        try {
            map(subTasks, "subTask_xhs_note");
        } catch (Exception e) {
            log.warn("ROOT_PROCESS_FILE is fail: {}", e.getMessage(), e);
            return new BaseTaskResult(false, "ROOT_PROCESS_FILE:" + e.getMessage());
        }
        return new BaseTaskResult(true, "ROOT_PROCESS_SUCCESS");
    }

    public BaseTaskResult runSub(SubTask subTask) {
        List<Long> singleMissionIdList = subTask.getSingleMissionIdList();
        for (Long missionId : singleMissionIdList) {
            settlementActuator.settlement(missionId);
        }
        return new BaseTaskResult(true, "SUB_PROCESS_SUCCESS");
    }

    @Override
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {
        TenantContextHolder.setIgnore(true);
        return super.reduce(taskContext, taskResults);
    }
}
