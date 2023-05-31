package com.starcloud.ops.workflow.component.app.process;

import cn.kstry.framework.core.component.bpmn.builder.SubProcessLink;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.bpmn.link.StartProcessLink;
import cn.kstry.framework.core.resource.config.ConfigResource;
import com.starcloud.ops.business.app.api.dto.StepWrapperDTO;
import com.starcloud.ops.business.app.api.dto.TemplateDTO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author df007df
 */
public class AppProcessParser implements ConfigResource {

    private final AppEntity app;

    private final String resourceName;

    public AppProcessParser(AppEntity app) {
        this.app = app;
        this.resourceName = app.getUniqueName();
    }

    public Optional<ProcessLink> getProcessLink() {

        String stepId = this.app.getUid();

        StartProcessLink bpmnLink = StartProcessLink.build(this.app.getUniqueName(), this.app.getName());

        // 初始化基本信息

        //instance parser to processLink

        bpmnLink.end();





        return Optional.of(bpmnLink);
    }
//
//    private String getAppStepId(TemplateDTO templateDTO) {
//
//        Optional<StepWrapperDTO> stepWrapperDTO = this.appInstance.getConfig().getFirstStep();
//
//        return templateDTO.getId() + stepWrapperDTO.get().getStep().getName();
//    }
//
//
//    public Optional<ProcessLink> getProcessLink(String startStepId) {
//        if (StringUtils.isBlank(startStepId)) {
//            return Optional.empty();
//        }
//
//        StartProcessLink bpmnLink = StartProcessLink.build("12", "展示商品详情");
//
//        //instance parser to processLink
//
//        appInstance.getVersion();
//        appInstance.getConfig().getSteps().forEach((stepWrapperDTO) -> {
//
//            stepWrapperDTO.getStep();
//
//        });
//
//
//        return Optional.of(bpmnLink);
//    }
//
//    public Map<String, SubProcessLink> getAllSubProcessLink() {
//        return null;
//    }
//
//    public Optional<SubProcessLink> getSubProcessLink(String subProcessId) {
//        if (StringUtils.isBlank(subProcessId)) {
//            return Optional.empty();
//        }
//        return Optional.ofNullable(getAllSubProcessLink().get(subProcessId));
//    }


    @Override
    public String getConfigName() {
        return this.resourceName;
    }
}
