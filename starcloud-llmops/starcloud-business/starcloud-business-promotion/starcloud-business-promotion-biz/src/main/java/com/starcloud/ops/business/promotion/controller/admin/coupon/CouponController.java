package com.starcloud.ops.business.promotion.controller.admin.coupon;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.security.core.annotations.PreAuthenticated;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageItemRespVO;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageReqVO;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponSendReqVO;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.coupon.*;
import com.starcloud.ops.business.promotion.convert.coupon.CouponConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponDO;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTakeTypeEnum;
import com.starcloud.ops.business.promotion.service.coupon.CouponService;
import com.starcloud.ops.business.promotion.service.coupon.CouponTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 优惠劵")
@RestController
@RequestMapping("/llm/promotion/coupon")
@Validated
public class CouponController {

    @Resource
    private CouponService couponService;
    @Resource
    private MemberUserApi memberUserApi;

    @Resource
    private CouponTemplateService couponTemplateService;

    @DeleteMapping("/delete")
    @Operation(summary = "回收优惠劵")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('promotion:coupon:delete')")
    public CommonResult<Boolean> deleteCoupon(@RequestParam("id") Long id) {
        couponService.deleteCoupon(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "获得优惠劵分页")
    @PreAuthorize("@ss.hasPermission('promotion:coupon:query')")
    public CommonResult<PageResult<CouponPageItemRespVO>> getCouponPage(@Valid CouponPageReqVO pageVO) {
        PageResult<CouponDO> pageResult = couponService.getCouponPage(pageVO);
        PageResult<CouponPageItemRespVO> pageResulVO = CouponConvert.INSTANCE.convertPage(pageResult);
        if (CollUtil.isEmpty(pageResulVO.getList())) {
            return success(pageResulVO);
        }

        // 读取用户信息，进行拼接
        Map<Long, MemberUserRespDTO> userMap = memberUserApi.getUserMap(convertSet(pageResult.getList(), CouponDO::getUserId));
        pageResulVO.getList().forEach(itemRespVO -> MapUtils.findAndThen(userMap, itemRespVO.getUserId(),
                userRespDTO -> itemRespVO.setNickname(userRespDTO.getNickname())));
        return success(pageResulVO);
    }

    @PostMapping("/send")
    @Operation(summary = "发送优惠劵")
    @PreAuthorize("@ss.hasPermission('promotion:coupon:send')")
    public CommonResult<Boolean> sendCoupon(@Valid @RequestBody CouponSendReqVO reqVO) {
        couponService.takeCouponByAdmin(reqVO.getTemplateId(), reqVO.getUserIds());
        return success(true);
    }


    //======================ADMIN ==========USER==========================
    @PostMapping("/u/take")
    @Operation(summary = "领取优惠劵")
    @Parameter(name = "templateId", description = "优惠券模板编号", required = true, example = "1024")
    @PreAuthenticated
    public CommonResult<Boolean> takeCoupon(@Valid @RequestBody AppCouponTakeReqVO reqVO) {
        // 1. 领取优惠劵
        Long userId = getLoginUserId();
        couponService.takeCoupon(reqVO.getTemplateId(), CollUtil.newHashSet(userId), CouponTakeTypeEnum.USER);

        // 2. 检查是否可以继续领取
        CouponTemplateDO couponTemplate = couponTemplateService.getCouponTemplate(reqVO.getTemplateId());
        boolean canTakeAgain = true;
        if (couponTemplate.getTakeLimitCount() != null && couponTemplate.getTakeLimitCount() > 0) {
            Integer takeCount = couponService.getTakeCount(reqVO.getTemplateId(), userId);
            canTakeAgain = takeCount < couponTemplate.getTakeLimitCount();
        }
        return success(canTakeAgain);
    }

    @GetMapping("/u/match-list")
    @Operation(summary = "系统会员-获得匹配指定商品的优惠劵列表", description = "用于下单页，展示优惠劵列表")
    public CommonResult<List<AppCouponMatchRespVO>> getMatchCouponList(AppCouponMatchReqVO matchReqVO) {
        // todo: 优化：优惠金额倒序
        return success(CouponConvert.INSTANCE.convertList(couponService.getMatchCouponList(getLoginUserId(), matchReqVO)));
    }

    @GetMapping("/u/page")
    @Operation(summary = "系统会员-我的优惠劵列表")
    @PreAuthenticated
    public CommonResult<PageResult<AppCouponRespVO>> getCouponPage(AppCouponPageReqVO pageReqVO) {
        PageResult<CouponDO> pageResult = couponService.getCouponPage(
                CouponConvert.INSTANCE.convert(pageReqVO, Collections.singleton(getLoginUserId())));
        return success(CouponConvert.INSTANCE.convertAppPage(pageResult));
    }

    @GetMapping(value = "/u/get-unused-count")
    @Operation(summary = "系统会员-获得未使用的优惠劵数量")
    @PreAuthenticated
    public CommonResult<Long> getUnusedCouponCount() {
        return success(couponService.getUnusedCouponCount(getLoginUserId()));
    }



}
