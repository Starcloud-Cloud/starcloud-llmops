package com.starcloud.ops.workflow.component.app;


import cn.kstry.framework.core.annotation.NoticeResult;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.annotation.TaskComponent;
import cn.kstry.framework.core.annotation.TaskService;

@TaskComponent(name = "TextGenerationTask")
public class TextGenerationTask {


    @NoticeResult
    @TaskService(name = "gptApi", desc = "Invoke the gpt 3.5 api to generate text")
    public Object gptApi(@ReqTaskParam(reqSelf = true) Object request) {
        return null;
    }

}
