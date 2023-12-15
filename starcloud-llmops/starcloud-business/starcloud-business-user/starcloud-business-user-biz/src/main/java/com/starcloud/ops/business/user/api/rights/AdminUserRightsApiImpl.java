package com.starcloud.ops.business.user.api.rights;

import cn.hutool.core.lang.Assert;

import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.POINT_RECORD_BIZ_NOT_SUPPORT;


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
    public void addRights(Long userId, Integer magicBean, Integer magicImage,
                          Integer bizType, String bizId) {
        Assert.isTrue(magicBean > 0);
        Assert.isTrue(magicImage > 0);
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        if (bizTypeEnum == null) {
            throw exception(POINT_RECORD_BIZ_NOT_SUPPORT);
        }
        adminUserRightsService.createPointRecord(userId, magicBean, magicImage, bizTypeEnum, bizId);
    }

    @Override
    public void reduceRights(Long userId, Integer rightType, Integer rightAmount,
                             Integer bizType, String bizId) {
        Assert.isTrue(rightAmount > 0);
        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.getByType(bizType);
        if (bizTypeEnum == null) {
            throw exception(POINT_RECORD_BIZ_NOT_SUPPORT);
        }
        AdminUserRightsTypeEnum rightTypeEnum = AdminUserRightsTypeEnum.getByType(rightType);
        if (bizTypeEnum == null) {
            throw exception(POINT_RECORD_BIZ_NOT_SUPPORT);
        }

        adminUserRightsService.createPointRecord(userId, -rightAmount,null, bizTypeEnum, bizId);
    }

}
