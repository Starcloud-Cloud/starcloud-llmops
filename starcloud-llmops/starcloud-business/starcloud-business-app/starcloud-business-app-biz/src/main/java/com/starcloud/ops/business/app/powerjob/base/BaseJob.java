package com.starcloud.ops.business.app.powerjob.base;

import lombok.extern.slf4j.Slf4j;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

@Slf4j
public abstract class BaseJob implements BasicProcessor {


    public abstract ProcessResult execute(TaskContext context);


    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {


        ProcessResult processResult;

        try {

            processResult = execute(taskContext);

            if (processResult == null) {

                processResult = new ProcessResult();
                processResult.setSuccess(true);
            }


        } catch (Exception e) {

            processResult = new ProcessResult();

            processResult.setSuccess(false);
            processResult.setMsg(e.getMessage());

            log.warn("BaseJob#process is fail: {} {}", e.getMessage(), e);
        }


        return processResult;

    }
}
