package com.starcloud.ops.business.user.api.level;


import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelConfigRespDTO;
import com.starcloud.ops.business.user.api.level.dto.AdminUserLevelRespDTO;

import java.util.List;

/**
 * 会员等级 API 接口
 *
 * @author owen
 */
public interface AdminUserLevelApi {

    /**
     * 获得会员等级列表
     *
     * @param userId     会员ID
     * @return 会员等级
     */
    List<AdminUserLevelRespDTO> getAdminUserLevelList(Long userId);


    /**
     * 新增会员等级
     *
     * @param userId  会员ID
     * @param levelId 会员等级编号
     * @return 会员等级
     */
    void addAdminUserLevel(Long userId, Long levelId);


    /**
     * 过期会员等级
     *
     * @param userId  会员ID
     * @param levelId 会员等级编号
     * @return 会员等级
     */
    void expireAdminUserLevel(Long userId, Long levelId);

}
