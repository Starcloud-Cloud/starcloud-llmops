package com.starcloud.ops.business.app.powerjob.base;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;
import tech.powerjob.worker.core.processor.sdk.MapReduceProcessor;

import java.util.List;

/**
 * MapReduce任务基类
 *
 * @author admin
 */
@Slf4j
public abstract class BaseMapReduceTask implements MapReduceProcessor {

    /**
     * 获取task名称
     *
     * @return task 任务名称
     */
    public String getTaskName() {
        return getClass().getSimpleName();
    }

    /**
     * 执行任务
     *
     * @param baseTaskContext 任务上下文
     * @return 任务结果
     */
    protected abstract BaseTaskResult execute(PowerJobTaskContext baseTaskContext);

    /**
     * 执行主任务，生成子任务
     *
     * @param baseTaskContext 任务上下文
     * @return 任务结果
     */
    protected abstract BaseTaskResult runRoot(PowerJobTaskContext baseTaskContext);

    /**
     * PowerJob MapReduce 执行任务入口
     *
     * @param context 任务上下文
     * @return 任务结果
     */
    @Override
    public ProcessResult process(TaskContext context) {

        PowerJobTaskContext baseTaskContext = PowerJobTaskContext.builder().taskContext(context).build();

        BaseTaskResult baseTaskResult;

        ProcessResult processResult;

        try {

            // 进行实际处理...
            baseTaskResult = execute(baseTaskContext);

        } catch (Exception e) {

            baseTaskResult = BaseTaskResult.builder().success(false).msg(e.getMessage()).build();
        }

        if (baseTaskResult == null) {

            processResult = new ProcessResult(true, "task: " + getTaskName() + " is finish");
        } else {
            processResult = new ProcessResult(baseTaskResult.isSuccess(), JSON.toJSONString(baseTaskResult));
        }

        //baseTaskContext.getLog().info("instanceId:{}, end: {}, status: {}, msg: {}", baseTaskContext.getInstanceId(), getName(), processResult.isSuccess(), processResult.getMsg());

        return processResult;
    }

    /**
     * 空实现
     *
     * @param taskContext 任务上下文
     * @param taskResults 任务结果
     * @return 任务结果
     */
    @Override
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {

        // 所有 Task 执行结束后，reduce 将会被执行
        // taskResults 保存了所有子任务的执行结果

        // 用法举例，统计执行结果

        int size = taskResults.size();

        // 该结果将作为任务最终执行结果
        return new ProcessResult(true, "success all task num:" + size);
    }


}
