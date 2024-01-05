package com.starcloud.ops.business.app.service.Task;

import cn.iocoder.yudao.framework.common.context.UserContextHolder;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.concurrent.*;


/**
 * @author starcloud
 */
@Component
@Slf4j
public class ThreadWithContext {

    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 36,
            60, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadFactory() {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r, "open-ai-thread");
            thread.setDaemon(true);
            return thread;
        }
    }, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("The open-ai-thread pool is full");
        }
    });


    public void asyncExecute(RunFunction runFunction) {
        Long tenantId = TenantContextHolder.getTenantId();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Long userId = UserContextHolder.getUserId();

        Authentication authentication = SecurityFrameworkUtils.getAuthentication();

        TtlRunnable task = TtlRunnable.get(() -> {
            TenantContextHolder.setIgnore(false);
            TenantContextHolder.setTenantId(tenantId);
            RequestContextHolder.setRequestAttributes(requestAttributes, true);

            SecurityFrameworkUtils.setAuthentication(authentication);

            UserContextHolder.setUserId(userId);

            log.info("asyncExecute_user: {} {}", SecurityFrameworkUtils.getLoginUserId(), TenantContextHolder.getTenantId());

            runFunction.run();
            UserContextHolder.clear();
            TenantContextHolder.clear();
            RequestContextHolder.resetRequestAttributes();
        });

        threadPoolExecutor.execute(task);
    }

}
