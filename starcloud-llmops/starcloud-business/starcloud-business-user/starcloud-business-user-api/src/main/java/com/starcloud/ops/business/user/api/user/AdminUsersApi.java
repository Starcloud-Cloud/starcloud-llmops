package com.starcloud.ops.business.user.api.user;

import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;

/**
 * 用户积分的 API 接口
 *
 * @author owen
 */
public interface AdminUsersApi {

    /**
     * 是否是新用户
     *
     * @param userId 用户编号
     */
    Boolean isNewUser(Long userId);

    /**
     * 用户权益统一新增
     *
     * @param rightsAndLevelCommonDTO 用户权益
     * @param userId                  用户编号
     * @param bizType                 业务类型
     * @param bizId                   业务编号
     */
    void insertUserRightsAndLevel(AdminUserRightsAndLevelCommonDTO rightsAndLevelCommonDTO, Long userId, Integer bizType, String bizId, int orderNums);

}
