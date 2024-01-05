package com.starcloud.ops.business.promotion.service.promocode;

import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageReqVO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 兑换码 Service 接口
 *
 * @author
 */
public interface PromoCodeService {

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
    PromoCodeDO validPromoCode(Long id, Long userId);

    /**
     * 校验兑换码，包括状态、有限期
     *
     * @param PromoCode 兑换码
     * @see #validPromoCode(Long, Long) 逻辑相同，只是入参不同
     */
    void validPromoCode(PromoCodeDO PromoCode);

    /**
     * 获得兑换码分页
     *
     * @param pageReqVO 分页查询
     * @return 兑换码分页
     */
    PageResult<PromoCodeDO> getPromoCodePage(PromoCodePageReqVO pageReqVO);

    /**
     * 获取兑换码信息 【优惠码】
     * 优惠码与订单相关 仅可以在下单时获取
     *
     * @param promoCode 兑换码编号
     * @param userId    用户编号
     */
    void getPromoCode(String promoCode, Long userId);

    /**
     * 使用兑换码 【优惠码】
     * 优惠码与订单相关 仅可以在下单时获取
     *
     * @param code   兑换码编号
     * @param userId 用户编号
     */
    Long useCouponPromoCode(String code, Long userId);

    /**
     * 使用兑换码 【权益码】
     *
     * @param id     兑换码编号
     * @param userId 用户编号
     */
    void usePromoCode(String code, Long userId);

    default Integer getUseCount(Long templateId, Long userId) {
        Map<Long, Integer> map = getUseCountMapByTemplateIds(Collections.singleton(templateId), userId);
        return MapUtil.getInt(map, templateId, 0);
    }


    /**
     * 统计会员领取优惠券的数量
     *
     * @param templateIds 优惠券模板编号列表
     * @param userId      用户编号
     * @return 领取优惠券的数量
     */
    Map<Long, Integer> getUseCountMapByTemplateIds(Collection<Long> templateIds, Long userId);
}
