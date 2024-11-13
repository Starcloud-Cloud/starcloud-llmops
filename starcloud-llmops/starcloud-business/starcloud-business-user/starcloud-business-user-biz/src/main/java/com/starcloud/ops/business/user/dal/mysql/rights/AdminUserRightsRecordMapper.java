package com.starcloud.ops.business.user.dal.mysql.rights;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.starcloud.ops.business.user.api.rights.dto.StatisticsUserRightReqDTO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.record.AdminUserRightsRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 用户积分记录 Mapper
 *
 * @author QingX
 */
@Mapper
public interface AdminUserRightsRecordMapper extends BaseMapperX<AdminUserRightsRecordDO> {

    default PageResult<AdminUserRightsRecordDO> selectPage(AdminUserRightsRecordPageReqVO reqVO, Set<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserRightsRecordDO>()
                .inIfPresent(AdminUserRightsRecordDO::getUserId, userIds)
                .eqIfPresent(AdminUserRightsRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AdminUserRightsRecordDO::getBizType, reqVO.getBizType())
                .likeIfPresent(AdminUserRightsRecordDO::getTitle, reqVO.getTitle())
                .orderByDesc(AdminUserRightsRecordDO::getId));
    }

    default PageResult<AdminUserRightsRecordDO> selectPage(Long userId, PageParam pageVO) {
        return selectPage(pageVO, new LambdaQueryWrapperX<AdminUserRightsRecordDO>()
                .eq(AdminUserRightsRecordDO::getUserId, userId)
                .orderByDesc(AdminUserRightsRecordDO::getId));
    }


    @Select({"<script>",
            "SELECT  t1.user_id  as userId,t1.team_id as teamId,t1.rights_type,ABS(SUM(CASE WHEN t1.rights_type = 1 THEN t1.rights_amount ELSE 0 END)) AS magicBeanCounts,ABS(SUM(CASE WHEN t1.rights_type = 2 THEN t1.rights_amount ELSE 0 END)) AS imageCounts FROM system_user_rights_record t1",
            "WHERE t1.team_id = #{teamId}",
            "AND",
            "t1.user_id IN (",
            "<foreach collection='userIds' item='userId' separator=','>",
            "#{userId}",
            "</foreach>)",
            "GROUP BY  t1.user_id; ",
            "</script>"
    })
    List<StatisticsUserRightReqDTO>  calculateRightUsedByUser(@Param("teamId") Long teamId, @Param("userIds") List<Long> userIds);


    List<StatisticsUserRightReqDTO> statisticsUserRightsByBizId(@Param("bizIdList") List<String> bizIdList);

}
