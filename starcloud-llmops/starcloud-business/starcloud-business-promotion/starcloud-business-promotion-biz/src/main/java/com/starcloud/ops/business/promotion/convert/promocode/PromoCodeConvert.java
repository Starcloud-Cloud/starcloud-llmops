package com.starcloud.ops.business.promotion.convert.promocode;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.coupon.vo.coupon.CouponPageReqVO;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.*;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.enums.coupon.CouponStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 兑换码 Convert
 *
 * @author
 */
@Mapper
public interface PromoCodeConvert {

    PromoCodeConvert INSTANCE = Mappers.getMapper(PromoCodeConvert.class);

    PageResult<PromoCodePageItemRespVO> convertPage(PageResult<PromoCodeDO> page);

    // CouponRespDTO convert(PromoCodeDO bean);

    default PromoCodeDO convert(PromoCodeTemplateDO template, Long userId) {
        return new PromoCodeDO()
                .setTemplateId(template.getId())
                .setName(template.getName())
                .setUseTime(LocalDateTime.now())
                .setUserId(userId);
    }

    CouponPageReqVO convert(AppPromoCodePageReqVO pageReqVO, Collection<Long> userIds);

    PageResult<AppPromoCodeRespVO> convertAppPage(PageResult<PromoCodeDO> pageResult);

    List<AppPromoCodeMatchRespVO> convertList(List<PromoCodeDO> list);

}
