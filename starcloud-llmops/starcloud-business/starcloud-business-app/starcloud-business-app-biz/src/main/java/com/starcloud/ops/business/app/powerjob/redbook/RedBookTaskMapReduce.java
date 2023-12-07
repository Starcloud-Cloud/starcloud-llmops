package com.starcloud.ops.business.app.powerjob.redbook;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeQueryReqVO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.powerjob.base.BaseMapReduceTask;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskContext;
import com.starcloud.ops.business.app.powerjob.base.BaseTaskResult;
import com.starcloud.ops.business.app.powerjob.base.PowerJobTaskContext;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.manager.context.CreativeContentExecuteContext;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
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
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 小红书生成任务执行入口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-22
 */
@Slf4j
@Component
public class RedBookTaskMapReduce extends BaseMapReduceTask {

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private CreativePlanService creativePlanService;

    /**
     * 执行任务
     *
     * @param context 任务上下文
     * @return 执行结果
     */
    @Override
    @TenantIgnore
    protected BaseTaskResult execute(PowerJobTaskContext context) {
        if (Objects.isNull(context)) {
            return BaseTaskResult.of(Boolean.FALSE, "小红书内容生成任务【执行失败】：任务上下文为空！无法执行任务！");
        }
        // 根任务，直接执行
        if (isRootTask()) {
            return runRoot(context);
        }
        // 子任务，可根据 subTask 的类型 或 TaskName 来判断分支
        Optional<Object> subTaskOptional = Optional.ofNullable(context.getTaskContext()).map(TaskContext::getSubTask);
        if (!subTaskOptional.isPresent()) {
            return BaseTaskResult.of(Boolean.FALSE, "小红书内容生成任务【执行失败】：任务上下文中无法获取 subTask 信息，无法执行任务！");
        }

        if (subTaskOptional.get() instanceof SubTask) {
            SubTask subTask = (SubTask) subTaskOptional.get();
            return runSub(context, subTask);
        }
        return BaseTaskResult.of(Boolean.FALSE, "小红书内容生成任务【执行失败】：未知错误：" + context.getTaskContext().getTaskName());
    }

    /**
     * 根任务执行
     *
     * @param context 任务上下文
     * @return 任务执行结果
     */
    @Override
    protected BaseTaskResult runRoot(PowerJobTaskContext context) {
        try {
            log.info("小红书内容生成根任务【执行开始】......");
            // 获取任务执行参数
            RunJobParams params = context.getParams(RunJobParams.class);
            if (Objects.isNull(params)) {
                return BaseTaskResult.of(Boolean.FALSE, "小红书内容生成根任务【执行失败】：执行参数不能为空！无法执行任务！");
            }
            log.info("小红书内容生成根任务【执行参数】\n：{}", JSONUtil.parse(params).toStringPretty());

            // 获取待执行任务
            CreativeQueryReqVO queryRequest = new CreativeQueryReqVO();
            queryRequest.setType(params.getRunType());
            queryRequest.setRetryProcess(params.getRetryProcess());
            queryRequest.setBathCount(params.getBathCount());
            queryRequest.setMaxRetry(params.getMaxRetry());
            List<CreativeContentDO> creativeContentList = creativeContentService.jobQuery(queryRequest);
            log.info("小红书内容生成根任务【查询小红书内容】：数量数量：{}", creativeContentList.size());
            if (CollectionUtils.isEmpty(creativeContentList)) {
                return BaseTaskResult.of(Boolean.TRUE, "小红书内容生成根任务【执行成功】: 没有待执行任务！");
            }

            // 按计划分组
            log.info("小红书内容生成根任务【分解子任务】......");
            Map<String, List<CreativeContentDO>> map = creativeContentList.stream().collect(Collectors.groupingBy(CreativeContentDO::getPlanUid));
            List<SubTask> subTasks = Lists.newArrayList();
            int subSize = params.getSubSize() == null ? 5 : params.getSubSize();
            for (String planUid : map.keySet()) {
                List<Long> idList = map.get(planUid).stream().map(CreativeContentDO::getId).collect(Collectors.toList());
                // 按照subSize进行切片分组
                List<List<Long>> split = CollUtil.split(idList, subSize);
                for (List<Long> longs : split) {
                    SubTask subTask = new SubTask();
                    subTask.setPlanUid(planUid);
                    subTask.setRunType(params.getRunType());
                    subTask.setContentIdList(longs);
                    subTask.setRetryProcess(params.getRetryProcess());
                    subTask.setMaxRetry(params.getMaxRetry());
                    subTasks.add(subTask);
                }
            }
            log.info("小红书内容生成根任务【分解子任务】：分解子任务数量：{}", subTasks.size());
            map(subTasks, "subTask_RedBook_initTarget");
            log.info("小红书内容生成根任务【执行成功】: 根任务执行成功！");
        } catch (Exception exception) {
            log.error("小红书内容生成根任务【执行失败】：错误信息：{}", exception.getMessage(), exception);
            return BaseTaskResult.of(Boolean.FALSE, "小红书内容生成根任务【执行失败】：" + exception.getMessage());
        }
        return BaseTaskResult.of(Boolean.TRUE, "小红书内容生成根任务【执行成功】：根任务执行成功！");
    }

