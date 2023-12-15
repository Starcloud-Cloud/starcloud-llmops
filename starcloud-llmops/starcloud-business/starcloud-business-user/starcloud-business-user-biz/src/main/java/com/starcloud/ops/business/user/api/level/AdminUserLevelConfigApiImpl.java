package com.starcloud.ops.business.user.api.level;

import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelConfigRespDTO;
import com.starcloud.ops.business.user.convert.level.AdminUserLevelConvert;
import com.starcloud.ops.business.user.enums.AdminUserExperienceBizTypeEnum;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.EXPERIENCE_BIZ_NOT_SUPPORT;

/**
 * 会员等级 API 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUserLevelConfigApiImpl implements AdminUserLevelConfigApi {

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Override
    public AdminUserLevelConfigRespDTO getAdminUserLevel(Long id) {
        return AdminUserLevelConvert.INSTANCE.convert02(adminUserLevelService.getLevel(id));
    }


    @Override
    public void addExperience(Long userId, Integer experience, Integer bizType, String bizId) {
        AdminUserExperienceBizTypeEnum bizTypeEnum = AdminUserExperienceBizTypeEnum.getByType(bizType);
        if (bizTypeEnum == null) {
            throw exception(EXPERIENCE_BIZ_NOT_SUPPORT);
        }
//        adminUserLevelService.addExperience(userId, experience, bizTypeEnum, bizId);
    }

    @Override
    public void reduceExperience(Long userId, Integer experience, Integer bizType, String bizId) {
        addExperience(userId, -experience, bizType, bizId);
    }

}
