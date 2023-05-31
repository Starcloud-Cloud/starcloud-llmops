package com.starcloud.ops.workflow.component.app;

import cn.kstry.framework.core.component.bpmn.builder.SubProcessLink;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.bpmn.link.StartProcessLink;
import cn.kstry.framework.core.resource.config.ConfigResource;
import com.starcloud.ops.business.app.api.dto.StepWrapperDTO;
import com.starcloud.ops.business.app.api.dto.TemplateDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author df007df
 */
public class AppProcessParser implements ConfigResource {

    private final TemplateDTO appInstance;

    private final String resourceName;

    public AppProcessParser(TemplateDTO appInstance) {
        this.appInstance = appInstance;
        this.resourceName = appInstance.getName() + appInstance.getVersion();
    }

    public Optional<ProcessLink> getProcessLink() {

        Optional<StepWrapperDTO> stepWrapperDTO = this.appInstance.getConfig().getFirstStep();
        if (stepWrapperDTO.isEmpty()) {
            return Optional.empty();
        }

        StartProcessLink bpmnLink = StartProcessLink.build(this.getAppStepId(this.appInstance), this.resourceName);

        // 初始化基本信息
        ProcessLink initTask = bpmnLink.nextService().component(CSF.C.GOODS).service(CSF.GOODS.S.INIT_BASE_INFO).name("初始化商品信息").customRole("goods-custom-role@goods-detail").build();

        //instance parser to processLink

        appInstance.getConfig().getSteps().forEach((stepWrapperDTO) -> {

            stepWrapperDTO.getStep();

        });


        return Optional.of(bpmnLink);
    }

    private String getAppStepId(TemplateDTO templateDTO) {

        Optional<StepWrapperDTO> stepWrapperDTO = this.appInstance.getConfig().getFirstStep();

        return templateDTO.getId() + stepWrapperDTO.get().getStep().getName();
    }


    public Optional<ProcessLink> getProcessLink(String startStepId) {
        if (StringUtils.isBlank(startStepId)) {
            return Optional.empty();
        }

        StartProcessLink bpmnLink = StartProcessLink.build(SHOW_GOODS_LINK, "展示商品详情");

        //instance parser to processLink

        appInstance.getVersion();
        appInstance.getConfig().getSteps().forEach((stepWrapperDTO) -> {

            stepWrapperDTO.getStep();

        });


        return Optional.of(bpmnLink);
    }

    public Map<String, SubProcessLink> getAllSubProcessLink() {
        return null;
    }

    public Optional<SubProcessLink> getSubProcessLink(String subProcessId) {
        if (StringUtils.isBlank(subProcessId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(getAllSubProcessLink().get(subProcessId));
    }


    @Override
    public String getConfigName() {
        return this.resourceName;
    }
}
