package com.starcloud.ops.llm.workflow.kstry;

import cn.kstry.framework.core.component.bpmn.BpmnProcessParser;
import cn.kstry.framework.core.component.bpmn.link.ProcessLink;
import cn.kstry.framework.core.component.dynamic.creator.DynamicProcess;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

@Component
public class DynamicProcessRegister implements DynamicProcess {

    @Override
    public long version(String key) {
        return 0;
    }

    @Override
    public Optional<ProcessLink> getProcessLink(String startId) {
        InputStream inputStream = DynamicProcessRegister.class.getClassLoader().getResourceAsStream("dynamic/goods.bpmn");
        BpmnProcessParser parser = new BpmnProcessParser("动态获取流程", inputStream);
        return parser.getProcessLink(startId);
    }
}
