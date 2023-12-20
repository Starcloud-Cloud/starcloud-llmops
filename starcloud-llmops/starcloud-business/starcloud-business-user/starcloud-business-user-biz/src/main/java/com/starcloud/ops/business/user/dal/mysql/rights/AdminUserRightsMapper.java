package com.starcloud.ops.business.user.dal.mysql.rights;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.rights.AdminUserRightsDO;
import org.apache.ibatis.annotations.Mapper;

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

    default PageResult<AdminUserRightsDO> selectPage(Long userId, PageParam pageVO) {
        return selectPage(pageVO, new LambdaQueryWrapperX<AdminUserRightsDO>()
                .eq(AdminUserRightsDO::getUserId, userId)
                .orderByDesc(AdminUserRightsDO::getId));
    }

}
