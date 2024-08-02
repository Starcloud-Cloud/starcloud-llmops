package com.starcloud.ops.business.user.framework.rights.core.rightslimit;

import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author Yang
 */
@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class RightsLimitAspect {

    @Resource
    private AdminUserLevelService adminUserLevelService;


    /**
     * 接口请求频率限制切面逻辑
     *
     * @param rightsLimit 访问限制
     */
    @Before(value = "@annotation(rightsLimit)")
    public void beforePointCut(JoinPoint joinPoint, RightsLimit rightsLimit) {
        // 获取当前登录用户
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        adminUserLevelService.validateLevelRightsLimit(rightsLimit.value().getRedisKey(),loginUserId);
    }
}
