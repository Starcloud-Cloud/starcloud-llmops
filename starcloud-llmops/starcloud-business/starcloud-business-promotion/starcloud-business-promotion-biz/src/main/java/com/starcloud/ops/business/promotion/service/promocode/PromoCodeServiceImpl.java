package com.starcloud.ops.business.promotion.service.promocode;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageReqVO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;
import com.starcloud.ops.business.promotion.dal.mysql.promocode.PromoCodeMapper;
import com.starcloud.ops.business.promotion.enums.promocode.PromoCodeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 优惠劵 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class PromoCodeServiceImpl implements PromoCodeService {

    // @Resource
    // private PromoCodeTemplateService promoCodeTemplateService;

    @Resource
    private PromoCodeMapper promoCodeMapper;

    @Resource
    private AdminUserApi adminUserApi;

    /**
     * 校验兑换码，包括状态、有限期
     * <p>
     * 1. 如果校验通过，则返回兑换码信息
     * 2. 如果校验不通过，则直接抛出业务异常
     *
     * @param id     兑换码编号
     * @param userId 用户编号
     * @return 兑换码信息
     */
    @Override
    public PromoCodeDO validPromoCode(Long id, Long userId) {

        PromoCodeDO PromoCode = promoCodeMapper.selectByIdAndUserId(id, userId);
        if (PromoCode == null) {
            // throw exception(PromoCode_NOT_EXISTS);
        }
        validPromoCode(PromoCode);
        return PromoCode;

    }

    /**
     * 校验兑换码，包括状态、有限期
     *
     * @param PromoCode 兑换码
     * @see #validPromoCode(Long, Long) 逻辑相同，只是入参不同
     */
    @Override
    public void validPromoCode(PromoCodeDO PromoCode) {
            // // 校验状态
            // if (ObjectUtil.notEqual(PromoCode.getStatus(), PromoCodeStatusEnum.UNUSED.getStatus())) {
            //     // throw exception(PromoCode_STATUS_NOT_UNUSED);
            // }
            // // 校验有效期；为避免定时器没跑，实际优惠劵已经过期
            // if (!LocalDateTimeUtils.isBetween(PromoCode.getValidStartTime(), PromoCode.getValidEndTime())) {
            //     // throw exception(PromoCode_VALID_TIME_NOT_NOW);
            // }
    }

    /**
     * 获得兑换码分页
     *
     * @param pageReqVO 分页查询
     * @return 兑换码分页
     */
    @Override
    public PageResult<PromoCodeDO> getPromoCodePage(PromoCodePageReqVO pageReqVO) {
        return null;
    }

    /**
     * 获取兑换码信息 【优惠码】
     * 优惠码与订单相关 仅可以在下单时获取
     *
     * @param promoCode 兑换码编号
     * @param userId    用户编号
     */
    @Override
    public void getPromoCode(String promoCode, Long userId) {

    }

    /**
     * 使用兑换码 【优惠码】
     * 优惠码与订单相关 仅可以在下单时获取
     *
     * @param id      兑换码编号
     * @param userId  用户编号
     * @param orderId 订单编号
     */
    @Override
    public void usePromoCode(Long id, Long userId, Long orderId) {

    }

    /**
     * 使用兑换码 【权益码】
     *
     * @param id     兑换码编号
     * @param userId 用户编号
     */
    @Override
    public void usePromoCode(Long id, Long userId) {

    }

    // @Override
    // public PromoCodeDO validPromoCode(Long id, Long userId) {
    //     PromoCodeDO PromoCode = promoCodeMapper.selectByIdAndUserId(id, userId);
    //     if (PromoCode == null) {
    //         // throw exception(PromoCode_NOT_EXISTS);
    //     }
    //     validPromoCode(PromoCode);
    //     return PromoCode;
    // }
    //
    // @Override
    // public void validPromoCode(PromoCodeDO PromoCode) {
    //     // // 校验状态
    //     // if (ObjectUtil.notEqual(PromoCode.getStatus(), PromoCodeStatusEnum.UNUSED.getStatus())) {
    //     //     throw exception(PromoCode_STATUS_NOT_UNUSED);
    //     // }
    //     // // 校验有效期；为避免定时器没跑，实际优惠劵已经过期
    //     // if (!LocalDateTimeUtils.isBetween(PromoCode.getValidStartTime(), PromoCode.getValidEndTime())) {
    //     //     throw exception(PromoCode_VALID_TIME_NOT_NOW);
    //     // }
    // }
    //
    // @Override
    // public PageResult<PromoCodeDO> getPromoCodePage(PromoCodePageReqVO pageReqVO) {
    //     // // 获得用户编号
    //     // if (StrUtil.isNotEmpty(pageReqVO.getNickname())) {
    //     //     List<MemberUserRespDTO> users = adminUserApi.getUser(pageReqVO.getNickname());
    //     //     if (CollUtil.isEmpty(users)) {
    //     //         return PageResult.empty();
    //     //     }
    //     //     pageReqVO.setUserIds(convertSet(users, MemberUserRespDTO::getId));
    //     // }
    //     // 分页查询
    //     return promoCodeMapper.selectPage(pageReqVO);
    // }
    //
    // @Override
    // public void usePromoCode(Long id, Long userId, Long orderId) {
    //     // 校验优惠劵
    //     validPromoCode(id, userId);
    //     //
    //     // // 更新状态
    //     // int updateCount = promoCodeMapper.updateByIdAndStatus(id, PromoCodeStatusEnum.UNUSED.getStatus(),
    //     //         new PromoCodeDO().setStatus(PromoCodeStatusEnum.USED.getStatus())
    //     //                 .setUseOrderId(orderId).setUseTime(LocalDateTime.now()));
    //     // if (updateCount == 0) {
    //     //     throw exception(PromoCode_STATUS_NOT_UNUSED);
    //     }
    // }
    //
    //
    // private void validatePromoCodeExists(Long id) {
    //     if (promoCodeMapper.selectById(id) == null) {
    //         throw exception(PromoCode_NOT_EXISTS);
    //     }
    // }
    //
    // @Override
    // public Long getUnusedPromoCodeCount(Long userId) {
    //     return promoCodeMapper.selectCountByUserIdAndStatus(userId, PromoCodeStatusEnum.UNUSED.getStatus());
    // }
    //
    // @Override
    // public void takePromoCode(Long templateId, Set<Long> userIds, PromoCodeTakeTypeEnum takeType) {
    //     PromoCodeTemplateDO template = promoCodeTemplateService.getTemplate(templateId);
    //     // 1. 过滤掉达到领取限制的用户
    //     removeTakeLimitUser(userIds, template);
    //     // 2. 校验优惠劵是否可以领取
    //     validatePromoCodeTemplateCanTake(template, userIds, takeType);
    //
    //     // 3. 批量保存优惠劵
    //     promoCodeMapper.insertBatch(convertList(userIds, userId -> PromoCodeConvert.INSTANCE.convert(template, userId)));
    //
    //     // 3. 增加优惠劵模板的领取数量
    //     promoCodeTemplateService.updateTemplateTakeCount(templateId, userIds.size());
    // }
    //
    // @Override
    // @Transactional(rollbackFor = Exception.class)
    // public void takePromoCodeByRegister(Long userId) {
    //     List<PromoCodeTemplateDO> templates = promoCodeTemplateService.getTemplateListByCodeType(PromoCodeTakeTypeEnum.REGISTER);
    //     for (PromoCodeTemplateDO template : templates) {
    //         takePromoCode(template.getId(), CollUtil.newHashSet(userId), PromoCodeTakeTypeEnum.REGISTER);
    //     }
    // }
    //
    // @Override
    // public Map<Long, Integer> getTakeCountMapByTemplateIds(Collection<Long> templateIds, Long userId) {
    //     if (CollUtil.isEmpty(templateIds)) {
    //         return Collections.emptyMap();
    //     }
    //     return promoCodeMapper.selectCountByUserIdAndTemplateIdIn(userId, templateIds);
    // }
    //
    // @Override
    // public List<PromoCodeDO> getMatchPromoCodeList(Long userId, AppPromoCodeMatchReqVO matchReqVO) {
    //     return promoCodeMapper.selectListByUserIdAndStatusAndUsePriceLeAndProductScope(userId,
    //             PromoCodeStatusEnum.UNUSED.getStatus(),
    //             matchReqVO.getPrice(), matchReqVO.getSpuIds(), matchReqVO.getCategoryIds());
    // }
    //
    // @Override
    // public int expirePromoCode() {
    //     // 1. 查询待过期的优惠券
    //     List<PromoCodeDO> list = promoCodeMapper.selectListByStatusAndValidEndTimeLe(
    //             PromoCodeStatusEnum.UNUSED.getStatus(), LocalDateTime.now());
    //     if (CollUtil.isEmpty(list)) {
    //         return 0;
    //     }
    //
    //     // 2. 遍历执行
    //     int count = 0;
    //     for (PromoCodeDO PromoCode : list) {
    //         try {
    //             boolean success = getSelf().expirePromoCode(PromoCode);
    //             if (success) {
    //                 count++;
    //             }
    //         } catch (Exception e) {
    //             log.error("[expirePromoCode][PromoCode({}) 更新为已过期失败]", PromoCode.getId(), e);
    //         }
    //     }
    //     return count;
    // }
    //
    // @Override
    // public Map<Long, Boolean> getUserCanCanTakeMap(Long userId, List<PromoCodeTemplateDO> templates) {
    //     // 1. 未登录时，都显示可以领取
    //     Map<Long, Boolean> userCanTakeMap = convertMap(templates, PromoCodeTemplateDO::getId, templateId -> true);
    //     if (userId == null) {
    //         return userCanTakeMap;
    //     }
    //
    //     // 2.1 过滤领取数量无限制的
    //     Set<Long> templateIds = convertSet(templates, PromoCodeTemplateDO::getId, template -> template.getTakeLimitCount() != -1);
    //     // 2.2 检查用户领取的数量是否超过限制
    //     if (CollUtil.isNotEmpty(templateIds)) {
    //         Map<Long, Integer> PromoCodeTakeCountMap = this.getTakeCountMapByTemplateIds(templateIds, userId);
    //         for (PromoCodeTemplateDO template : templates) {
    //             Integer takeCount = PromoCodeTakeCountMap.get(template.getId());
    //             userCanTakeMap.put(template.getId(), takeCount == null || takeCount < template.getTakeLimitCount());
    //         }
    //     }
    //     return userCanTakeMap;
    // }
    //
    // /**
    //  * 过期单个优惠劵
    //  *
    //  * @param PromoCode 优惠劵
    //  * @return 是否过期成功
    //  */
    // private boolean expirePromoCode(PromoCodeDO PromoCode) {
    //     // 更新记录状态
    //     int updateRows = promoCodeMapper.updateByIdAndStatus(PromoCode.getId(), PromoCodeStatusEnum.UNUSED.getStatus(),
    //             new PromoCodeDO().setStatus(PromoCodeStatusEnum.EXPIRE.getStatus()));
    //     if (updateRows == 0) {
    //         log.error("[expirePromoCode][PromoCode({}) 更新为已过期失败]", PromoCode.getId());
    //         return false;
    //     }
    //     log.info("[expirePromoCode][PromoCode({}) 更新为已过期成功]", PromoCode.getId());
    //     return true;
    // }
    //
    // /**
    //  * 校验优惠券是否可以领取
    //  *
    //  * @param PromoCodeTemplate 优惠券模板
    //  * @param userIds        领取人列表
    //  * @param takeType       领取方式
    //  */
    // private void validatePromoCodeTemplateCanTake(PromoCodeTemplateDO PromoCodeTemplate, Set<Long> userIds, PromoCodeTakeTypeEnum takeType) {
    //     // 如果所有用户都领取过，则抛出异常
    //     if (CollUtil.isEmpty(userIds)) {
    //         throw exception(PromoCode_TEMPLATE_USER_ALREADY_TAKE);
    //     }
    //
    //     // 校验模板
    //     if (PromoCodeTemplate == null) {
    //         throw exception(PromoCode_TEMPLATE_NOT_EXISTS);
    //     }
    //     // 校验剩余数量
    //     if (PromoCodeTemplate.getTakeCount() + userIds.size() > PromoCodeTemplate.getTotalCount()) {
    //         throw exception(PromoCode_TEMPLATE_NOT_ENOUGH);
    //     }
    //     // 校验"固定日期"的有效期类型是否过期
    //     if (PromoCodeTemplateValidityTypeEnum.DATE.getType().equals(PromoCodeTemplate.getValidityType())) {
    //         if (LocalDateTimeUtils.beforeNow(PromoCodeTemplate.getValidEndTime())) {
    //             throw exception(PromoCode_TEMPLATE_EXPIRED);
    //         }
    //     }
    //     // 校验领取方式
    //     if (ObjectUtil.notEqual(PromoCodeTemplate.getTakeType(), takeType.getValue())) {
    //         throw exception(PromoCode_TEMPLATE_CANNOT_TAKE);
    //     }
    // }
    //
    // /**
    //  * 过滤掉达到领取上线的用户
    //  *
    //  * @param userIds        用户编号数组
    //  * @param PromoCodeTemplate 优惠劵模版
    //  */
    // private void removeTakeLimitUser(Set<Long> userIds, PromoCodeTemplateDO PromoCodeTemplate) {
    //     if (PromoCodeTemplate.getTakeLimitCount() <= 0) {
    //         return;
    //     }
    //     // 查询已领过券的用户
    //     List<PromoCodeDO> alreadyTakePromoCodes = promoCodeMapper.selectListByTemplateIdAndUserId(PromoCodeTemplate.getId(), userIds);
    //     if (CollUtil.isEmpty(alreadyTakePromoCodes)) {
    //         return;
    //     }
    //     // 移除达到领取限制的用户
    //     Map<Long, Integer> userTakeCountMap = CollStreamUtil.groupBy(alreadyTakePromoCodes, PromoCodeDO::getUserId, Collectors.summingInt(c -> 1));
    //     userIds.removeIf(userId -> MapUtil.getInt(userTakeCountMap, userId, 0) >= PromoCodeTemplate.getTakeLimitCount());
    // }
    //

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PromoCodeServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
