package com.starcloud.ops.business.user.api.user;

import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;

/**
 * 用户积分的 API 接口
 *
 * @author owen
 */
public interface AdminUsersApi {

    /**
     * 是否是新用户
     *
     * @param userId     用户编号
     * @param limitDays  积分
     */
    Boolean isNewUser(Long userId, Long limitDays);


}
