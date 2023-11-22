package cn.iocoder.yudao.framework.tenant.core.context;

import cn.iocoder.yudao.framework.common.context.UserContextHolder;

public class RequestContext {


    /**
     * 补充开放接口用户态
     * @param userId
     * @param tenantId
     */
    public void initContext(Long userId, Long tenantId) {
        UserContextHolder.setUserId(userId);
        TenantContextHolder.setTenantId(tenantId);
        TenantContextHolder.setIgnore(false);
    }

}