    /**
     * 子任务执行
     *
     * @param context 任务上下文
     * @param subTask 子任务信息
     * @return 任务执行结果
     */
    @SuppressWarnings("all")
    public BaseTaskResult runSub(BaseTaskContext context, SubTask subTask) {
        try {
            log.info("小红书内容生成子任务【执行开始】......");
            // 构建执行上下文
            CreativeContentExecuteContext executeContext = new CreativeContentExecuteContext();
            executeContext.setPlanUid(subTask.getPlanUid());
            executeContext.setContentIdList(subTask.getContentIdList());
            executeContext.setType(subTask.getRunType());
            executeContext.setRetryProcess(subTask.getRetryProcess());
            executeContext.setMaxRetry(subTask.getMaxRetry());
            log.info("小红书内容生成子任务【执行参数】\n：{}", JSONUtil.parse(executeContext).toStringPretty());

            // 执行任务
            List<Long> redBookTask = subTask.getContentIdList();
            Map<Long, Boolean> resp = creativeContentService.execute(redBookTask, subTask.getRunType(), false);


            StringJoiner sj = new StringJoiner(",");
            List<Long> errorTasks = new ArrayList<>();
            for (Long id : redBookTask) {
                if (BooleanUtils.isTrue(resp.get(id))) {
                    continue;
                }
                errorTasks.add(id);
                sj.add(id.toString());
            }

            return new SubTaskResult(true, sj.toString(), subTask.planUid, redBookTask, errorTasks);
        } catch (Exception exception) {
            log.error("小红书内容生成子任务【执行失败】：错误信息：{}", exception.getMessage(), exception);
            return new SubTaskResult(Boolean.FALSE, "小红书内容生成子任务【执行失败】：计划UID：" + subTask.getPlanUid() + " 错误信息：" + exception.getMessage());
        }
    }

    /**
     * 子任务执行完执行
     *
     * @param context 任务上下文
     * @param results 子任务执行结果
     * @return 任务执行结果
     */
    @Override
    @TenantIgnore
    public ProcessResult reduce(TaskContext context, List<TaskResult> results) {
        try {
            log.info("小红书内容生成 结果任务【执行开始】......");
            if (CollectionUtils.isEmpty(results)) {
                return new ProcessResult(Boolean.TRUE, "reduce_success");
            }

            // 查询计划表下的 所有状态，并更新计划表的状态
            List<String> planUidList = results.stream().map(item -> {
                SubTaskResult result = JSON.parseObject(item.getResult(), SubTaskResult.class);
                if (Objects.isNull(result) || StringUtils.isBlank(result.getPlanUid())) {
                    return null;
                }
                return result.getPlanUid();
            }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

            for (String planUid : planUidList) {
                creativePlanService.updatePlanStatus(planUid);
            }
            log.info("小红书内容生成 结果任务【执行成功】：结果任务数量：{}", planUidList.size());
            return new ProcessResult(true, results.toString());
        } catch (Exception exception) {
            log.error("小红书内容生成 结果任务【执行失败】：错误信息：{}", exception.getMessage(), exception);
            return new ProcessResult(Boolean.FALSE, "小红书内容生成 结果任务【执行失败】：错误信息：" + exception.getMessage());
        }
    }

    /**
     * 子任务参数
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubTask {

        /**
         * 生成计划ID
         */
        private String planUid;

        /**
         * 创作内容ID列表
         */
        private List<Long> contentIdList;

        /**
         * 执行类型
         */
        private String runType;

        /**
         * 是否只执行重试任务
         */
        private Boolean retryProcess;

        /**
         * 最大重试次数
         */
        private Integer maxRetry;

    }
}
