package com.starcloud.ops.business.user.api.user;

import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
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

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Resource
    private AdminUserRightsService adminUserRightsService;


    /**
     * 是否是新用户
     *
     * @param userId 用户编号
     */
    @Override
    public Boolean isNewUser(Long userId) {
        return starUserService.isNewUser(userId);
    }


    // /**
    //  * 用户权益和等级统一新增处理
    //  *
    //  * @param levelCreateReqVO 用户等级
    //  * @param addRightsDTO     用户权益
    //  */
    // // @Override
    // public void insertUserRightsAndLevel(AdminUserRightsCommonDTO rightsCommonDTO) {
    //
    //     AdminUserLevelDO levelRecord = adminUserLevelService.createLevelRecord(levelCreateReqVO);
    //
    //     adminUserRightsService.createRights(rightsCommonDTO);
    // }
}
