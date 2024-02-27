package com.starcloud.ops.business.app.powerjob.redbook;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskContext;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;


/**
 * 小红书生成任务执行入口
 */
@Slf4j
@Component
public class RedBookTaskMapReduce extends BaseMapReduceTask {

    @Resource
    private CreativeContentService xhsCreativeContentService;

    @Resource
    private CreativePlanService creativePlanService;

    @Override
    protected BaseTaskResult execute(PowerJobTaskContext powerJobTaskContext) {
        if (isRootTask()) {
            return runRoot(powerJobTaskContext);
        }
        // 非子任务，可根据 subTask 的类型 或 TaskName 来判断分支
        if (powerJobTaskContext.getTaskContext().getSubTask() instanceof SubTask) {
            SubTask subTask = (SubTask) powerJobTaskContext.getTaskContext().getSubTask();
            return runSub(powerJobTaskContext, subTask);
        }
        return new BaseTaskResult(false, "UNKNOWN_BUG_taskName_" + powerJobTaskContext.getTaskContext().getTaskName());
    }


    /**
     * 生成子任务
     *
     * @param baseTaskContext
     * @return
     */
    @TenantJob
    @Override
    protected BaseTaskResult runRoot(PowerJobTaskContext baseTaskContext) {

        RunJobParams params = baseTaskContext.getParams(RunJobParams.class);
        String rank = "ASC";

        //需要按类型去执行，文案或图片
        if (params.getRunType() == null) {
            return BaseTaskResult.builder().success(false).msg("The getRunType parameter is null. getRunType must have a value").build();
        }


        //根据查询，查询出所有执行中的计划下的所有待执行的创作任务
        //支持的条件可能有，文案模版，图片模版，渠道 （创作任务表上的字段） 时间生序查询，优先执行最早的


        CreativeQueryReqVO queryReq = new CreativeQueryReqVO();
        queryReq.setType(params.getRunType());
        queryReq.setRetryProcess(params.getRetryProcess());
        queryReq.setBathCount(params.getBathCount());
        queryReq.setIsTest(params.getIsTest());

        List<CreativeContentDO> creativeContentList = xhsCreativeContentService.jobQuery(queryReq);
        if (CollectionUtils.isEmpty(creativeContentList)) {
            return new BaseTaskResult(true, "ROOT_PROCESS_SUCCESS : 未找到待执行的任务");
        }

        Map<String, List<CreativeContentDO>> planUidGroup = creativeContentList.stream().collect(Collectors.groupingBy(CreativeContentDO::getPlanUid));

        List<SubTask> subTasks = new ArrayList<>(planUidGroup.size());
        Integer subSize = params.getSubSize() == null ? 5 : params.getSubSize();
        for (String planUid : planUidGroup.keySet()) {
            List<CreativeContentDO> creativeContentDOList = planUidGroup.get(planUid);
            List<List<CreativeContentDO>> split = CollUtil.split(creativeContentDOList, subSize);
            for (List<CreativeContentDO> creativeContents : split) {
                SubTask subTask = new SubTask();
                subTask.setPlanUid(planUid);
                subTask.setBatch(planUidGroup.get(planUid).get(0).getBatch());
                subTask.setRunType(params.getRunType());
                subTask.setRedBookIdList(creativeContents.stream().map(CreativeContentDO::getId).collect(Collectors.toList()));
                subTask.setTenantId(planUidGroup.get(planUid).get(0).getTenantId());
                subTasks.add(subTask);
            }
        }
        try {
            map(subTasks, "subTask_RedBook_initTarget");
        } catch (Exception e) {
            log.warn("ROOT_PROCESS_FILE is fail: {}", e.getMessage(), e);
            return new BaseTaskResult(false, "ROOT_PROCESS_FILE:" + e.getMessage());
        }
        return new BaseTaskResult(true, "ROOT_PROCESS_SUCCESS");
    }


    //单操作任务执行入口
    public BaseTaskResult runSub(BaseTaskContext powerJobTaskContext, SubTask subTask) {
        try {

            StringJoiner sj = new StringJoiner(",");
            List<Long> errorTasks = new ArrayList<>();
            List<Long> redBookTask = subTask.getRedBookIdList();

            //按租户去执行
            TenantUtils.execute(subTask.getTenantId(), () -> {

                Map<Long, Boolean> resp = xhsCreativeContentService.execute(redBookTask, subTask.getRunType(), false);

                for (Long id : redBookTask) {
                    if (BooleanUtils.isTrue(resp.get(id))) {
                        continue;
                    }
                    errorTasks.add(id);
                    sj.add(id.toString());
                }

            });


            //调用批量执行的服务，服务内部不要抛异常，执行失败的在后续的轮训中继续执行

            //返回所有执行状态

            //判断所有执行状态，如果有失败就返回给 job 去做日志，
            //servide.batch(1,2,3,4)


            return new SubTaskResult(true, sj.toString(), subTask.planUid, subTask.getBatch(), redBookTask, errorTasks);
        } catch (Exception e) {
            //不会调用到这里，兜底错误和日志
            return new SubTaskResult(false, "task id is " + subTask.getPlanUid() + "  RunTask is fail: " + e.getMessage());
        }
    }

    /**
     * 子任务执行完执行
     *
     * @param taskContext
     * @param taskResults
     * @return
     */
    @Override
    @TenantIgnore
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {
        if (CollectionUtils.isEmpty(taskResults)) {
            return new ProcessResult(true, "reduce_success");
        }

        //查询计划表下的 所有状态，并更新计划表的状态
        List<SubTaskResult> planUids = taskResults.stream().map(sub -> {
            return JSON.parseObject(sub.getResult(), SubTaskResult.class);
        }).collect(Collectors.toList());

        updateInstance(planUids);

        return new ProcessResult(true, taskResults.toString());
    }

    private void updateInstance(List<SubTaskResult> subTaskResultList) {

        //根据创作任务找到所有创作计划

        //查询所有创作计划的所有任务状态，判断是否都执行完成。完成就更新创作计划状态到执行完成。
        List<SubTaskResult> distinct = CollUtil.distinct(subTaskResultList, SubTaskResult::getPlanUid, false);

        for (SubTaskResult subTaskResult : distinct) {
            creativePlanService.updatePlanStatus(subTaskResult.getPlanUid(), subTaskResult.getBatch());
        }
    }


    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {

        //DTO对象
        private List<Long> redBookIdList;

        private Long tenantId;


        /**
         * 生成计划ID
         */
        private String planUid;

        private Long batch;

        //带入一些上游的参数做校验
        private String runType;
    }
}
