package com.starcloud.ops.business.promotion.service.promocode;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import com.starcloud.ops.business.product.api.category.ProductCategoryApi;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplateCreateReqVO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplatePageReqVO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplateUpdateReqVO;
import com.starcloud.ops.business.promotion.convert.promocode.PromoCodeTemplateConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.dal.mysql.promocode.PromoCodeTemplateMapper;
import com.starcloud.ops.business.promotion.enums.common.PromotionCodeTypeEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTemplateValidityTypeEnum;
import com.starcloud.ops.business.promotion.service.coupon.CouponTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.promotion.enums.ErrorCodeConstants.*;


/**
 * 兑换码模板 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PromoCodeTemplateServiceImpl implements PromoCodeTemplateService {

    @Resource
    private PromoCodeTemplateMapper PromoCodeTemplateMapper;

    @Resource
    private ProductCategoryApi productCategoryApi;

    @Resource
    private CouponTemplateService couponTemplateService;
    @Resource
    private ProductSpuApi productSpuApi;

    @Override
    public Long createPromoCodeTemplate(PromoCodeTemplateCreateReqVO createReqVO) {
        // 校验优惠码的有效性
        validateCoupon(createReqVO.getCodeType(), createReqVO.getCouponTemplateId(), createReqVO.getTotalCount(), createReqVO.getTakeLimitCount());
        // 校验权益码的有效性

        // 插入
        PromoCodeTemplateDO PromoCodeTemplate = PromoCodeTemplateConvert.INSTANCE.convert(createReqVO)
                .setStatus(CommonStatusEnum.ENABLE.getStatus());
        PromoCodeTemplateMapper.insert(PromoCodeTemplate);
        // 返回
        return PromoCodeTemplate.getId();
    }

    @Override
    public void updatePromoCodeTemplate(PromoCodeTemplateUpdateReqVO updateReqVO) {
        // 校验存在
        PromoCodeTemplateDO PromoCodeTemplate = validatePromoCodeTemplateExists(updateReqVO.getId());
        // 校验优惠码的有效性
        validateCoupon(updateReqVO.getCodeType(), updateReqVO.getCouponTemplateId(), updateReqVO.getTotalCount(), updateReqVO.getTakeLimitCount());
        // 校验权益码的有效性

        // 更新
        PromoCodeTemplateDO updateObj = PromoCodeTemplateConvert.INSTANCE.convert(updateReqVO);
        PromoCodeTemplateMapper.updateById(updateObj);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        // 校验存在
        validatePromoCodeTemplateExists(id);
        // 更新
        PromoCodeTemplateMapper.updateById(new PromoCodeTemplateDO().setId(id).setStatus(status));
    }

    @Override
    public void deleteTemplate(Long id) {
        // 校验存在
        validatePromoCodeTemplateExists(id);
        // 删除
        PromoCodeTemplateMapper.deleteById(id);
    }

    private PromoCodeTemplateDO validatePromoCodeTemplateExists(Long id) {
        PromoCodeTemplateDO PromoCodeTemplate = PromoCodeTemplateMapper.selectById(id);
        if (PromoCodeTemplate == null) {
            throw exception(PROMO_CODE_TEMPLATE_NOT_EXISTS);
        }
        return PromoCodeTemplate;
    }

    /**
     * 优惠码校验
     *
     * @param codeType
     * @param couponTemplateId
     * @param totalCount
     * @param takeLimitCount
     */
    private void validateCoupon(Integer codeType, Long couponTemplateId, Integer totalCount, Integer takeLimitCount) {
        if (PromotionCodeTypeEnum.COUPON_CODE.getType().equals(codeType)) {
            CouponTemplateDO couponTemplate = couponTemplateService.getCouponTemplate(couponTemplateId);
            // 校验模板
            if (couponTemplate == null) {
                throw exception(COUPON_TEMPLATE_NOT_EXISTS);
            }
            // 校验数量
            if (totalCount > couponTemplate.getTotalCount()) {
                throw exception(COUPON_TEMPLATE_NOT_ENOUGH);
            }

            // 校验兑换限制
            if (takeLimitCount > couponTemplate.getTakeLimitCount()) {
                throw exception(PROMO_CODE_TEMPLATE_NO_EQUAL_COUPON_LIMIT);
            }
            // 校验"固定日期"的有效期类型是否过期
            if (CouponTemplateValidityTypeEnum.DATE.getType().equals(couponTemplate.getValidityType())) {
                if (LocalDateTimeUtils.beforeNow(couponTemplate.getValidEndTime())) {
                    throw exception(PROMO_CODE_TEMPLATE_WITH_COUPON_DATE_TYPE);
                }
            }
        }

    }

    @Override
    public PromoCodeTemplateDO getTemplate(Long id) {
        return PromoCodeTemplateMapper.selectById(id);
    }

    /**
     * 获得兑换码模板
     *
     * @param code 兑换码编号
     * @return 兑换码模板
     */
    @Override
    public PromoCodeTemplateDO getTemplate(String code, Integer codeType) {
        return PromoCodeTemplateMapper.selectTemplateByCode(code, codeType);
    }

    @Override
    public PageResult<PromoCodeTemplateDO> getTemplatePage(PromoCodeTemplatePageReqVO pageReqVO) {
        return PromoCodeTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateTemplateTakeCount(Long id, int incrCount) {
        PromoCodeTemplateMapper.updateTakeCount(id, incrCount);
    }

    @Override
    public List<PromoCodeTemplateDO> getTemplateListByCodeType(PromotionCodeTypeEnum codeType) {
        return PromoCodeTemplateMapper.selectListByCodeType(codeType.getType());
    }

    @Override
    public List<PromoCodeTemplateDO> getTemplateList(List<Integer> codeTypes, Integer productScope,
                                                     Long productScopeValue, Integer count) {
        return PromoCodeTemplateMapper.selectList(codeTypes, productScope, productScopeValue, count);
    }

    @Override
    public List<PromoCodeTemplateDO> getTemplateList(Collection<Long> ids) {
        return PromoCodeTemplateMapper.selectBatchIds(ids);
    }

}
