package com.starcloud.ops.business.user.service.user.handler;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 邀请用户权益发放
 * {@link NewUserHandler} 实现类
 *
 * @author Cusack Alan
 */
@Slf4j
@Component
@Order(NewUserHandler.USER_LEVEL)
public class NewUserLevelHandler implements NewUserHandler {


    @Resource
    private AdminUserLevelService adminUserLevelService;

    /**
     * 用户注册后处理逻辑
     *
     * @param adminUserDO  新注册用户
     * @param inviteUserDO 邀请人信息
     */
    @Override
    public void afterUserRegister(AdminUserDO adminUserDO, AdminUserDO inviteUserDO) {
        log.info("【新用户注册 邀请人等级发放】，准备为注册用户等级发放");
        adminUserLevelService.createInitLevelRecord(adminUserDO.getId());
        log.info("【新用户注册 邀请人等级发放】用户注册，注册用户新增等级发放成功");
    }
}
