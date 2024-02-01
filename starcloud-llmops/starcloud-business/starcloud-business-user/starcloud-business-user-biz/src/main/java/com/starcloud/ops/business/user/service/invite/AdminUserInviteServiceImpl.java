package com.starcloud.ops.business.user.service.invite;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.controller.admin.invite.vo.records.InvitationRecordsPageReqVO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteDO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteRuleDO;
import com.starcloud.ops.business.user.dal.mysql.invite.AdminUserInviteMapper;
import com.starcloud.ops.business.user.enums.invite.InviteRuleTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 邀请记录 Service 实现类
 *
 * @author Alancusack
 */
@Slf4j
@Service
@Validated
public class AdminUserInviteServiceImpl implements AdminUserInviteService {

    @Resource
    private AdminUserInviteMapper adminUserInviteMapper;

    @Resource
    private AdminUserInviteRuleService adminUserInviteRuleService;

    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    @Lazy
    private CouponApi couponApi;

    @Resource
    private SendUserMsgService sendUserMsgService;


    @Override
    public Long createInvitationRecords(Long inviterId, Long inviteeId) {
        try {
            log.info("[createInvitationRecords][增加邀请记录：邀请人用户ID({})｜被邀请人({})]", inviterId, inviteeId);
            // 插入
            AdminUserInviteDO invitationRecords = new AdminUserInviteDO();
            invitationRecords.setInviterId(inviterId);
            invitationRecords.setInviteeId(inviteeId);
            invitationRecords.setInvitationDate(LocalDateTimeUtil.now());
            invitationRecords.setCreator(String.valueOf(inviteeId));
            invitationRecords.setUpdater(String.valueOf(inviteeId));
            adminUserInviteMapper.insert(invitationRecords);
            return invitationRecords.getId();
        } catch (RuntimeException e) {
            log.error("[createInvitationRecords][增加邀请记录失败：邀请人用户ID({})｜被邀请人({})]", inviterId, inviteeId);
        }
        return 0L;

    }


    @Override
    public List<AdminUserInviteDO> getInvitationRecords(Long userId) {
        LambdaQueryWrapper<AdminUserInviteDO> queryWrapper = Wrappers.lambdaQuery(AdminUserInviteDO.class)
                .eq(AdminUserInviteDO::getInviterId, userId)
                .orderByAsc(AdminUserInviteDO::getId);
        return adminUserInviteMapper.selectList(queryWrapper);
    }

