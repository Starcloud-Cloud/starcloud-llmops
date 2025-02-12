package com.starcloud.ops.business.user.service.invite;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.SetUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.api.rights.dto.UserRightsBasicDTO;
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

    @Resource
    private DictDataService dictDataService;

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
     * @param userId    用户编号
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public Long getInviteCountByTimes(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return adminUserInviteMapper.selectCount(Wrappers.lambdaQuery(AdminUserInviteDO.class)
                .eq(AdminUserInviteDO::getInviterId, userId)
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
        log.info("用户{}当前邀请人数为{},开始检测是否满足规则", inviteUserDO.getId(), count);
        if (count ==0L){
            log.info("用户{}当前邀请人数为{},不否满足规则，直接跳出", inviteUserDO.getId(), count);
            return;
        }
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
    public void validateCycleEffectRule(AdminUserDO inviteUserDO, Long inviteCount, Long ruleId, List<AdminUserInviteRuleDO.Rule> inviteRules, Long inviteRecordsId) {
        // 满足要求 添加权益
        for (AdminUserInviteRuleDO.Rule rule : inviteRules) {
            if (inviteCount % rule.getCount() == 0L) {
                log.info("当前邀请人数满足邀请规则配置，当前邀请人数为{}规则配置为{}", inviteCount, inviteRules);
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
    public void validateSingleEffectRule(AdminUserDO inviteUserDO, Long inviteCount, Long ruleId, List<AdminUserInviteRuleDO.Rule> inviteRules, Long inviteRecordsId) {

        for (AdminUserInviteRuleDO.Rule rule : inviteRules) {
            if (Objects.equals(rule.getCount(), inviteCount)) {
                log.info("当前邀请人数满足邀请规则配置，当前邀请人数为{}规则配置为{}", inviteCount, inviteRules);
                getSelf().setInviteUserRights(inviteUserDO, ruleId, rule, inviteRecordsId, inviteCount);
                break; // 如果你只需要找到一个匹配的规则，可以使用break退出循环
            }
        }
    }


    public void setInviteUserRights(AdminUserDO inviteUserDO, Long ruleId, AdminUserInviteRuleDO.Rule inviteRule, Long inviteRecordsId, Long inviteCount) {

        int tag = 0;
        log.info("[executeInviteRuleByRuleType] 设置邀请人权益,邀请人 ID 为{},记录 ID 为{},规则为{}", inviteUserDO.getId(), inviteRecordsId, ruleId);
        if (Objects.nonNull(inviteRule.getGiveRights()) && Objects.nonNull(inviteRule.getGiveRights().getRightsBasicDTO())) {
            TenantUtils.execute(inviteUserDO.getTenantId(), () -> {
                UserRightsBasicDTO rightsBasicDTO = inviteRule.getGiveRights().getRightsBasicDTO();
                AddRightsDTO inviteUserRightsDTO = new AddRightsDTO()
                        .setUserId(inviteUserDO.getId())
                        .setMagicBean(rightsBasicDTO.getMagicBean())
                        .setMagicImage(rightsBasicDTO.getMagicImage())
                        .setMatrixBean(rightsBasicDTO.getMatrixBean())
                        .setTimeNums(1)
                        .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
                        .setBizId(String.valueOf(inviteRecordsId))
                        .setBizType(AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getType())
                        .setLevelId(null);
                adminUserRightsService.createRights(inviteUserRightsDTO);
            });
            tag++;
        } else {
            log.warn("[executeInviteRuleByRuleType] 当前规则暂无权益配置,规则ID为{}", ruleId);
        }

        if (CollUtil.isNotEmpty(inviteRule.getGiveCouponTemplateIds())) {
            List<Long> ids1 = JSON.parseArray(JSON.toJSONString(inviteRule.getGiveCouponTemplateIds()), Long.class);
            ids1.forEach(coupon -> couponApi.addCoupon(coupon, SetUtils.asSet(inviteUserDO.getId())));
            tag++;
        } else {
            log.warn("[executeInviteRuleByRuleType] 当前规则暂无优惠券配置,规则ID为{}", ruleId);
        }

        log.info("准备发送消息，当前 tag 为{}", tag);
        if (tag > 0) {
            TenantUtils.execute(inviteUserDO.getTenantId(), () -> sendMsg(inviteUserDO.getId(), inviteCount)
            );}
    }

    public void sendMsg(Long userId, Long count) {
        log.info("邀请达人公众号信息准备发送，userId={}", userId);
        // 发送信息
        try {

            DictDataDO wechatInviteMsg = dictDataService.getDictData("WECHAT_INVITE_MSG", StrUtil.format("msg_{}", TenantContextHolder.getTenantId()));
            sendUserMsgService.sendMsgToWx(userId, wechatInviteMsg.getRemark());
            log.info("邀请达人公众号信息发送成功，userId={}", userId);
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
