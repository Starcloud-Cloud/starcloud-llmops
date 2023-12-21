package com.starcloud.ops.business.user.api.rights;

import cn.hutool.core.lang.Assert;

import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import java.time.LocalDateTime;

import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.RIGHTS_BIZ_NOT_SUPPORT;


import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 用户积分的 API 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUserRightsApiImpl implements AdminUserRightsApi {

    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Override
    public void addRights(Long userId, Integer magicBean, Integer magicImage, LocalDateTime validStartTime, LocalDateTime validEndTime,
                          Integer bizType, String bizId) {
        Assert.isTrue(magicBean > 0 || magicImage > 0);
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        if (bizTypeEnum == null) {
            throw exception(RIGHTS_BIZ_NOT_SUPPORT);
        }
        adminUserRightsService.createRights(userId, magicBean, magicImage, validStartTime, validEndTime, bizTypeEnum, bizId);
    }

    @Override
    public void reduceRights(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount,
                             Integer bizType, String bizId) {
        Assert.isTrue(rightAmount > 0);
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);

        if (bizTypeEnum == null) {
            throw exception(RIGHTS_BIZ_NOT_SUPPORT);
        }
        adminUserRightsService.reduceRights(userId, rightsType, rightAmount, bizTypeEnum, bizId);
    }

    /**
     * 判断权益是否充足
     *
     * @param userId
     * @param rightsType
     * @param rightAmount
     * @return
     */
    @Override
    public Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        return adminUserRightsService.calculateUserRightsEnough(userId, rightsType, rightAmount);
    }

}
