package com.starcloud.ops.business.app.service.Task;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author starcloud
 */
@Component
@Slf4j
public class ThreadWithContext {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 36,
            60, TimeUnit.MICROSECONDS, new SynchronousQueue<>(), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("The Thread pool is full");
        }
    });


    public void asyncExecute(RunFunction runFunction) {
        Long tenantId = TenantContextHolder.getTenantId();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        threadPoolExecutor.execute(() -> {
            TenantContextHolder.setIgnore(false);
            TenantContextHolder.setTenantId(tenantId);
            RequestContextHolder.setRequestAttributes(requestAttributes);
            runFunction.run();
        });
    }

}
