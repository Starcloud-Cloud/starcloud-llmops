package com.starcloud.ops.business.promotion.controller.admin.promocode;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.*;
import com.starcloud.ops.business.promotion.convert.promocode.PromoCodeTemplateConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.service.promocode.PromoCodeTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 兑换码模板")
@RestController
@RequestMapping("/llm/promotion/code/template")
@Validated
public class PromoCodeTemplateController {

    @Resource
    private PromoCodeTemplateService promoCodeTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建兑换码模板")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:create')")
    public CommonResult<Long> createCouponTemplate(@Valid @RequestBody PromoCodeTemplateCreateReqVO createReqVO) {
        return success(promoCodeTemplateService.createPromoCodeTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新兑换码模板")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:update')")
    public CommonResult<Boolean> updateCouponTemplate(@Valid @RequestBody PromoCodeTemplateUpdateReqVO updateReqVO) {
        promoCodeTemplateService.updatePromoCodeTemplate(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新兑换码模板状态")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:update')")
    public CommonResult<Boolean> updateCouponTemplateStatus(@Valid @RequestBody PromoCodeTemplateUpdateStatusReqVO reqVO) {
        promoCodeTemplateService.updateStatus(reqVO.getId(), reqVO.getStatus());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除兑换码模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:delete')")
    public CommonResult<Boolean> deleteCouponTemplate(@RequestParam("id") Long id) {
        promoCodeTemplateService.deleteTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得兑换码模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:query')")
    public CommonResult<PromoCodeTemplateRespVO> getCouponTemplate(@RequestParam("id") Long id) {
        PromoCodeTemplateDO template = promoCodeTemplateService.getTemplate(id);
        return success(PromoCodeTemplateConvert.INSTANCE.convert(template));
    }

    @GetMapping("/page")
    @Operation(summary = "获得兑换码模板分页")
    @PreAuthorize("@ss.hasPermission('promotion:coupon-template:query')")
    public CommonResult<PageResult<PromoCodeTemplateRespVO>> getCouponTemplatePage(@Valid PromoCodeTemplatePageReqVO pageVO) {
        PageResult<PromoCodeTemplateDO> pageResult = promoCodeTemplateService.getTemplatePage(pageVO);
        return success(PromoCodeTemplateConvert.INSTANCE.convertPage(pageResult));
    }

}
