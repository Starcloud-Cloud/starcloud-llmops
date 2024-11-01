package com.starcloud.ops.business.app.powerjob.redbook;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.job.TenantJob;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentTaskReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskContext;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.TaskResult;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;


/**
 * 小红书生成任务执行入口
 */
@Slf4j
@Component
public class RedBookTaskMapReduce extends BaseMapReduceTask {

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private CreativePlanService creativePlanService;

    /**
     * 任务执行
     *
     * @param context 任务上下文
     * @return 执行结果
     */
    @Override
    @DataPermission(enable = false)
    protected BaseTaskResult execute(PowerJobTaskContext context) {

        if (isRootTask()) {
            return runRoot(context);
        }
        // 非子任务，可根据 subTask 的类型 或 TaskName 来判断分支
        if (context.getTaskContext().getSubTask() instanceof SubTask) {
            SubTask subTask = (SubTask) context.getTaskContext().getSubTask();
            return runSub(context, subTask);
        }

        // 返回失败结果
        BaseTaskResult taskResult = new BaseTaskResult();
        taskResult.setSuccess(Boolean.FALSE);
        taskResult.setMsg("执行失败：未知错误：任务名称：" + context.getTaskContext().getTaskName());
        return taskResult;
    }


    /**
     * 生成子任务
     *
     * @param context 上下文
     * @return 执行结果
     */
    @TenantJob
    @Override
    @DataPermission(enable = false)
    protected BaseTaskResult runRoot(PowerJobTaskContext context) {

        // 获取任务执行参数
        RunJobParams params = context.getParams(RunJobParams.class);
        log.info("创作内容根任务执行开始：参数：{}", JsonUtils.toJsonString(params));

        // 根据参数查询任务列表
        CreativeContentTaskReqVO query = new CreativeContentTaskReqVO();
        query.setStatus(params.getStatus());
        query.setMaxRetry(params.getMaxRetry());
        query.setBathCount(params.getBathCount());
        List<CreativeContentRespVO> contentList = creativeContentService.listTask(query);

        // 如果没有找到带执行的任务，直接返回。
        if (CollectionUtils.isEmpty(contentList)) {
            BaseTaskResult taskResult = new BaseTaskResult();
            taskResult.setSuccess(Boolean.FALSE);
            taskResult.setMsg("任务成功结束：未找到待执行任务！");
            return taskResult;
        }

        // 有几组分成几个子任务
        List<SubTask> subTaskList = new ArrayList<>();

        // 每一个子任务的子任务数量
        int subSize = Objects.isNull(params.getSubSize()) ? 5 : params.getSubSize();

        // 按照创作计划批次UID进行分组
        Map<String, List<CreativeContentRespVO>> batchContentMap = contentList.stream()
                .collect(Collectors.groupingBy(CreativeContentRespVO::getBatchUid));

        // 遍历处理任务
        batchContentMap.forEach((bathUid, batchContentList) -> {
            // 按照 subSize 分段截取每一个分组的数量
            List<List<CreativeContentRespVO>> segmentTaskList = CollUtil.split(batchContentList, subSize);
            // 遍历每一个分段
            for (List<CreativeContentRespVO> creativeContents : segmentTaskList) {
                if (CollectionUtils.isEmpty(creativeContents)) {
                    continue;
                }

                CreativeContentRespVO content = creativeContents.get(0);
                // 获取分组UID列表
                List<String> contentUidList = creativeContents.stream()
                        .map(CreativeContentRespVO::getUid).collect(Collectors.toList());

                // 构建子任务
                SubTask subTask = new SubTask();
                subTask.setTenantId(content.getTenantId());
                subTask.setPlanUid(content.getPlanUid());
                subTask.setBatchUid(bathUid);
                subTask.setMaxRetry(params.getMaxRetry());
                subTask.setContentUidList(contentUidList);
                subTaskList.add(subTask);
            }
        });

        try {
            map(subTaskList, "创作内容子任务");
        } catch (Exception e) {
            log.warn("创作内容任务执行失败：错误信息：{}", e.getMessage(), e);
            BaseTaskResult taskResult = new BaseTaskResult();
            taskResult.setSuccess(Boolean.FALSE);
            taskResult.setMsg("创作内容根任务执行失败：错误信息：" + e.getMessage());
            return taskResult;
        }

        BaseTaskResult taskResult = new BaseTaskResult();
        taskResult.setSuccess(Boolean.TRUE);
        taskResult.setMsg("创作内容根任务执行成功");
        return taskResult;
    }

