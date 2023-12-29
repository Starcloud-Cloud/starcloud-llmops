// package com.starcloud.ops.business.promotion.controller.admin.promocode;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.iocoder.yudao.framework.common.pojo.CommonResult;
// import cn.iocoder.yudao.framework.common.pojo.PageResult;
// import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
// import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
// import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
// import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
// import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageItemRespVO;
// import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageReqVO;
// import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponSendReqVO;
// import com.starcloud.ops.business.promotion.controller.app.coupon.vo.coupon.*;
// import com.starcloud.ops.business.promotion.convert.coupon.CouponConvert;
// import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponDO;
// import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
// import com.starcloud.ops.business.promotion.enums.coupon.CouponTakeTypeEnum;
// import com.starcloud.ops.business.promotion.service.promocode.PromoCodeService;
// import com.starcloud.ops.business.promotion.service.promocode.PromoCodeTemplateService;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.*;
//
// import javax.annotation.Resource;
// import javax.validation.Valid;
// import java.util.Collections;
// import java.util.List;
// import java.util.Map;
//
// import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
// import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
// import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
//
// @Tag(name = "管理后台 - 优惠劵")
// @RestController
// @RequestMapping("/llm/promotion/code")
// @Validated
// public class PromoCodeController {
//
//     @Resource
//     private PromoCodeService promoCodeService;
//     @Resource
//     private MemberUserApi memberUserApi;
//
//     @Resource
//     private PromoCodeTemplateService promoCodeTemplateService;
//
//     @DeleteMapping("/delete")
//     @Operation(summary = "回收优惠劵")
//     @Parameter(name = "id", description = "编号", required = true)
//     @PreAuthorize("@ss.hasPermission('promotion:coupon:delete')")
//     public CommonResult<Boolean> deleteCoupon(@RequestParam("id") Long id) {
//         promoCodeService.deleteCoupon(id);
//         return success(true);
//     }
//
//     @GetMapping("/page")
//     @Operation(summary = "获得优惠劵分页")
//     @PreAuthorize("@ss.hasPermission('promotion:coupon:query')")
//     public CommonResult<PageResult<CouponPageItemRespVO>> getCouponPage(@Valid CouponPageReqVO pageVO) {
//         PageResult<CouponDO> pageResult = promoCodeService.getCouponPage(pageVO);
//         PageResult<CouponPageItemRespVO> pageResulVO = CouponConvert.INSTANCE.convertPage(pageResult);
//         if (CollUtil.isEmpty(pageResulVO.getList())) {
//             return success(pageResulVO);
//         }
//
//         // 读取用户信息，进行拼接
//         Map<Long, MemberUserRespDTO> userMap = memberUserApi.getUserMap(convertSet(pageResult.getList(), CouponDO::getUserId));
//         pageResulVO.getList().forEach(itemRespVO -> MapUtils.findAndThen(userMap, itemRespVO.getUserId(),
//                 userRespDTO -> itemRespVO.setNickname(userRespDTO.getNickname())));
//         return success(pageResulVO);
//     }
//
//     @PostMapping("/send")
//     @Operation(summary = "发送优惠劵")
//     @PreAuthorize("@ss.hasPermission('promotion:coupon:send')")
//     public CommonResult<Boolean> sendCoupon(@Valid @RequestBody CouponSendReqVO reqVO) {
//         promoCodeService.takeCouponByAdmin(reqVO.getTemplateId(), reqVO.getUserIds());
//         return success(true);
//     }
//
//
//     //======================ADMIN ==========USER==========================
//     @PostMapping("/u/rights_code")
//     @Operation(summary = " 使用权益码")
//     @Parameter(name = "promoCode", description = "优惠券模板编号", required = true, example = "1024")
//     @PreAuthenticated
//     public CommonResult<Boolean> useRightsPromoCode(@Valid @RequestBody AppCouponTakeReqVO reqVO) {
//         // 1. 领取优惠劵
//         Long userId = getLoginUserId();
//         promoCodeService.useCoupon(reqVO.getTemplateId(), userId, CouponTakeTypeEnum.USER);
//
//         // 2. 检查是否可以继续领取
//         CouponTemplateDO couponTemplate = promoCodeTemplateService.getTemplate(reqVO.getTemplateId());
//         boolean canTakeAgain = true;
//         if (couponTemplate.getTakeLimitCount() != null && couponTemplate.getTakeLimitCount() > 0) {
//             Integer takeCount = promoCodeService.getTakeCount(reqVO.getTemplateId(), userId);
//             canTakeAgain = takeCount < couponTemplate.getTakeLimitCount();
//         }
//         return success(canTakeAgain);
//     }
//
//     //======================ADMIN ==========USER==========================
//     @PostMapping("/u/coupon_code")
//     @Operation(summary = "获取优惠码")
//     @Parameter(name = "promoCode", description = "优惠券模板编号", required = true, example = "1024")
//     @PreAuthenticated
//     public CommonResult<Boolean> getCouponPromoCode(@Valid @RequestBody AppCouponTakeReqVO reqVO) {
//         // 1. 领取优惠劵
//         Long userId = getLoginUserId();
//         promoCodeService.takeCoupon(reqVO.getTemplateId(), CollUtil.newHashSet(userId), CouponTakeTypeEnum.USER);
//
//         // 2. 检查是否可以继续领取
//         CouponTemplateDO couponTemplate = promoCodeTemplateService.getTemplate(reqVO.getTemplateId());
//         boolean canTakeAgain = true;
//         if (couponTemplate.getTakeLimitCount() != null && couponTemplate.getTakeLimitCount() > 0) {
//             Integer takeCount = promoCodeService.getTakeCount(reqVO.getTemplateId(), userId);
//             canTakeAgain = takeCount < couponTemplate.getTakeLimitCount();
//         }
//         return success(canTakeAgain);
//     }
//
//
//
//
//
// }
