package com.starcloud.ops.business.user.api.user;

import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import com.starcloud.ops.business.user.service.user.StarUserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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


    /**
     * 用户权益和等级统一新增处理
     *
     * @param userId                  用户编号
     * @param bizType                 业务类型
     * @param bizId                   业务 编号
     * @param rightsAndLevelCommonDTO 用户权益数据
     */
    @Override
    public void insertUserRightsAndLevel(AdminUserRightsAndLevelCommonDTO rightsAndLevelCommonDTO, Long userId, Integer bizType, String bizId, int orderNums) {
        log.info("【用户权益和等级统一新增处理,当前数据用户编号为{},业务类型为{},业务编号为{},权益数据为{}】", userId, bizType, bizId, rightsAndLevelCommonDTO);
        // 增加商品数量 根据商品数量控制权益中团队人数
        adminUserLevelService.createLevelRecord(rightsAndLevelCommonDTO, userId, bizType, bizId, orderNums);

        adminUserRightsService.createRights(rightsAndLevelCommonDTO, userId, bizType, bizId);
    }
}
