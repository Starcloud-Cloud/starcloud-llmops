package com.starcloud.ops.business.app.workflow.app.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.kstry.framework.core.component.bpmn.joinpoint.InclusiveJoinPoint;
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
                    .property(JSONUtil.toJsonStr(ServiceTaskPropertyDTO.builder().stepId(stepWrapper.getStepCode()).build()))
                    .build();

            processLink.end();
        }

        // parallelJoinPoint.end();

        bpmnLink.end();

        return Optional.of(bpmnLink);
    }


    /**
     * 创建一个串行执行的 process
     *
     * @return
     */
    public Optional<ProcessLink> getFlowProcessLink() {

        StartProcessLink bpmnLink = StartProcessLink.build(this.app.getUid(), this.app.getName());

        List<WorkflowStepWrapper> stepWrappers = CollectionUtil.defaultIfEmpty(this.app.getWorkflowConfig().getSteps(), new ArrayList<>());

        ProcessLink processLink = bpmnLink;

        for (WorkflowStepWrapper stepWrapper : stepWrappers) {

            String service = stepWrapper.getFlowStep().getHandler();

            processLink = processLink.nextService(Exp.b(e -> e.equals("req.uid", "'" + this.app.getUid() + "'")), service)
                    .name(stepWrapper.getStepCode())
                    .property(JSONUtil.toJsonStr(ServiceTaskPropertyDTO.builder().stepId(stepWrapper.getStepCode()).build()))
                    .build();
        }

        processLink.end();

        return Optional.of(bpmnLink);
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
