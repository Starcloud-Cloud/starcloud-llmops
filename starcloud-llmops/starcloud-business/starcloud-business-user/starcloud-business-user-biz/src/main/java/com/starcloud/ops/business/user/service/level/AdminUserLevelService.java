package com.starcloud.ops.business.user.service.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.*;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;

import java.util.List;

/**
 * 会员等级记录 Service 接口
 *
 * @author owen
 */
public interface AdminUserLevelService {

    /**
     * 获得会员等级记录明细
     *
     * @param id 编号
     * @return 会员等级记录
     */
    AdminUserLevelDO getLevel(Long id);


    /**
     * 通过业务 ID 和业务类型获得会员等级记录明细
     *
     * @param bizType 业务类型
     * @param bizId   业务编号
     * @param userId  用户编号
     * @return 会员等级记录
     */
    AdminUserLevelDO getRecordByBiz(Integer bizType, Long bizId, Long userId);


    /**
     * 获得会员等级记录分页
     *
     * @param pageReqVO 分页查询
     * @return 会员等级记录分页
     */
    PageResult<AdminUserLevelDO> getLevelPage(AdminUserLevelPageReqVO pageReqVO);

    /**
     * 创建会员等级记录
     *
     * @param levelRecord 会员等级记录
     */
    AdminUserLevelDO createLevelRecord(AdminUserLevelCreateReqVO levelRecord);


    /**
     * 新增用户等级
     *
     * @param rightsAndLevelCommonDTO 统一权益 DTO
     * @param userId                  用户编号
     * @param bizType                 业务类型
     * @param bizId                   业务 编号
     * @return AdminUserLevelDO
     */
    AdminUserLevelDO createLevelRecord(AdminUserRightsAndLevelCommonDTO rightsAndLevelCommonDTO, Long userId, Integer bizType, String bizId, int orderNums);

    /**
     * 创建会员默认等级记录
     *
     * @param userId 用户 ID
     */
    void createInitLevelRecord(Long userId);

    /**
     * 获取会员下有效的等级列表
     *
     * @param userId 用户 ID
     */
    List<AdminUserLevelDetailRespVO> getLevelList(Long userId);

    /**
     * 等级过期提醒
     *
     * @param userId 用户 ID
     */
    NotifyExpiringLevelRespVO notifyExpiringLevel(Long userId);

    /**
     * 设置默认等级
     */
    @Deprecated
    void setInitLevel();

    /**
     * 【系统】自动过期用户等级
     *
     * @return 过期数量
     */
    int expireLevel();

    /**
     * 【系统】 过期用户等级操作
     *
     * @param levelDO 等级 DO
     */
    void expireLevelBySystem(AdminUserLevelDO levelDO);


    /**
     * 用户等级中配置的权益限制
     *
     * @param levelRightsCode 等级中权益类型
     * @param userId          用户编号
     * @return VO
     */
    AdminUserLevelLimitRespVO validateLevelRightsLimit(String levelRightsCode, Long userId);

    /**
     * 获取用户等级中配置的权益限制数
     *
     * @param levelRightsCode 等级中权益类型
     * @param userId          用户编号
     * @return VO
     */
    AdminUserLevelLimitUsedRespVO getLevelRightsLimitCount(String levelRightsCode, Long userId);


    /**
     * 【系统】验证用户等级和用户角色是否对应
     *
     * @param userId 用户编号（可以为空）
     */
    void validateLevelAndRole(Long userId);

    /**
     * @param adminUserLevelDO  用户等级 DO
     * @param adminUserRightsDO 用户权益 DO
     */
    Boolean checkLevelAndRights(AdminUserLevelDO adminUserLevelDO, AdminUserRightsDO adminUserRightsDO);

    /**
     * 获取团队的用户等级
     *
     * @param userId 用户编号
     * @return List<AdminUserLevelDetailRespVO>
     */
    List<AdminUserLevelDetailRespVO> getGroupLevelList(Long userId);
}
