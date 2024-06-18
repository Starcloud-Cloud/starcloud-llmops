package com.starcloud.ops.business.promotion.controller.admin.promocode;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.starcloud.ops.business.product.api.sku.ProductSkuApi;
import com.starcloud.ops.business.product.api.sku.dto.ProductSkuRespDTO;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageItemRespVO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageReqVO;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.template.AppCouponTemplateRespVO;
import com.starcloud.ops.business.promotion.convert.coupon.CouponTemplateConvert;
import com.starcloud.ops.business.promotion.convert.promocode.PromoCodeConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.enums.common.PromotionCodeTypeEnum;
import com.starcloud.ops.business.promotion.enums.common.PromotionProductScopeEnum;
import com.starcloud.ops.business.promotion.service.coupon.CouponService;
import com.starcloud.ops.business.promotion.service.coupon.CouponTemplateService;
import com.starcloud.ops.business.promotion.service.promocode.PromoCodeService;
import com.starcloud.ops.business.promotion.service.promocode.PromoCodeTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static com.starcloud.ops.business.promotion.enums.ErrorCodeConstants.*;

@Tag(name = "管理后台 - 兑换码")
@RestController
@RequestMapping("/llm/promotion/code")
@Validated
public class PromoCodeController {

    @Resource
    private PromoCodeService promoCodeService;

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private PromoCodeTemplateService promoCodeTemplateService;

    @Resource
    private CouponTemplateService couponTemplateService;

    @Resource
    private CouponService couponService;

    @Resource
    private ProductSpuApi productSpuApi;

    @Resource
    private ProductSkuApi productSkuApi;


    @GetMapping("/page")
    @Operation(summary = "获得兑换码分页")
    @PreAuthorize("@ss.hasPermission('promotion:coupon:query')")
    public CommonResult<PageResult<PromoCodePageItemRespVO>> getCouponPage(@Valid PromoCodePageReqVO pageVO) {
        PageResult<PromoCodeDO> pageResult = promoCodeService.getPromoCodePage(pageVO);
        PageResult<PromoCodePageItemRespVO> pageResulVO = PromoCodeConvert.INSTANCE.convertPage(pageResult);
        if (CollUtil.isEmpty(pageResulVO.getList())) {
            return success(pageResulVO);
        }

        // // 读取用户信息，进行拼接
        // Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(convertSet(pageResult.getList(), CouponDO::getUserId));
        // pageResulVO.getList().forEach(itemRespVO -> MapUtils.findAndThen(userMap, itemRespVO.getUserId(),
        //         userRespDTO -> itemRespVO.setNickname(userRespDTO.getNickname())));
        return success(pageResulVO);
    }


    //======================ADMIN ==========USER==========================
    @PostMapping("/u/use_rights_code")
    @Operation(summary = "使用权益码")
    @Parameter(name = "code", description = "优惠券模板编号", required = true, example = "1024")
    @PreAuthenticated
    public CommonResult<Boolean> useRightsPromoCode(@RequestParam("code") String code) {
        promoCodeService.usePromoCode(code, getLoginUserId());
        return success(Boolean.TRUE);
    }


    @GetMapping("/u/get_coupon_code")
    @Operation(summary = "获取优惠码信息")
    @Parameters({
            @Parameter(name = "code", description = "兑换码", required = true),
            @Parameter(name = "spuId", description = "商品 SPU 编号", required = true),
    })
    @PreAuthenticated
    public CommonResult<AppCouponTemplateRespVO> getCouponPromoCode(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "spuId") Long spuId) {
        // 1. 领取兑换码
        Long userId = getLoginUserId();
        PromoCodeTemplateDO template = promoCodeTemplateService.getTemplate(code, PromotionCodeTypeEnum.COUPON_CODE.getType());
        if (Objects.isNull(template)) {
            throw exception(PROMO_CODE_TEMPLATE_NOT_EXISTS);
        }

        Integer useCount = promoCodeService.getUseCount(template.getId(), userId);

        if (useCount >= template.getTakeLimitCount()) {
            throw exception(PROMO_CODE_TEMPLATE_LIMIT, useCount);
        }


        // 2. 检查是否可以继续领取
        CouponTemplateDO couponTemplate = couponTemplateService.getCouponTemplate(template.getCouponTemplateId());
        boolean canTakeAgain = true;

        // 通用券 可以领取
        if (Objects.equals(PromotionProductScopeEnum.ALL.getScope(), couponTemplate.getProductScope())) {
            canTakeAgain = true;
        }
        // 商品券 校验商品
        if (Objects.equals(PromotionProductScopeEnum.SPU.getScope(), couponTemplate.getProductScope())) {
            if (CollUtil.contains(couponTemplate.getProductScopeValues(), spuId)) {
                canTakeAgain = true;
            } else {
                canTakeAgain = false;
            }

        }

        // 品类券 校验商品品类
        if (Objects.equals(PromotionProductScopeEnum.CATEGORY.getScope(), couponTemplate.getProductScope())) {
            ProductSpuRespDTO spu = productSpuApi.getSpu(spuId);
            if (spu != null) {
                canTakeAgain = CollUtil.contains(couponTemplate.getProductScopeValues(), spu.getCategoryId());
            } else {
                canTakeAgain = false;
            }
        }
        // 品类券 校验商品品类
        if (Objects.equals(PromotionProductScopeEnum.SKU.getScope(), couponTemplate.getProductScope())) {
            ProductSkuRespDTO sku = productSkuApi.getSku(spuId);
            if (sku != null) {
                canTakeAgain = CollUtil.contains(couponTemplate.getProductScopeValues(), sku.getId());
            } else {
                canTakeAgain = false;
            }

        }


        if (canTakeAgain && couponTemplate.getTakeLimitCount() != null && couponTemplate.getTakeLimitCount() > 0) {
            Integer takeCount = couponService.getTakeCount(couponTemplate.getId(), userId);
            canTakeAgain = takeCount < couponTemplate.getTakeLimitCount();
        }

        if (canTakeAgain) {
            return success(CouponTemplateConvert.INSTANCE.convertApp(couponTemplate));
        } else {
            throw exception(PROMO_CODE_NOT_EXISTS);
        }

        // return success(null);

    }


}
