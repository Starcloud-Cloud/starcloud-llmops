package com.starcloud.ops.workflow.component.app;

import cn.kstry.framework.core.component.bpmn.builder.SubProcessLink;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.resource.config.ConfigResource;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * @author df007df
 */
public class AppProcessParser implements ConfigResource {

    private final Object appInstance;

    private final String resourceName;

    public AppProcessParser(Object appInstance, String resourceName) {
        this.appInstance = appInstance;
        this.resourceName = resourceName;
    }

    public Optional<ProcessLink> getProcessLink(String startEventId) {
        if (StringUtils.isBlank(startEventId)) {
            return Optional.empty();
        }

        //instance parser to processLink


        return null;
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
