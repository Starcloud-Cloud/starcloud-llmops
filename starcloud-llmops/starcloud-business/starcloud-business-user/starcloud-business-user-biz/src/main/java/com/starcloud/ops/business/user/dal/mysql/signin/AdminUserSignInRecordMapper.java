package com.starcloud.ops.business.user.dal.mysql.signin;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starcloud.ops.business.user.controller.admin.signin.vo.record.AdminUserSignInRecordPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.signin.AdminUserSignInRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * 签到记录 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AdminUserSignInRecordMapper extends BaseMapperX<AdminUserSignInRecordDO> {

    default PageResult<AdminUserSignInRecordDO> selectPage(AdminUserSignInRecordPageReqVO reqVO, Set<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserSignInRecordDO>()
                .inIfPresent(AdminUserSignInRecordDO::getUserId, userIds)
                .eqIfPresent(AdminUserSignInRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AdminUserSignInRecordDO::getDay, reqVO.getDay())
                .betweenIfPresent(AdminUserSignInRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AdminUserSignInRecordDO::getId));
    }

    default PageResult<AdminUserSignInRecordDO> selectPage(Long userId, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AdminUserSignInRecordDO>()
                .eq(AdminUserSignInRecordDO::getUserId, userId)
                .orderByDesc(AdminUserSignInRecordDO::getId));
    }

    /**
     * 获取用户最近的签到记录信息，根据签到时间倒序
     *
     * @param userId 用户编号
     * @return 签到记录列表
     */
    default AdminUserSignInRecordDO selectLastRecordByUserId(Long userId) {
        return selectOne(new QueryWrapper<AdminUserSignInRecordDO>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
                .last("limit 1"));
    }

    default Long selectCountByUserId(Long userId) {
        return selectCount(AdminUserSignInRecordDO::getUserId, userId);
    }

    /**
     * 获取用户的签到记录列表信息
     *
     * @param userId 用户编号
     * @return 签到记录信息
     */
    default List<AdminUserSignInRecordDO> selectListByUserId(Long userId) {
        return selectList(AdminUserSignInRecordDO::getUserId, userId);
    }

}
