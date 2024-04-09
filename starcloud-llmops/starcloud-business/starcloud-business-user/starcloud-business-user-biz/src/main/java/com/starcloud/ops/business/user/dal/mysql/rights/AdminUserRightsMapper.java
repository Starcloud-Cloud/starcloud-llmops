package com.starcloud.ops.business.user.dal.mysql.rights;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsPageReqVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AppAdminUserRightsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 用户积分记录 Mapper
 *
 * @author QingX
 */
@Mapper
public interface AdminUserRightsMapper extends BaseMapperX<AdminUserRightsDO> {

    default PageResult<AdminUserRightsDO> selectPage(AdminUserRightsPageReqVO reqVO, Set<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserRightsDO>()
                .inIfPresent(AdminUserRightsDO::getUserId, userIds)
                .eqIfPresent(AdminUserRightsDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AdminUserRightsDO::getBizType, reqVO.getBizType())
                .likeIfPresent(AdminUserRightsDO::getTitle, reqVO.getTitle())
                .orderByDesc(AdminUserRightsDO::getId));
    }

    default PageResult<AdminUserRightsDO> selectPage(Long userId, AppAdminUserRightsPageReqVO pageVO) {
        return selectPage(pageVO, new LambdaQueryWrapperX<AdminUserRightsDO>()
                .eqIfPresent(AdminUserRightsDO::getBizType, pageVO.getBizId())
                .eqIfPresent(AdminUserRightsDO::getUserId, userId)
                .orderByDesc(AdminUserRightsDO::getId));
    }


    default List<AdminUserRightsDO> selectListByStatusAndValidTimeLt(Integer status, LocalDateTime now) {
        return selectList(new LambdaQueryWrapper<AdminUserRightsDO>()
                .lt(AdminUserRightsDO::getValidStartTime, now)
                .lt(AdminUserRightsDO::getValidEndTime, now)
                .eq(AdminUserRightsDO::getStatus, status)
        );
    }

    default Integer updateByIdAndStatus(Long id, Integer status, AdminUserRightsDO update) {
        return update(update, new LambdaUpdateWrapper<AdminUserRightsDO>()
                .eq(AdminUserRightsDO::getId, id).eq(AdminUserRightsDO::getStatus, status));

    }


    default AdminUserRightsDO findLatestExpirationByLevel(Long userId, Long levelId) {

        LambdaQueryWrapper<AdminUserRightsDO> wrapper = Wrappers.lambdaQuery(AdminUserRightsDO.class)
                .eq(AdminUserRightsDO::getUserId, userId)
                .eq(AdminUserRightsDO::getUserLevelId, levelId)
                .ge(AdminUserRightsDO::getValidStartTime, LocalDateTime.now())
                .ge(AdminUserRightsDO::getValidEndTime, LocalDateTime.now())
                .eq(AdminUserRightsDO::getStatus, AdminUserRightsStatusEnum.EXPIRE.getType())
                .orderByDesc(AdminUserRightsDO::getValidEndTime)
                .last("limit 1");
        return selectOne(wrapper);
    }
}
