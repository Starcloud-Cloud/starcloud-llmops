package com.starcloud.ops.business.user.dal.mysql.level;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starcloud.ops.business.user.controller.admin.level.vo.record.AdminUserLevelRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级记录 Mapper
 *
 * @author owen
 */
@Mapper
public interface AdminUserLevelRecordMapper extends BaseMapperX<AdminUserLevelRecordDO> {

    default PageResult<AdminUserLevelRecordDO> selectPage(AdminUserLevelRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserLevelRecordDO>()
                .eqIfPresent(AdminUserLevelRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AdminUserLevelRecordDO::getLevelId, reqVO.getLevelId())
                .betweenIfPresent(AdminUserLevelRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdminUserLevelRecordDO::getId));
    }

    default AdminUserLevelRecordDO findLatestExpirationByLevel(Long userId, Long levelId) {
        return selectOne(new QueryWrapper<AdminUserLevelRecordDO>()
                .eq("user_id", userId)
                .eq("level_id", levelId)
                .orderByDesc("valid_start_time")
                .last("limit 1"));
    }


}
