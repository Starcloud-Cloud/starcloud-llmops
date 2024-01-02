package com.starcloud.ops.business.user.api.rights;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
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
    private AdminUserService adminUserService;

    @Autowired
    private AdminUserApi adminUserApi;

    @Override
    public void reduceRights(Long userId, AdminUserRightsTypeEnum rightsType, Integer rightAmount,
                             Integer bizType, String bizId) {

        Long deptUserId = this.getDeptRightsUserId(userId, rightsType, rightAmount);
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

        Long deptUserId = this.getDeptRightsUserId(userId, rightsType, rightAmount);
        return super.calculateUserRightsEnough(deptUserId, rightsType, rightAmount);
    }


    /**
     * 获取应该创作权益的用户
     * 1，获取当前用户的部门
     * 2，判断是否是部门管理员
     * 1）是部门管理员，返回
     * 2）不是部门管理员，优先获取部门管理员。判断管理员有无剩余点数
     * 3，返回有剩余点的用户ID（管理员或当前用户）
     */
    protected Long getDeptRightsUserId(Long currentUserId, AdminUserRightsTypeEnum rightsType, Integer rightAmount) {

        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(currentUserId);
        //获取当前用户部门ID,只能获取用户当前激活的部门
        Long deptId = adminUserRespDTO.getDeptId();
        //找到部门的管理员
        DeptDO deptDO = deptService.getDept(deptId);
        if (deptDO != null) {
            //部门管理员不是当前用户，获取管理员ID
            if (deptDO.getLeaderUserId() == null) {
                //之前数据没配置, 这里做兼容处理
                return currentUserId;
            } else {

                if (!deptDO.getLeaderUserId().equals(currentUserId)) {
                    //判断管理员是否还有权益
                    if (super.calculateUserRightsEnough(deptDO.getLeaderUserId(), rightsType, rightAmount)) {
                        log.info("权益切换：当前用户[{}]切换到部门[{}]负责人[{}]", currentUserId, deptDO.getName(), deptDO.getLeaderUserId());
                        return deptDO.getLeaderUserId();
                    }
                }
            }

        }

        return currentUserId;
    }

}
