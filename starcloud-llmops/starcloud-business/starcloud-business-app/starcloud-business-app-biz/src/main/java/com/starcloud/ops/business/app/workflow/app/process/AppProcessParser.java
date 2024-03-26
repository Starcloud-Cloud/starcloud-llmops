package com.starcloud.ops.business.app.workflow.app.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.bpmn.link.StartProcessLink;
import cn.kstry.framework.core.component.expression.Exp;
import cn.kstry.framework.core.enums.ResourceTypeEnum;
import cn.kstry.framework.core.resource.config.ConfigResource;
import cn.kstry.framework.core.util.KeyUtil;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author df007df
 */
public class AppProcessParser implements ConfigResource {

    private final AppEntity app;

    private final String resourceName;


    @Override
    public ResourceTypeEnum getResourceType() {
        return ResourceTypeEnum.DYNAMIC_PROCESS;
    }

    public AppProcessParser(AppEntity app) {
        this.app = app;
        this.resourceName = app.getUid();
    }

    /**
     * 创建一个一次只执行一次的 process
     *
     * @return
     */
    public Optional<ProcessLink> getProcessLink() {

        StartProcessLink bpmnLink = StartProcessLink.build(this.app.getUid(), this.app.getName());


        List<ProcessLink> processLinks = new ArrayList<>();

        // ProcessLink processLink = null;

        List<WorkflowStepWrapper> stepWrappers = CollectionUtil.defaultIfEmpty(this.app.getWorkflowConfig().getSteps(), new ArrayList<>());
        for (WorkflowStepWrapper stepWrapper : stepWrappers) {
            ProcessLink processLink = bpmnLink.nextService(KeyUtil.req("stepId == '" + stepWrapper.getStepCode() + "'"), stepWrapper.getFlowStep().getHandler()).name(stepWrapper.getStepCode())
                    .property(JsonUtils.toJsonString(ServiceTaskPropertyDTO.builder().stepId(stepWrapper.getStepCode()).build()))
                    .build();

            processLink.end();
        }

        // parallelJoinPoint.end();

        bpmnLink.end();

        return Optional.of(bpmnLink);
    }


    /**
     * 创建一个串行执行的 process
     * 需要支持从任意节点开始执行（重试情况），所以需要组装所有节点执行的可能链路
     *
     * @return
     */
    public Optional<ProcessLink> getFlowProcessLink() {

        StartProcessLink bpmnLink = StartProcessLink.build(this.app.getUid(), this.app.getName());

        List<WorkflowStepWrapper> stepWrappers = CollectionUtil.defaultIfEmpty(this.app.getWorkflowConfig().getSteps(), new ArrayList<>());

        for (int i = 0; i < CollectionUtil.size(stepWrappers); i++) {

            this.buildServiceFlowByStep(bpmnLink, i, stepWrappers);
        }

        bpmnLink.end();

        return Optional.of(bpmnLink);
    }


    /**
     * 通过步骤下标位置开始 创建一条执行到最后的链路，返回下标步骤的执行节点，
     * 并绑定到传入的startProcessLink链路中，这样startProcessLink 就是一个带条件的并行多条执行链路
     * @param startProcessLink
     * @param startIndex
     * @param stepWrappers
     * @return
     */
    private Optional<ProcessLink> buildServiceFlowByStep(StartProcessLink startProcessLink, Integer startIndex, List<WorkflowStepWrapper> stepWrappers) {

        //开始的节点单独处理
        WorkflowStepWrapper startStepWrapper = stepWrappers.get(startIndex);

        ProcessLink startStepProcessLink = startProcessLink.nextService(
                        Exp.b(e -> e.equals("req.uid", "'" + this.app.getUid() + "'")
                                .and(Exp.b(ex -> ex.equals("req.stepId", startStepWrapper.getStepCode()))))
                        , startStepWrapper.getFlowStep().getHandler()
                )
                .name(startStepWrapper.getStepCode())
                .property(JsonUtils.toJsonString(ServiceTaskPropertyDTO.builder().stepId(startStepWrapper.getStepCode()).build()))
                .build();

        //增加后续节点
        List<WorkflowStepWrapper> flowsWrappers = CollectionUtil.sub(stepWrappers, startIndex + 1, CollectionUtil.size(stepWrappers));

        ProcessLink processLink = startStepProcessLink;

        for (WorkflowStepWrapper stepWrapper : flowsWrappers) {

            String stepId = stepWrapper.getStepCode();
            //从启动节点开始增加后续连续的节点
            processLink = processLink.nextService(
                            Exp.b(e -> e.equals("req.uid", "'" + this.app.getUid() + "'")),
                            stepWrapper.getFlowStep().getHandler())
                    .name(stepId)
                    .property(JsonUtils.toJsonString(ServiceTaskPropertyDTO.builder().stepId(stepId).build()))
                    .build();

        }

        processLink.end();

        return Optional.of(startStepProcessLink);
    }

    @Override
    public String getConfigName() {
        return this.resourceName;
    }


    @Builder
    @Data
    public static class ServiceTaskPropertyDTO {

        private String stepId;

    }

}
