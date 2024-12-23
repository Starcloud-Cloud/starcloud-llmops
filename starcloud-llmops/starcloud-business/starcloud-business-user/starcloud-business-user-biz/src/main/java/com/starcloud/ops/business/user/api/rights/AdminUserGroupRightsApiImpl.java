package com.starcloud.ops.business.user.api.rights;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.dal.dataobject.dept.UserDeptDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 用户积分的 API 实现类, 团队权益包装类
 *
 * @author owen
 */
@Primary
@Slf4j
@Service
@Validated
public class  AdminUserGroupRightsApiImpl extends AdminUserRightsApiImpl {


    @Autowired
    private DeptService deptService;

    @Autowired
    private AdminUserApi adminUserApi;

    @Resource
    private UserDeptService userDeptService;

    @Override
    @DataPermission(enable = false)
    public void reduceRights(Long userId, Long teamOwnerId, Long teamId, AdminUserRightsTypeEnum rightsType, Integer rightAmount,
                             Integer bizType, String bizId)  {
        UserDeptDO userDeptDO = this.getDeptRightsUserId(userId, rightsType, rightAmount);
        userDeptService.recordRights(userDeptDO, userId, rightsType, rightAmount);
        Long deptUserId = Optional.ofNullable(userDeptDO).map(UserDeptDO::getUserId).orElse(userId);
        teamId = Optional.ofNullable(userDeptDO).map(UserDeptDO::getDeptId).orElse(null);
        super.reduceRights(userId, deptUserId, teamId, rightsType, rightAmount, bizType, bizId);

    }


    /**
     * @param reduceRightsDTO 权益扣减DTO
     */
    @Override
    @DataPermission(enable = false)
    public void reduceRights(ReduceRightsDTO reduceRightsDTO) {
        UserDeptDO userDeptDO = this.getDeptRightsUserId(reduceRightsDTO.getUserId(), AdminUserRightsTypeEnum.getByType(reduceRightsDTO.getRightType()), reduceRightsDTO.getReduceNums());
        reduceRightsDTO.setTeamOwnerId(Optional.ofNullable(userDeptDO).map(UserDeptDO::getUserId).orElse(reduceRightsDTO.getUserId()));
        reduceRightsDTO.setTeamId(Optional.ofNullable(userDeptDO).map(UserDeptDO::getDeptId).orElse(null));
        super.reduceRights(reduceRightsDTO);
    }

    /**
     * 判断权益是否充足
     *
     * @param userId      用户 ID
     * @param rightsType  权益类型
     * @param rightAmount 检测权益数 可以为空 为空 仅仅判断当前权益数大于 0
     * @return
     */
    @Override
    @DataPermission(enable = false)
    public Boolean calculateUserRightsEnough(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        UserDeptDO userDeptDO = this.getDeptRightsUserId(userId, rightsType, rightAmount);
        Long deptUserId = Optional.ofNullable(userDeptDO).map(UserDeptDO::getUserId).orElse(userId);
        return super.calculateUserRightsEnough(deptUserId, rightsType, rightAmount);
    }


    /**
     * 获取应该创作权益的用户   返回部门超级管理员id
     * 1，获取当前用户的部门
     * 2，判断是否是部门管理员
     * 1）是部门管理员，返回
     * 2）不是部门管理员，优先获取部门管理员。判断管理员有无剩余点数
     * 3，返回有剩余点的用户ID（管理员或当前用户）
     */
    /**
     * 这里关闭数据权限，主要是后面的 SQL查询会带上 kstry 线程中的其他正常用户的上下文，导致跟 powerjob 执行应用时候导致用户上下文冲突
     * 所以这里直接 关闭数据权限，这样下面的 关于权益的扣点 已经不需要用户上下文了，单ruiyi 本地比如SQL update会继续获取，所以后续的方法最好直接指定字段创作DB。
     */
    protected UserDeptDO getDeptRightsUserId(Long currentUserId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        UserDeptDO userDeptDO = userDeptService.selectSuperAdminId(currentUserId);
        if (!currentUserId.equals(userDeptDO.getUserId())) {
            // 判断管理员是否还有权益
            if (super.calculateUserRightsEnough(userDeptDO.getUserId(), rightsType, rightAmount)) {
                log.info("权益切换：当前用户[{}]切换到部门负责人[{}]", currentUserId, userDeptDO.getUserId());
                return userDeptDO;
            }
            return userDeptService.selectOwnerDept(currentUserId);
        } else {
            return userDeptDO;
        }
    }

}
