package com.starcloud.ops.workflow.component.app.process;

import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.dynamic.creator.DynamicProcess;
import com.starcloud.ops.workflow.component.app.AppProcessParser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppProcessRegister implements DynamicProcess {

    @Override
    public long version(String appId) {

        Object appInstance = getAppInstance(appId);

        return 0;
    }

    @Override
    public Optional<ProcessLink> getProcessLink(String appId) {

        //find appInstance
        Object appInstance = getAppInstance(appId);

        AppProcessParser parser = new AppProcessParser(appInstance, appInstance.getClass().getName());

        return parser.getProcessLink(appId);
    }

    private Object getAppInstance(String appId) {
        Object appInstance = new Object();

        return appInstance;
    }
}
