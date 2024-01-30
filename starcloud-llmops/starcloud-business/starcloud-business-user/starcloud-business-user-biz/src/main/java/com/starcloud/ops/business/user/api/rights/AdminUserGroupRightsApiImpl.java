package com.starcloud.ops.business.user.api.rights;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.RIGHTS_BIZ_NOT_SUPPORT;

/**
 * 用户积分的 API 实现类, 团队权益包装类
 *
 * @author owen
 */
@Primary
@Slf4j
@Service
@Validated
public class AdminUserGroupRightsApiImpl extends AdminUserRightsApiImpl {


    @Autowired
    private DeptService deptService;

    @Autowired
    private AdminUserApi adminUserApi;

    @Resource
    private UserDeptService userDeptService;

    @Override
    public void reduceRights(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount,
                             Integer bizType, String bizId) {
        UserDeptDO userDeptDO = this.getDeptRightsUserId(userId, rightsType, rightAmount);
        userDeptService.recordRights(userDeptDO,userId, rightsType, rightAmount);
        Long deptUserId = Optional.ofNullable(userDeptDO).map(UserDeptDO::getUserId).orElse(userId);
        super.reduceRights(deptUserId, rightsType, rightAmount, bizType, bizId);

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
    protected UserDeptDO getDeptRightsUserId(Long currentUserId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {
        UserDeptDO userDeptDO = userDeptService.selectSuperAdminId(currentUserId);
        if (userDeptDO == null) {
            //之前数据没配置, 这里做兼容处理
            return null;
        } else {
            if (!currentUserId.equals(userDeptDO.getUserId())) {
                //判断管理员是否还有权益
                if (super.calculateUserRightsEnough(userDeptDO.getUserId(), rightsType, rightAmount)) {
                    log.info("权益切换：当前用户[{}]切换到部门负责人[{}]", currentUserId, userDeptDO.getUserId());
                    return userDeptDO;
                }
            } else {
                return userDeptDO;
            }
        }
        return null;
    }

}
