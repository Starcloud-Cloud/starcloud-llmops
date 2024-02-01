package com.starcloud.ops.business.user.service.invite;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleCreateReqVO;
import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleUpdateReqVO;
import com.starcloud.ops.business.user.convert.invite.AdminUserInviteRuleConvert;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteRuleDO;
import com.starcloud.ops.business.user.dal.mysql.invite.AdminUserInviteRuleMapper;
import com.starcloud.ops.business.user.enums.invite.InviteRuleTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminUserInviteRuleServiceImpl implements AdminUserInviteRuleService {

    @Resource
    private AdminUserInviteRuleMapper adminUserInviteRuleMapper;

    /**
     * 创建规则
     *
     * @param createReqVO 规则Vo
     */
    @Override
    public Long createRule(AdminUserInviteRuleCreateReqVO createReqVO) {

        AdminUserInviteRuleDO rule = AdminUserInviteRuleConvert.INSTANCE.convert(createReqVO);
        // 根据配置类型 分析应该禁用的规则类型
        List<Integer> disabledTypes = InviteRuleTypeEnum.getDisabledTypes(createReqVO.getType());

        List<AdminUserInviteRuleDO> disabledRules = adminUserInviteRuleMapper.selectShouldDisableRule(disabledTypes);
        // 先禁用同类型配置
        disabledRules.forEach(rules -> getSelf().disableRule(rule.getId()));

        // 增加配置
        adminUserInviteRuleMapper.insert(rule);
        // 重新排序
        getSelf().ResetSort(rule);

        return rule.getId();
    }

    /**
     * 修改规则
     *
     * @param updateReqVO 用户ID
     */
    @Override
    public void updateRule(AdminUserInviteRuleUpdateReqVO updateReqVO) {
        AdminUserInviteRuleDO rule = AdminUserInviteRuleConvert.INSTANCE.convert(updateReqVO);
        // 根据配置类型 分析应该禁用的规则类型
        List<Integer> disabledTypes = InviteRuleTypeEnum.getDisabledTypes(updateReqVO.getType());

        List<AdminUserInviteRuleDO> disabledRules = adminUserInviteRuleMapper.selectShouldDisableRule(disabledTypes);
        // 禁用相悖类型配置
        disabledRules.forEach(rules -> {
            if (!rules.getId().equals(rules.getId())) getSelf().disableRule(rules.getId());
        });
        // 增加配置
        adminUserInviteRuleMapper.updateById(rule);
        // 重新排序
        getSelf().ResetSort(rule);
    }

    /**
     * 删除规则
     *
     * @param id 编号
     * @return 邀请记录列表
     */
    @Override
    public void delete(Long id) {
        adminUserInviteRuleMapper.deleteById(id);
    }

    /**
     * 启用邀新规则
     *
     * @param id 编号
     * @return 邀请记录列表
     */
    @Override
    public void enableRule(Long id) {
        adminUserInviteRuleMapper.updateById(new AdminUserInviteRuleDO().setId(id).setStatus(CommonStatusEnum.ENABLE.getStatus()));
    }

    /**
     * 禁用规则
     *
     * @param id 编号
     * @return 邀请记录列表
     */
    @Override
    public void disableRule(Long id) {
        adminUserInviteRuleMapper.updateById(new AdminUserInviteRuleDO().setId(id).setStatus(CommonStatusEnum.DISABLE.getStatus()));
    }

    /**
     * 获得【启用】状态的邀请规则
     *
     * @return 邀请记录列表
     */
    @Override
    public AdminUserInviteRuleDO getEnableRule() {
        return adminUserInviteRuleMapper.selectEnableRules();
    }

    private void ResetSort(AdminUserInviteRuleDO ruleDO) {

        List<AdminUserInviteRuleDO.Rule> inviteRule = ruleDO.getInviteRule();
        if (1 == inviteRule.size()) {
            return;
        }
        // 使用Collectors.sort进行排序并重新设置sort字段
        inviteRule.sort(Comparator.comparingLong(AdminUserInviteRuleDO.Rule::getCount));
        int currentSort = 1;
        for (AdminUserInviteRuleDO.Rule rule : inviteRule) {
            rule.setSort(currentSort++);
        }
        adminUserInviteRuleMapper.updateById(new AdminUserInviteRuleDO().setId(ruleDO.getId()).setInviteRule(inviteRule));
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AdminUserInviteRuleServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
