package com.starcloud.ops.business.promotion.dal.mysql.promocode;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.toolkit.MPJWrappers;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.code.PromoCodePageReqVO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 兑换码 Mapper
 *
 * @author Cusack Alan
 */
@Mapper
public interface PromoCodeMapper extends BaseMapperX<PromoCodeDO> {

    default PageResult<PromoCodeDO> selectPage(PromoCodePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PromoCodeDO>()
                .eqIfPresent(PromoCodeDO::getTemplateId, reqVO.getTemplateId())
                // .eqIfPresent(PromoCodeDO::getStatus, reqVO.getStatus())
                .inIfPresent(PromoCodeDO::getUserId, reqVO.getUserIds())
                .betweenIfPresent(PromoCodeDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PromoCodeDO::getId));
    }

    default List<PromoCodeDO> selectListByUserIdAndStatus(Long userId, Integer status) {
        return selectList(new LambdaQueryWrapperX<PromoCodeDO>()
                // .eq(PromoCodeDO::getUserId, userId).eq(PromoCodeDO::getStatus, status)
        );
    }

    default PromoCodeDO selectByIdAndUserId(Long id, Long userId) {
        return selectOne(new LambdaQueryWrapperX<PromoCodeDO>()
                .eq(PromoCodeDO::getId, id).eq(PromoCodeDO::getUserId, userId));
    }

    default int delete(Long id, Collection<Integer> whereStatuses) {
        return update(null, new LambdaUpdateWrapper<PromoCodeDO>()
                // .eq(PromoCodeDO::getId, id).in(PromoCodeDO::getStatus, whereStatuses)
                .set(PromoCodeDO::getDeleted, 1));
    }

    default int updateByIdAndStatus(Long id, Integer status, PromoCodeDO updateObj) {
        return update(updateObj, new LambdaUpdateWrapper<PromoCodeDO>()
                // .eq(PromoCodeDO::getId, id).eq(PromoCodeDO::getStatus, status)
        );
    }

    default Long selectCountByUserId(Long userId, Long templateId) {
        return selectCount(new LambdaQueryWrapperX<PromoCodeDO>()
                .eq(PromoCodeDO::getUserId, userId)
                .eq(PromoCodeDO::getTemplateId, templateId)
        );
    }

    default List<PromoCodeDO> selectListByTemplateIdAndUserId(Long templateId, Collection<Long> userIds) {
        return selectList(new LambdaQueryWrapperX<PromoCodeDO>()
                .eq(PromoCodeDO::getTemplateId, templateId)
                .in(PromoCodeDO::getUserId, userIds)
        );
    }

    default Map<Long, Integer> selectCountByUserIdAndTemplateIdIn(Long userId, Collection<Long> templateIds) {
        String templateIdAlias = "templateId";
        String countAlias = "count";
        List<Map<String, Object>> list = selectMaps(MPJWrappers.lambdaJoin(PromoCodeDO.class)
                .selectAs(PromoCodeDO::getTemplateId, templateIdAlias)
                .selectCount(PromoCodeDO::getId, countAlias)
                .eq(PromoCodeDO::getUserId, userId)
                .in(PromoCodeDO::getTemplateId, templateIds)
                .groupBy(PromoCodeDO::getTemplateId));
        return convertMap(list, map -> MapUtil.getLong(map, templateIdAlias), map -> MapUtil.getInt(map, countAlias));
    }

    default List<PromoCodeDO> selectListByUserIdAndStatusAndUsePriceLeAndProductScope(
            Long userId, Integer status, Integer usePrice, List<Long> spuIds, List<Long> categoryIds) {
        Function<List<Long>, String> productScopeValuesFindInSetFunc = ids -> ids.stream()
                .map(id -> StrUtil.format("FIND_IN_SET({}, product_scope_values) ", id))
                .collect(Collectors.joining(" OR "));
        return selectList(new LambdaQueryWrapperX<PromoCodeDO>()
                .eq(PromoCodeDO::getUserId, userId));
                // .eq(PromoCodeDO::getStatus, status)
                // .le(PromoCodeDO::getUsePrice, usePrice) // 价格小于等于，满足价格使用条件
                // .and(w -> w.eq(PromoCodeDO::getProductScope, PromotionProductScopeEnum.ALL.getScope()) // 商品范围一：全部
                //         .or(ww -> ww.eq(PromoCodeDO::getProductScope, PromotionProductScopeEnum.SPU.getScope()) // 商品范围二：满足指定商品
                //                 .apply(productScopeValuesFindInSetFunc.apply(spuIds)))
                //         .or(ww -> ww.eq(PromoCodeDO::getProductScope, PromotionProductScopeEnum.CATEGORY.getScope()) // 商品范围三：满足指定分类
                //                 .apply(productScopeValuesFindInSetFunc.apply(categoryIds)))));
    }

}
