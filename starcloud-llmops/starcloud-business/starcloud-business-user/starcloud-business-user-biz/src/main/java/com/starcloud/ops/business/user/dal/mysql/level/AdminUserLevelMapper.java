package com.starcloud.ops.business.user.dal.mysql.level;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    /**
     * 获取失效的等级列表
     *
     * @param status 状态
     * @param now    时间
     * @return 数据列表
     */
    default List<AdminUserLevelDO> getUserLevelsNearExpiry(Integer status, LocalDateTime now) {
        return selectList(new LambdaQueryWrapper<AdminUserLevelDO>()
                .lt(AdminUserLevelDO::getValidStartTime, now)
                .lt(AdminUserLevelDO::getValidEndTime, now)
                .eq(AdminUserLevelDO::getStatus, status)
        );
    }

    /**
     * 获取有效【包含未生效】的等级数据列表
     * 如果用户编号（userId）为空 则查询所有的数据
     *
     * @param userId 用户编号
     * @return 数据列表
     */
    default List<AdminUserLevelDO> getValidAdminUserLevels(Long userId, List<Long> levels, LocalDateTime now) {

        return selectList(new LambdaQueryWrapperX<AdminUserLevelDO>()
                .eqIfPresent(AdminUserLevelDO::getUserId, userId)
                .inIfPresent(AdminUserLevelDO::getLevelId, levels)
                .eq(AdminUserLevelDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .and(wrapper -> wrapper
                        .or(w->w.ge(AdminUserLevelDO::getValidStartTime, now) // validStartTime <= NOW()
                                .le(AdminUserLevelDO::getValidEndTime, now))
                        .or(w-> w.gt(AdminUserLevelDO::getValidStartTime, now) // validStartTime > NOW()
                                        .gt(AdminUserLevelDO::getValidEndTime, now))
                       )
        );
    }

    default Integer updateByIdAndStatus(Long id, Integer status, AdminUserLevelDO update) {
        return update(update, new LambdaUpdateWrapper<AdminUserLevelDO>()
                .eq(AdminUserLevelDO::getId, id).eq(AdminUserLevelDO::getStatus, status));

    }


}
