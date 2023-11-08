package com.starcloud.ops.business.app.powerjob.redbook;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskContext;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 小红书生成任务执行入口
 */
@Slf4j
@Configuration
public class RedBookTaskMapReduce extends BaseMapReduceTask {


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
    @Override
    protected BaseTaskResult runRoot(PowerJobTaskContext baseTaskContext) {

        RunJobParams params = baseTaskContext.getParams(RunJobParams.class);
        String rank = "ASC";

        //需要按类型去执行，文案或图片
        if (params.getRunType() == null) {
            return BaseTaskResult.builder().success(false).msg("The getRunType parameter is null. getRunType must have a value").build();
        }

        //只执行重试的判断
        if (Boolean.TRUE.equals(params.getRetryProcess())) {

        }

        //根据查询，查询出所有执行中的计划下的所有待执行的创作任务
        //支持的条件可能有，文案模版，图片模版，渠道 （创作任务表上的字段） 时间生序查询，优先执行最早的

        //查询对应数量的数据
        params.getBathCount();

        List<Object> taskIdList = new ArrayList<>();

        //根据数据大小情况，这个subTask 一定要小，有大小限制的
        List<SubTask> subTasks = Optional.ofNullable(taskIdList).orElse(new ArrayList<>()).stream().map(id -> {
            return SubTask.builder().redBookList(new ArrayList<>()).build();

        }).collect(Collectors.toList());
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

            List<Object> redBookTask = subTask.getRedBookList();

            //调用批量执行的服务，服务内部不要抛异常，执行失败的在后续的轮训中继续执行

            //返回所有执行状态

            //判断所有执行状态，如果有失败就返回给 job 去做日志，
            //servide.batch(1,2,3,4)

            String log = "json";
            return new BaseTaskResult(log, true);
        } catch (Exception e) {
            //不会调用到这里，兜底错误和日志
            return new BaseTaskResult(false, "task id is " + subTask.getPlanUid() + "  RunTask is fail: " + e.getMessage());
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
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {

        //查询计划表下的 所有状态，并更新计划表的状态


        return null;
    }

    private void updateInstance(List<TaskResult> taskResults) {
        //找到所有创作任务
        List<String> codes = Optional.ofNullable(taskResults).orElse(new ArrayList<>()).stream().map(taskResult -> {
            BaseTaskResult baseTaskResult = JSON.parseObject(taskResult.getResult(), BaseTaskResult.class);
            return baseTaskResult;
        }).filter(baseTaskResult -> {
            return StrUtil.isNotBlank(baseTaskResult.getKey());
        }).map(result -> {
            return result.getKey();
        }).collect(Collectors.toList());
        if (CollUtil.isEmpty(codes)) {
            return;
        }

        //根据创作任务找到所有创作计划

        //查询所有创作计划的所有任务状态，判断是否都执行完成。完成就更新创作计划状态到执行完成。

    }


    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {

        //DTO对象
        private List<Object> redBookList;


        /**
         * 生成计划ID
         */
        private String planUid;

        //带入一些上游的参数做校验
        private String runType;
    }
}
