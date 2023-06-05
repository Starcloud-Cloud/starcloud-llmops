package com.starcloud.ops.workflow.component.app.process;

import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.dynamic.creator.DynamicProcess;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppProcessRegister implements DynamicProcess {

    @Override
    public long version(String appId) {

        AppEntity app = AppFactory.factoryBase(appId);

        return app.getVersion();
    }

    @Override
    public Optional<ProcessLink> getProcessLink(String appId) {

        //find appInstance
        AppEntity app = AppFactory.factory(appId);
        AppProcessParser parser = new AppProcessParser(app);

        return parser.getProcessLink();
    }

}