    @Override
    public List<AdminUserInviteDO> getInvitationRecordsList(Collection<Long> ids) {
        return adminUserInviteMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<AdminUserInviteDO> getInvitationRecordsPage(InvitationRecordsPageReqVO pageReqVO) {
        return adminUserInviteMapper.selectPage(pageReqVO);
    }

    /**
     * 获取当天邀请记录
     *
     * @param inviterId 用户ID
     * @return List<AdminUserInviteDO>
     */
    @Override
    public List<AdminUserInviteDO> getTodayInvitations(Long inviterId) {
        LocalDateTime now = LocalDateTimeUtil.now();
        return adminUserInviteMapper.selectList(Wrappers.lambdaQuery(AdminUserInviteDO.class)
                .eq(AdminUserInviteDO::getInviterId, inviterId)
                .between(AdminUserInviteDO::getCreateTime, LocalDateTimeUtil.beginOfDay(now), LocalDateTimeUtil.endOfDay(now)));
    }

    /**
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    @Override
    public Long getInviteCountByTimes(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return adminUserInviteMapper.selectCount(Wrappers.lambdaQuery(AdminUserInviteDO.class)
                .eq(AdminUserInviteDO::getInviteeId, userId)
                .between(AdminUserInviteDO::getCreateTime, startTime, endTime));
    }

    /**
     * @param inviteUserDO
     * @param inviteRecordsId
     */
    @Override
    public void setInviteRights(AdminUserDO inviteUserDO, Long inviteRecordsId) {
        // 获取当前系统启用的邀请规则
        AdminUserInviteRuleDO enableInviteRule = adminUserInviteRuleService.getEnableRule();
        if (Objects.isNull(enableInviteRule)) {
            log.info("【afterUserRegister】邀请人权益发放，当前系统不存在启用的邀请配置，跳过权益发放");
            return;
        }
        getSelf().executeInviteRuleByRuleType(inviteUserDO, enableInviteRule, inviteRecordsId);
    }

    /**
     * @param userId
     * @param inviteeId
     */
    @Override
    public void testInviteRule(Long userId, Long inviteeId) {

    }

    /**
     *
     */
    public void executeInviteRuleByRuleType(AdminUserDO inviteUserDO, AdminUserInviteRuleDO ruleDO, Long inviteRecordsId) {

        // 获取时间范围
        LocalDateTime endTime = LocalDateTime.now();

        LocalDateTime startTime = TimeRangeTypeEnum.getMinusTimeByRange(ruleDO.getTimeRange(), ruleDO.getTimeNums(), endTime);
        // 根据时间范围查询已经邀请人数
        Long count = getSelf().getInviteCountByTimes(inviteUserDO.getId(), startTime, endTime);
        // 判断是否满足要求
        List<AdminUserInviteRuleDO.Rule> inviteRule = ruleDO.getInviteRule();

        switch (InviteRuleTypeEnum.getByType(ruleDO.getType())) {
            case CYCLE_EFFECT:
                validateCycleEffectRule(inviteUserDO, count, ruleDO.getId(), inviteRule, inviteRecordsId);
                break;
            case SINGLE_EFFECT:
                validateSingleEffectRule(inviteUserDO, count, ruleDO.getId(), inviteRule, inviteRecordsId);
                break;
        }
    }

    /**
     * 验证循环规则
     *
     * @param inviteUserDO
     * @param inviteCount
     * @param ruleId
     * @param inviteRules
     * @param inviteRecordsId
     */
    private void validateCycleEffectRule(AdminUserDO inviteUserDO, Long inviteCount, Long ruleId, List<AdminUserInviteRuleDO.Rule> inviteRules, Long inviteRecordsId) {
        // 满足要求 添加权益
        for (AdminUserInviteRuleDO.Rule rule : inviteRules) {
            if (inviteCount % rule.getCount() == 0) {
                getSelf().setInviteUserRights(inviteUserDO, ruleId, rule, inviteRecordsId, inviteCount);
                break; // 如果你只需要找到一个匹配的规则，可以使用break退出循环
            }
        }

    }


    /**
     * 验证单次生效规则
     *
     * @param inviteUserDO
     * @param inviteCount
     * @param ruleId
     * @param inviteRules
     * @param inviteRecordsId
     */
    private void validateSingleEffectRule(AdminUserDO inviteUserDO, Long inviteCount, Long ruleId, List<AdminUserInviteRuleDO.Rule> inviteRules, Long inviteRecordsId) {

        for (AdminUserInviteRuleDO.Rule rule : inviteRules) {
            if (Objects.equals(rule.getCount(), inviteCount)) {
                getSelf().setInviteUserRights(inviteUserDO, ruleId, rule, inviteRecordsId, inviteCount);
                break; // 如果你只需要找到一个匹配的规则，可以使用break退出循环
            }
        }
    }


    public void setInviteUserRights(AdminUserDO inviteUserDO, Long ruleId, AdminUserInviteRuleDO.Rule inviteRule, Long inviteRecordsId, Long inviteCount) {

        int tag = 0;
        log.info("[executeInviteRuleByRuleType] 设置邀请人权益,邀请人 ID 为{},记录 ID 为{},规则为{}", inviteUserDO.getId(), inviteRecordsId, ruleId);
        if (Objects.nonNull(inviteRule.getGiveRights()) && inviteRule.getGiveRights().getGiveMagicBean() > 0 || inviteRule.getGiveRights().getGiveImage() > 0) {
            TenantUtils.execute(inviteUserDO.getTenantId(), () -> adminUserRightsService.createRights(inviteUserDO.getId(), inviteRule.getGiveRights().getGiveMagicBean(), inviteRule.getGiveRights().getGiveImage(), null, null, AdminUserRightsBizTypeEnum.USER_INVITE, String.valueOf(inviteRecordsId), null));
            tag++;
        } else {
            log.warn("[executeInviteRuleByRuleType] 当前规则暂无权益配置,规则ID为{}", ruleId);
        }

        if (CollUtil.isNotEmpty(inviteRule.getGiveCouponTemplateIds())) {
            List<Long> ids1 = JSON.parseArray(JSON.toJSONString(inviteRule.getGiveCouponTemplateIds()), Long.class);
            ids1.forEach(coupon -> couponApi.addCoupon(coupon, SetUtils.asSet(inviteUserDO.getId())));
        } else {
            log.warn("[executeInviteRuleByRuleType] 当前规则暂无优惠券配置,规则ID为{}", ruleId);
        }
        if (tag > 0) {
            sendMsg(inviteUserDO.getId(), inviteCount);
        }
    }

    private void sendMsg(Long userId, Long count) {
        // 发送信息
        try {
            sendUserMsgService.sendMsgToWx(userId, String.format(
                    "您已成功邀请了【%s】位朋友加入魔法AI大家庭，并成功解锁了一份独特的权益礼包" + "我们已经将这份珍贵的礼物送至您的账户中。" + "\n" + "\n" +
                            "值得一提的是，每邀请三位朋友，您都将再次解锁一个全新的权益包，彰显您的独特地位。", count));
        } catch (Exception e) {
            log.error("邀请达人公众号信息发送失败，userId={}", userId, e);
        }
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AdminUserInviteServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }


}