    /**
     * 执行子任务
     *
     * @param context 执行上下文
     * @param subTask 子任务
     * @return 执行结果
     */
    @SuppressWarnings("unused")
    @DataPermission(enable = false)
    public BaseTaskResult runSub(BaseTaskContext context, SubTask subTask) {
        try {

            StringJoiner stringJoiner = new StringJoiner(",");
            List<String> errorTasks = new ArrayList<>();
            // 创作内容列表
            List<String> contentUidList = subTask.getContentUidList();
            // 构建请求列表
            List<CreativeContentExecuteReqVO> requestList = contentUidList.stream()
                    .map(item -> {
                        CreativeContentExecuteReqVO request = new CreativeContentExecuteReqVO();
                        request.setUid(item);
                        request.setPlanUid(subTask.getPlanUid());
                        request.setBatchUid(subTask.getBatchUid());
                        request.setMaxRetry(subTask.getMaxRetry());
                        request.setForce(Boolean.FALSE);
                        request.setTenantId(subTask.getTenantId());
                        return request;
                    })
                    .collect(Collectors.toList());

            //按租户去执行
            TenantUtils.execute(subTask.getTenantId(), () -> {
                // 批量执行
                List<CreativeContentExecuteRespVO> responseList = creativeContentService.batchExecute(requestList);
                // 处理结果
                for (CreativeContentExecuteRespVO response : responseList) {
                    if (response.getSuccess()) {
                        continue;
                    }
                    errorTasks.add(response.getUid());
                    stringJoiner.add(response.getUid());
                }
            });

            // 成功，返回数据
            SubTaskResult subTaskResult = new SubTaskResult();
            subTaskResult.setSuccess(Boolean.TRUE);
            subTaskResult.setMsg(stringJoiner.toString());
            subTaskResult.setPlanUid(subTask.getPlanUid());
            subTaskResult.setBatchUid(subTask.getBatchUid());
            subTaskResult.setTaskUidList(contentUidList);
            subTaskResult.setErrorTaskUidList(errorTasks);
            return subTaskResult;
        } catch (Exception e) {
            //不会调用到这里，兜底错误和日志
            SubTaskResult subTaskResult = new SubTaskResult();
            subTaskResult.setSuccess(Boolean.FALSE);
            subTaskResult.setMsg("创作内容执行失败：计划UID: " + subTask.getPlanUid() + "批次UID: " + subTask.getBatchUid() + "错误信息：" + e.getMessage());
            subTaskResult.setPlanUid(subTask.getPlanUid());
            subTaskResult.setBatchUid(subTask.getBatchUid());
            return subTaskResult;
        }
    }

    /**
     * 所有任务执行完执行
     *
     * @param taskContext 任务上下文
     * @param taskResults 任务结果
     * @return 执行结果
     */
    @Override
    @TenantIgnore
    @DataPermission(enable = false)
    public ProcessResult reduce(TaskContext taskContext, List<TaskResult> taskResults) {
        // 更新创作状态交给 creativeContentService.batchExecute 去处理。
//
//        // 任务结果为空！不需要进行更新创作计划，创作计划批次状态！
//        if (CollectionUtils.isEmpty(taskResults)) {
//            return new ProcessResult(Boolean.TRUE, "任务结果为空！不需要进行更新创作状态！");
//        }
//
//        // 查询计划表下的 所有状态，并更新计划表的状态
//        List<SubTaskResult> subTaskResultList = taskResults.stream()
//                .filter(item -> StringUtils.isNotBlank(item.getResult()))
//                .map(item -> JsonUtils.parseObject(item.getResult(), SubTaskResult.class))
//                .filter(Objects::nonNull)
//                .filter(item -> StringUtils.isNotBlank(item.getPlanUid()))
//                .filter(item -> StringUtils.isNotBlank(item.getBatchUid()))
//                .collect(Collectors.toList());
//
//        // 如果为空，说明不需要更新计划状态
//        if (CollectionUtils.isEmpty(subTaskResultList)) {
//            return new ProcessResult(Boolean.TRUE, "任务结果为空！不需要进行更新创作状态！");
//        }
//
//        log.info("创作内容后置处理器开始执行：更新创作状态开始！");
//
//        // 需要更新的计划列表
//        log.info("需要更新创作状态的子任务：{}", JsonUtils.toJsonPrettyString(subTaskResultList));
//        updateInstance(subTaskResultList);
//
//        // 计划更新完成
//        log.info("创作内容后置处理器执行完成，更新创作状态完成！！！");
        return new ProcessResult(Boolean.TRUE, taskResults.toString());
    }

    /**
     * 更新
     *
     * @param subTaskResultList 子任务结果集合
     */
    private void updateInstance(List<SubTaskResult> subTaskResultList) {
        //查询所有创作计划的所有任务状态，判断是否都执行完成。完成就更新创作计划状态到执行完成。
        List<SubTaskResult> distinct = CollUtil.distinct(subTaskResultList, SubTaskResult::getBatchUid, false);
        for (SubTaskResult subTaskResult : distinct) {
            creativePlanService.updatePlanStatus(subTaskResult.getPlanUid(), subTaskResult.getBatchUid());
        }
    }

    /**
     * 创作内容子任务
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {

        /**
         * 租户ID
         */
        private Long tenantId;

        /**
         * 生成计划ID
         */
        private String planUid;

        /**
         * 批次UID
         */
        private String batchUid;

        /**
         * 创作内容UID集合
         */
        private List<String> contentUidList;

        /**
         * 最大重试次数
         */
        private Integer maxRetry;
    }
}
