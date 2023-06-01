package com.starcloud.ops.workflow;

import cn.kstry.framework.core.annotation.EnableKstry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * 分发域启动类
 */
@Slf4j
@EnableKstry
@Configuration
public class WorkflowConfiguration {

    @PostConstruct
    public void init() {
        log.info("init StarCloud-Workflow ...... ");
    }

}
