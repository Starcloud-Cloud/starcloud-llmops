package com.starcloud.ops.business.promotion.convert.coupon;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponRespDTO;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageItemRespVO;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageReqVO;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.coupon.AppCouponMatchRespVO;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.coupon.AppCouponPageReqVO;
import com.starcloud.ops.business.promotion.controller.app.coupon.vo.coupon.AppCouponRespVO;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponDO;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.enums.coupon.CouponStatusEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTemplateValidityTypeEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 优惠劵 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface CouponConvert {

    CouponConvert INSTANCE = Mappers.getMapper(CouponConvert.class);

    PageResult<CouponPageItemRespVO> convertPage(PageResult<CouponDO> page);

    CouponRespDTO convert(CouponDO bean);

    List<CouponRespDTO> convertList02(List<CouponDO> bean);

    default CouponDO convert(CouponTemplateDO template, Long userId) {
        CouponDO couponDO = new CouponDO()
                .setTemplateId(template.getId())
                .setName(template.getName())
                .setTakeType(template.getTakeType())
                .setUsePrice(template.getUsePrice())
                .setProductScope(template.getProductScope())
                .setProductScopeValues(template.getProductScopeValues())
                .setDiscountType(template.getDiscountType())
                .setDiscountPercent(template.getDiscountPercent())
                .setDiscountPrice(template.getDiscountPrice())
                .setDiscountLimitPrice(template.getDiscountLimitPrice())
                .setStatus(CouponStatusEnum.UNUSED.getStatus())
                .setUserId(userId);
        if (CouponTemplateValidityTypeEnum.DATE.getType().equals(template.getValidityType())) {
            couponDO.setValidStartTime(template.getValidStartTime());
            couponDO.setValidEndTime(template.getValidEndTime());
        } else if (CouponTemplateValidityTypeEnum.TERM.getType().equals(template.getValidityType())) {
            couponDO.setValidStartTime(LocalDateTime.now().plusDays(template.getFixedStartTerm()));
            couponDO.setValidEndTime(LocalDateTime.now().plusDays(template.getFixedEndTerm()));
        }
        return couponDO;
    }

    CouponPageReqVO convert(AppCouponPageReqVO pageReqVO, Collection<Long> userIds);

    PageResult<AppCouponRespVO> convertAppPage(PageResult<CouponDO> pageResult);

    List<AppCouponMatchRespVO> convertList(List<CouponDO> list);

    AppCouponMatchRespVO convertList(CouponDO list);

}
