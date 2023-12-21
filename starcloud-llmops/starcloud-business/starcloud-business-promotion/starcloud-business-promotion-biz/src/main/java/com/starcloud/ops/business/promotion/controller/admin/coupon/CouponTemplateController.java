package com.starcloud.ops.business.promotion.controller.admin.coupon;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.template.*;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.template.AppCouponTemplatePageReqVO;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.template.AppCouponTemplateRespVO;
import com.starcloud.ops.business.promotion.convert.coupon.CouponTemplateConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.enums.common.PromotionProductScopeEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTakeTypeEnum;
import com.starcloud.ops.business.promotion.service.coupon.CouponService;
import com.starcloud.ops.business.promotion.service.coupon.CouponTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 优惠劵模板")
@RestController
@RequestMapping("/llm/promotion/coupon-template")
@Validated
public class CouponTemplateController {
    @Resource
    private CouponService couponService;

    @Resource
    private CouponTemplateService couponTemplateService;
    @Resource
    private ProductSpuApi productSpuApi;

    @PostMapping("/create")
    @Operation(summary = "创建优惠劵模板")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:create')")
    public CommonResult<Long> createCouponTemplate(@Valid @RequestBody CouponTemplateCreateReqVO createReqVO) {
        return success(couponTemplateService.createCouponTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新优惠劵模板")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:update')")
    public CommonResult<Boolean> updateCouponTemplate(@Valid @RequestBody CouponTemplateUpdateReqVO updateReqVO) {
        couponTemplateService.updateCouponTemplate(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新优惠劵模板状态")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:update')")
    public CommonResult<Boolean> updateCouponTemplateStatus(@Valid @RequestBody CouponTemplateUpdateStatusReqVO reqVO) {
        couponTemplateService.updateCouponTemplateStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除优惠劵模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:delete')")
    public CommonResult<Boolean> deleteCouponTemplate(@RequestParam("id") Long id) {
        couponTemplateService.deleteCouponTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得优惠劵模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:query')")
    public CommonResult<CouponTemplateRespVO> getCouponTemplate(@RequestParam("id") Long id) {
        CouponTemplateDO couponTemplate = couponTemplateService.getCouponTemplate(id);
        return success(CouponTemplateConvert.INSTANCE.convert(couponTemplate));
    }

    @GetMapping("/page")
    @Operation(summary = "获得优惠劵模板分页")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:query')")
    public CommonResult<PageResult<CouponTemplateRespVO>> getCouponTemplatePage(@Valid CouponTemplatePageReqVO pageVO) {
        PageResult<CouponTemplateDO> pageResult = couponTemplateService.getCouponTemplatePage(pageVO);
        return success(CouponTemplateConvert.INSTANCE.convertPage(pageResult));
    }

    //=====================ADMIN==========USER========================

    @GetMapping("/u/list")
    @Operation(summary = "系统会员-获得优惠劵模版列表")
    @Parameters({
            @Parameter(name = "spuId", description = "商品 SPU 编号"), // 目前主要给商品详情使用
            @Parameter(name = "useType", description = "使用类型"),
            @Parameter(name = "count", description = "数量", required = true)
    })
    public CommonResult<List<AppCouponTemplateRespVO>> getCouponTemplateList(
            @RequestParam(value = "spuId", required = false) Long spuId,
            @RequestParam(value = "productScope", required = false) Integer productScope,
            @RequestParam(value = "count", required = false, defaultValue = "10") Integer count) {
        // 1.1 处理查询条件：商品范围编号
        Long productScopeValue = getProductScopeValue(productScope, spuId);
        // 1.2 处理查询条件：领取方式 = 直接领取
        List<Integer> canTakeTypes = Collections.singletonList(CouponTakeTypeEnum.USER.getValue());

        // 2. 查询
        List<CouponTemplateDO> list = couponTemplateService.getCouponTemplateList(canTakeTypes, productScope,
                productScopeValue, count);

        // 3.1 领取数量
        Map<Long, Boolean> canCanTakeMap = couponService.getUserCanCanTakeMap(getLoginUserId(), list);
        // 3.2 拼接返回
        return success(CouponTemplateConvert.INSTANCE.convertAppList(list, canCanTakeMap));
    }

    @GetMapping("/u/page")
    @Operation(summary = "系统会员-获得优惠劵模版分页")
    public CommonResult<PageResult<AppCouponTemplateRespVO>> getCouponTemplatePage(AppCouponTemplatePageReqVO pageReqVO) {
        // 1.1 处理查询条件：商品范围编号
        Long productScopeValue = getProductScopeValue(pageReqVO.getProductScope(), pageReqVO.getSpuId());
        // 1.2 处理查询条件：领取方式 = 直接领取
        List<Integer> canTakeTypes = Collections.singletonList(CouponTakeTypeEnum.USER.getValue());

        // 2. 分页查询
        PageResult<CouponTemplateDO> pageResult = couponTemplateService.getCouponTemplatePage(
                CouponTemplateConvert.INSTANCE.convert(pageReqVO, canTakeTypes, pageReqVO.getProductScope(), productScopeValue));

        // 3.1 领取数量
        Map<Long, Boolean> canCanTakeMap = couponService.getUserCanCanTakeMap(getLoginUserId(), pageResult.getList());
        // 3.2 拼接返回
        return success(CouponTemplateConvert.INSTANCE.convertAppPage(pageResult, canCanTakeMap));
    }

    /**
     * 获得商品的使用范围编号
     *
     * @param productScope 商品范围
     * @param spuId        商品 SPU 编号
     * @return 商品范围编号
     */
    private Long getProductScopeValue(Integer productScope, Long spuId) {
        // 通用券：没有商品范围
        if (ObjectUtils.equalsAny(productScope, PromotionProductScopeEnum.ALL.getScope(), null)) {
            return null;
        }
        // 品类券：查询商品的品类编号
        if (Objects.equals(productScope, PromotionProductScopeEnum.CATEGORY.getScope()) && spuId != null) {
            ProductSpuRespDTO spu = productSpuApi.getSpu(spuId);
            return spu != null ? spu.getCategoryId() : null;
        }
        // 商品卷：直接返回
        return spuId;
    }
}
