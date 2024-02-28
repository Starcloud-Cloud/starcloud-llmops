package com.starcloud.ops.business.user.api.user;

import com.starcloud.ops.business.user.service.user.StarUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 用户积分的 API 实现类
 *
 * @author owen
 */
@Service
@Validated
public class AdminUsersApiImpl implements AdminUsersApi {

    @Resource
    private StarUserService starUserService;


    /**
     * 是否是新用户
     *
     * @param userId    用户编号
     */
    @Override
    public Boolean isNewUser(Long userId) {
        return starUserService.isNewUser(userId);
    }
}
