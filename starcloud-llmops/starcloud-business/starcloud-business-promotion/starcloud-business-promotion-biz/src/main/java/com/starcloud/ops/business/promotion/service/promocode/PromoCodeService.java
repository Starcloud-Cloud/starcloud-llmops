// package com.starcloud.ops.business.promotion.service.promocode;
//
// import cn.hutool.core.collection.CollUtil;
// import cn.hutool.core.map.MapUtil;
// import cn.iocoder.yudao.framework.common.pojo.PageResult;
// import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.AppPromoCodeMatchReqVO;
// import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageReqVO;
// import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;
// import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
//
// import java.util.*;
//
// /**
//  * 兑换码 Service 接口
//  *
//  * @author
//  */
// public interface PromoCodeService {
//
//     /**
//      * 校验兑换码，包括状态、有限期
//      * <p>
//      * 1. 如果校验通过，则返回兑换码信息
//      * 2. 如果校验不通过，则直接抛出业务异常
//      *
//      * @param id     兑换码编号
//      * @param userId 用户编号
//      * @return 兑换码信息
//      */
//     PromoCodeDO validPromoCode(Long id, Long userId);
//
//     /**
//      * 校验兑换码，包括状态、有限期
//      *
//      * @param PromoCode 兑换码
//      * @see #validPromoCode(Long, Long) 逻辑相同，只是入参不同
//      */
//     void validPromoCode(PromoCodeDO PromoCode);
//
//     /**
//      * 获得兑换码分页
//      *
//      * @param pageReqVO 分页查询
//      * @return 兑换码分页
//      */
//     PageResult<PromoCodeDO> getPromoCodePage(PromoCodePageReqVO pageReqVO);
//
//     /**
//      * 使用兑换码
//      *
//      * @param id      兑换码编号
//      * @param userId  用户编号
//      * @param orderId 订单编号
//      */
//     void usePromoCode(Long id, Long userId, Long orderId);
//
//     /**
//      * 退还已使用的优惠券
//      *
//      * @param id 优惠券编号
//      */
//     void returnUsedPromoCode(Long id);
//
//     /**
//      * 回收兑换码
//      *
//      * @param id 兑换码编号
//      */
//     void deletePromoCode(Long id);
//
//     /**
//      * 获得用户的兑换码列表
//      *
//      * @param userId 用户编号
//      * @param status 兑换码状态
//      * @return 兑换码列表
//      */
//     List<PromoCodeDO> getPromoCodeList(Long userId, Integer status);
//
//     /**
//      * 获得未使用的兑换码数量
//      *
//      * @param userId 用户编号
//      * @return 未使用的兑换码数量
//      */
//     Long getUnusedPromoCodeCount(Long userId);
//
//     /**
//      * 领取优惠券
//      *
//      * @param templateId 优惠券模板编号
//      * @param userIds    用户编号列表
//      * @param takeType   领取方式
//      */
//     void takePromoCode(Long templateId, Set<Long> userIds, PromoCodeTakeTypeEnum takeType);
//
//     /**
//      * 【管理员】给用户发送优惠券
//      *
//      * @param templateId 优惠券模板编号
//      * @param userIds    用户编号列表
//      */
//     default void takePromoCodeByAdmin(Long templateId, Set<Long> userIds) {
//         // takePromoCode(templateId, userIds, PromoCodeTakeTypeEnum.ADMIN);
//     }
//
//     /**
//      * 【会员】领取优惠券
//      *
//      * @param templateId 优惠券模板编号
//      * @param userId     用户编号
//      */
//     default void takePromoCodeByUser(Long templateId, Long userId) {
//         // takePromoCode(templateId, CollUtil.newHashSet(userId), PromoCodeTakeTypeEnum.USER);
//     }
//
//     /**
//      * 【系统】给用户发送新人券
//      *
//      * @param userId 用户编号
//      */
//     void takePromoCodeByRegister(Long userId);
//
//     /**
//      * 获取会员领取指定优惠券的数量
//      *
//      * @param templateId 优惠券模板编号
//      * @param userId     用户编号
//      * @return 领取优惠券的数量
//      */
//     default Integer getTakeCount(Long templateId, Long userId) {
//         Map<Long, Integer> map = getTakeCountMapByTemplateIds(Collections.singleton(templateId), userId);
//         return MapUtil.getInt(map, templateId, 0);
//     }
//
//     /**
//      * 统计会员领取优惠券的数量
//      *
//      * @param templateIds 优惠券模板编号列表
//      * @param userId      用户编号
//      * @return 领取优惠券的数量
//      */
//     Map<Long, Integer> getTakeCountMapByTemplateIds(Collection<Long> templateIds, Long userId);
//
//     /**
//      * 获取用户匹配的优惠券列表
//      *
//      * @param userId     用户编号
//      * @param matchReqVO 匹配参数
//      * @return 优惠券列表
//      */
//     List<PromoCodeDO> getMatchPromoCodeList(Long userId, AppPromoCodeMatchReqVO matchReqVO);
//
//     /**
//      * 过期优惠券
//      *
//      * @return 过期数量
//      */
//     int expirePromoCode();
//
//     /**
//      * 获取用户是否可以领取优惠券
//      *
//      * @param userId    用户编号
//      * @param templates 优惠券列表
//      * @return 是否可以领取
//      */
//     Map<Long, Boolean> getUserCanCanTakeMap(Long userId, List<PromoCodeTemplateDO> templates);
//
// }
