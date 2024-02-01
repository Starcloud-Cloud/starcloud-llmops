package com.starcloud.ops.business.user.service.invite;

import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleUpdateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteRuleDO;

/**
 * 邀请记录 Service 接口
 *
 * @author Alancusack
 */
public interface AdminUserInviteRuleService {

    /**
     * 创建规则
     *
     * @param createReqVO 规则Vo
     */
    Long createRule(AdminUserInviteRuleCreateReqVO createReqVO);


    /**
     * 修改规则
     *
     * @param updateReqVO 用户ID
     */
    void updateRule(AdminUserInviteRuleUpdateReqVO updateReqVO);


    /**
     * 删除规则
     *
     * @param id 编号
     * @return 邀请记录列表
     */
    void delete(Long id);


    /**
     * 启用邀新规则
     *
     * @param id 编号
     * @return 邀请记录列表
     */
    void enableRule(Long id);


    /**
     * 禁用规则
     *
     * @param id 编号
     * @return 邀请记录列表
     */
    void disableRule(Long id);

    /**
     * 获得【启用】状态的邀请规则
     *
     * @return 邀请记录列表
     */
    AdminUserInviteRuleDO getEnableRule();
}
