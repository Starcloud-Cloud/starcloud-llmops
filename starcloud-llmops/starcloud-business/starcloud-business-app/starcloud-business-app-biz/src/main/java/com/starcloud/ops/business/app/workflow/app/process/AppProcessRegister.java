package com.starcloud.ops.business.app.workflow.app.process;

import cn.kstry.framework.core.bus.ScopeDataQuery;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.dynamic.creator.DynamicProcess;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppProcessRegister implements DynamicProcess {


    private final Map<String, AppContext> appEntityMap = new ConcurrentHashMap<>();

    @Override
    public long version(String appId) {

        //AppEntity app = AppFactory.factory(appId);

        return -1L;
    }

    @Override
    public String getKey(ScopeDataQuery scopeDataQuery) {

        String key = scopeDataQuery.getStartId();

        this.appEntityMap.put(key, scopeDataQuery.getReqScope());

        return scopeDataQuery.getStartId();
    }


    @Override
    public Optional<ProcessLink> getProcessLink(String startId) {

        AppContext appContext = this.appEntityMap.get(startId);
        AppProcessParser parser = new AppProcessParser(appContext.getApp());
        this.appEntityMap.remove(startId);

        //获取workflow执行类型
        if (Boolean.TRUE.equals(appContext.getStepOnce())) {
            return parser.getProcessLink();
        }

        return parser.getFlowProcessLink();


    }

}
