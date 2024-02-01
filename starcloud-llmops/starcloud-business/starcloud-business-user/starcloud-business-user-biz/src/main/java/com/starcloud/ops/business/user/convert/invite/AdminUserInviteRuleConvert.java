package com.starcloud.ops.business.user.convert.invite;

import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteRuleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 邀请记录 Convert
 *
 * @author Alancusack
 */
@Mapper
public interface AdminUserInviteRuleConvert {

    AdminUserInviteRuleConvert INSTANCE = Mappers.getMapper(AdminUserInviteRuleConvert.class);

    AdminUserInviteRuleDO convert(AdminUserInviteRuleCreateReqVO createReqVO);

    AdminUserInviteRuleDO convert(AdminUserInviteRuleUpdateReqVO updateReqVO);



}
