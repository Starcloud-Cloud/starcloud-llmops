package com.starcloud.ops.business.user.dal.mysql.invite;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 邀请规则 Mapper
 *
 * @author Alancusack
 */
@Mapper
public interface AdminUserInviteRuleMapper extends BaseMapperX<AdminUserInviteRuleDO> {

    // default PageResult<AdminUserInviteRuleDO> selectPage(InvitationRecordsPageReqVO reqVO) {
    //     return selectPage(reqVO, new LambdaQueryWrapperX<AdminUserInviteDO>()
    //             .eqIfPresent(AdminUserInviteRuleDO::getInviterId, reqVO.getInviterId())
    //             .eqIfPresent(AdminUserInviteRuleDO::getInviteeId, reqVO.getInviteeId())
    //             .betweenIfPresent(AdminUserInviteRuleDO::getInvitationDate, reqVO.getInvitationDate())
    //             .betweenIfPresent(AdminUserInviteRuleDO::getCreateTime, reqVO.getCreateTime())
    //             .orderByDesc(AdminUserInviteRuleDO::getId));
    // }

    default List<AdminUserInviteRuleDO> selectSameTypeRule(Integer types) {
        LambdaQueryWrapper<AdminUserInviteRuleDO> wrapper =
                Wrappers.lambdaQuery(AdminUserInviteRuleDO.class)
                        .eq(AdminUserInviteRuleDO::getType, types)
                        .eq(AdminUserInviteRuleDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
        return selectList(wrapper);
    }

    default List<AdminUserInviteRuleDO> selectShouldDisableRule(List<Integer> types) {
        LambdaQueryWrapper<AdminUserInviteRuleDO> wrapper =
                Wrappers.lambdaQuery(AdminUserInviteRuleDO.class)
                        .in(AdminUserInviteRuleDO::getType, types)
                        .eq(AdminUserInviteRuleDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
        return selectList(wrapper);
    }


    default AdminUserInviteRuleDO selectEnableRules() {
        return selectOne(AdminUserInviteRuleDO::getStatus, CommonStatusEnum.ENABLE.getStatus());
    }


}
