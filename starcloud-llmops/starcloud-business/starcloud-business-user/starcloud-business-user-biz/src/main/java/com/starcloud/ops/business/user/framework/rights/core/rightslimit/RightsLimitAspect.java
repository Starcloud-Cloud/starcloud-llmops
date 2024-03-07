package com.starcloud.ops.business.user.framework.rights.core.rightslimit;

import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelDetailRespVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.enums.LevelRightsLimitEnums;
import com.starcloud.ops.business.user.service.level.AdminUserLevelConfigService;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Yang
 */
@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class RightsLimitAspect {

    // @Resource
    // private final UserLevelConfigLimitRedisDAO userLevelConfigLimitRedisDAO;

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Resource
    private AdminUserLevelConfigService adminUserLevelConfigService;

    /**
     * 接口请求频率限制切面逻辑
     *
     * @param rightsLimit 访问限制
     */
    @Before(value = "@annotation(rightsLimit)")
    public void beforePointCut(JoinPoint joinPoint, RightsLimit rightsLimit) {

        // 获取当前登录用户
        Long loginUserId = WebFrameworkUtils.getLoginUserId();

        Assert.notNull(loginUserId, "用户未登录");

        // 获取用户等级信息
        List<AdminUserLevelDetailRespVO> levelList = adminUserLevelService.getLevelList(loginUserId);
        if (levelList.isEmpty()) {
            log.info("用户{}，没有等级信息", loginUserId);
            return;
        }
        AdminUserLevelConfigDO levelConfigDO = adminUserLevelConfigService.getLevelConfig(levelList.get(0).getLevelId());
        // 获取用户当前权益最大的值
        LevelConfigDTO levelConfigDTO = levelConfigDO.getLevelConfig();

        LevelRightsLimitEnums value = rightsLimit.value();

        Integer data = (Integer) value.getExtractor().apply(levelConfigDTO);

        //  如果不做限制
        if (data == -1) {
            return;
        }
        //
        // Integer result = userLevelConfigLimitRedisDAO.get(value.getRedisKey());
        //
        // //  如果第一次访问
        // if (result == 0 || data >= result) {
        //     userLevelConfigLimitRedisDAO.increment(value.getRedisKey());
        //     return;
        // }
        // throw new RuntimeException(rightsLimit.info());

    }
}
