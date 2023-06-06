package com.starcloud.ops.workflow.component.app.process;

import cn.hutool.core.collection.CollectionUtil;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.bpmn.link.StartProcessLink;
import cn.kstry.framework.core.resource.config.ConfigResource;
import cn.kstry.framework.core.util.KeyUtil;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.AppStepWrapper;
import java.util.ArrayList;
import java.util.List;
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

        StartProcessLink bpmnLink = StartProcessLink.build(this.app.getUid(), this.app.getName());


        List<ProcessLink> processLinks = new ArrayList<>();

        // ProcessLink processLink = null;

        List<AppStepWrapper> appStepWrappers = CollectionUtil.defaultIfEmpty(this.app.getConfig().getSteps(), new ArrayList<>());
        for (AppStepWrapper appStepWrapper : appStepWrappers) {
            ProcessLink processLink = bpmnLink.nextService(KeyUtil.req("stepId == '"+appStepWrapper.getField() +"'"), appStepWrapper.getStep().getType()).name(appStepWrapper.getName()).build();

           processLink.end();
        }

        // parallelJoinPoint.end();

        bpmnLink.end();

        return Optional.of(bpmnLink);
    }

    @Override
    public String getConfigName() {
        return this.resourceName;
    }
}
