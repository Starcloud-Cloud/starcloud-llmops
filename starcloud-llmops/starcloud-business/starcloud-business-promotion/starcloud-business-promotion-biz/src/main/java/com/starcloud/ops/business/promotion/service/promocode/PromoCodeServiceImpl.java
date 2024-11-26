package com.starcloud.ops.business.promotion.service.promocode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageReqVO;
import com.starcloud.ops.business.promotion.convert.promocode.PromoCodeConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.dal.mysql.promocode.PromoCodeMapper;
import com.starcloud.ops.business.promotion.enums.common.PromotionCodeTypeEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTakeTypeEnum;
import com.starcloud.ops.business.promotion.service.coupon.CouponService;
import com.starcloud.ops.business.user.api.level.AdminUserLevelApi;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.user.AdminUsersApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.promotion.enums.ErrorCodeConstants.*;

/**
 * 优惠劵 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class PromoCodeServiceImpl implements PromoCodeService {

    @Resource
    private PromoCodeTemplateService promoCodeTemplateService;

    @Resource
    private PromoCodeMapper promoCodeMapper;

    @Resource
    private CouponService couponService;

    @Resource
    private AdminUserLevelApi adminUserLevelApi;
    //
    @Resource
    private AdminUserRightsApi adminUserRightsApi;

    @Resource
    private AdminUsersApi adminUsersApi;



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
            throw exception(PROMO_CODE_NOT_EXISTS);
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
     *
     * @param code   兑换码编号
     * @param userId 用户编号
     */
    @Override
    public Long useCouponPromoCode(String code, Long userId) {

        PromoCodeTemplateDO template = promoCodeTemplateService.getTemplate(code, PromotionCodeTypeEnum.COUPON_CODE.getType());

        validatePromoCodeTemplateCanUse(template, userId);
        PromoCodeDO convert = PromoCodeConvert.INSTANCE.convert(template, userId);
        promoCodeMapper.insert(convert);
        return couponService.takeCoupon(template.getCouponTemplateId(), userId, CouponTakeTypeEnum.USER);
    }

    /**
     * 使用兑换码 【权益码】
     *
     * @param code   兑换码编号
     * @param userId 用户编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void usePromoCode(String code, Long userId) {
        PromoCodeTemplateDO template = promoCodeTemplateService.getTemplate(code, PromotionCodeTypeEnum.RIGHTS_CODE.getType());

        validatePromoCodeTemplateCanUse(template, userId);
        PromoCodeDO convert = PromoCodeConvert.INSTANCE.convert(template, userId);
        promoCodeMapper.insert(convert);

        // 3. 增加优惠劵模板的领取数量
        promoCodeTemplateService.updateTemplateTakeCount(template.getId(), 1);

        if (ObjectUtil.isNull(template.getGiveRights())) {
            log.error("兑换码中权益信息缺失，添加权益失败，当前兑换码为{}，当前用户为{}", code, userId);
            return;
        }
        // 增加权益
        adminUsersApi.insertUserRightsAndLevel(template.getGiveRights(),userId, AdminUserRightsBizTypeEnum.REDEEM_CODE.getType(), String.valueOf(convert.getId()),1);

    }

    /**
     * 统计会员领取优惠券的数量
     *
     * @param templateIds 优惠券模板编号列表
     * @param userId      用户编号
     * @return 领取优惠券的数量
     */
    @Override
    public Map<Long, Integer> getUseCountMapByTemplateIds(Collection<Long> templateIds, Long userId) {
        if (CollUtil.isEmpty(templateIds)) {
            return Collections.emptyMap();
        }
        return promoCodeMapper.selectCountByUserIdAndTemplateIdIn(userId, templateIds);
    }


    /**
     * 校验兑换码是否可以使用
     *
     * @param PromoCodeTemplate 优惠券模板
     * @param userId            领取人列表
     */
    private void validatePromoCodeTemplateCanUse(PromoCodeTemplateDO PromoCodeTemplate, Long userId) {

        // 校验模板
        if (PromoCodeTemplate == null) {
            throw exception(PROMO_CODE_TEMPLATE_NOT_EXISTS);
        }
        // 校验剩余数量
        if (PromoCodeTemplate.getTakeCount() + 1 > PromoCodeTemplate.getTotalCount()) {
            throw exception(PROMO_CODE_TEMPLATE_NOT_ENOUGH);
        }
        Long useCount = promoCodeMapper.selectCountByUserId(userId, PromoCodeTemplate.getId());
        // 校验超过兑换限制
        if (useCount >= PromoCodeTemplate.getTakeLimitCount()) {
            throw exception(PROMO_CODE_TEMPLATE_LIMIT, useCount);
        }
        // 校验是否过期
        if (LocalDateTimeUtils.beforeNow(PromoCodeTemplate.getValidEndTime())) {
            throw exception(PROMO_CODE_TEMPLATE_EXPIRED);
        }
    }


    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private PromoCodeServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
