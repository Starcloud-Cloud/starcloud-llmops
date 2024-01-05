package com.starcloud.ops.business.app.workflow.app.process;

import cn.kstry.framework.core.bus.ScopeDataQuery;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.dynamic.creator.DynamicProcess;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppProcessRegister implements DynamicProcess {


    private final Map<String, AppEntity> appEntityMap = new ConcurrentHashMap<>();

    @Override
    public long version(String appId) {

        //AppEntity app = AppFactory.factory(appId);

        return -1L;
    }

    @Override
    public String getKey(ScopeDataQuery scopeDataQuery) {

        String key = scopeDataQuery.getStartId();

        this.appEntityMap.put(key, (AppEntity) scopeDataQuery.getReqData("app").get());

        return scopeDataQuery.getStartId();
    }


    @Override
    public Optional<ProcessLink> getProcessLink(String startId) {

        //find appInstance
        // AppEntity app = AppFactory.factory(startId);

        AppEntity app = this.appEntityMap.get(startId);
        AppProcessParser parser = new AppProcessParser(app);
        this.appEntityMap.remove(startId);


        //获取workflow执行类型

        app.getWorkflowConfig().getSteps();

        return parser.getProcessLink();
    }

}
