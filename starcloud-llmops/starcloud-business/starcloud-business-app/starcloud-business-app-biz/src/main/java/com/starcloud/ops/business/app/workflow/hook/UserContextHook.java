/*
 *
 *  * Copyright (c) 2020-2023, Lykan (jiashuomeng@gmail.com).
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.starcloud.ops.business.app.workflow.hook;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.kstry.framework.core.bus.ScopeDataQuery;
import cn.kstry.framework.core.engine.thread.hook.ThreadSwitchHook;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * kstry 下 用户太线程切换支持
 */
@Slf4j
@Component
public class UserContextHook implements ThreadSwitchHook<UserContextHook.UserContext> {


    @Override
    public UserContext getPreviousData(ScopeDataQuery scopeDataQuery) {

        Boolean ignore = TenantContextHolder.isIgnore();
        Long tenantId = TenantContextHolder.getTenantId();
        Authentication authentication = SecurityFrameworkUtils.getAuthentication();

        return new UserContext(authentication, tenantId, ignore);
    }

    @Override
    public void usePreviousData(UserContext context, ScopeDataQuery scopeDataQuery) {
        if (ObjectUtils.isEmpty(context)) {
            return;
        }

        TenantContextHolder.setIgnore(context.getIgnore());
        TenantContextHolder.setTenantId(context.getTenantId());
        SecurityFrameworkUtils.setAuthentication(context.getAuthentication());
    }

    /**
     * 需要情况，因为 mofaai, mofabiji 的应用执行都会复用 线程。导致用户态会影响，部门权限获取错误导致SQL错误（小红薯执行，但是部门一直跳转到租户2的部门ID）
     * @param context
     * @param scopeDataQuery
     */
    @Override
    public void clear(UserContext context, ScopeDataQuery scopeDataQuery) {
        if (ObjectUtils.isEmpty(context)) {
            return;
        }

        TenantContextHolder.setIgnore(false);
        TenantContextHolder.setTenantId(null);
        SecurityFrameworkUtils.setAuthentication(null);

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }


    @Data
    public static class UserContext {

        /**
         * 用户信息和部门信息
         */
        private Authentication authentication;

        private Long tenantId;

        private Boolean ignore;

        public UserContext(Authentication authentication, Long tenantId, Boolean ignore) {
            this.authentication = authentication;
            this.tenantId = tenantId;
            this.ignore = ignore;
        }
    }
}
