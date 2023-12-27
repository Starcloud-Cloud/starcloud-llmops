package com.starcloud.ops.business.user.dal.mysql.level;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员等级记录 Mapper
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelMapper extends BaseMapperX<AdminUserLevelDO> {

    default PageResult<AdminUserLevelDO> selectPage(AdminUserLevelPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserLevelDO>()
                .eqIfPresent(AdminUserLevelDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AdminUserLevelDO::getLevelId, reqVO.getLevelId())
                .betweenIfPresent(AdminUserLevelDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdminUserLevelDO::getId));
    }

    default AdminUserLevelDO findLatestExpirationByLevel(Long userId, Long levelId) {
        return selectOne(new QueryWrapper<AdminUserLevelDO>()
                .eq("user_id", userId)
                .eq("level_id", levelId)
                .orderByDesc("valid_start_time")
                .last("limit 1"));
    }

    default List<AdminUserLevelDO> selectValidList(Long userId) {
        return selectList(new LambdaQueryWrapper<AdminUserLevelDO>()
                .eq(AdminUserLevelDO::getUserId, userId)
                .le(AdminUserLevelDO::getValidStartTime, LocalDateTime.now())
                .ge(AdminUserLevelDO::getValidEndTime, LocalDateTime.now())
                .eq(AdminUserLevelDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByDesc(AdminUserLevelDO::getLevelId)
        );
    }

    default List<AdminUserLevelDO> selectListByStatusAndValidTimeLt(Integer status, LocalDateTime now) {
        return selectList(new LambdaQueryWrapper<AdminUserLevelDO>()
                .lt(AdminUserLevelDO::getValidStartTime, now)
                .lt(AdminUserLevelDO::getValidEndTime, now)
                .eq(AdminUserLevelDO::getStatus, status)
        );
    }

    default Integer updateByIdAndStatus(Long id, Integer status, AdminUserLevelDO update) {
        return update(update, new LambdaUpdateWrapper<AdminUserLevelDO>()
                .eq(AdminUserLevelDO::getId, id).eq(AdminUserLevelDO::getStatus, status));

    }


}
