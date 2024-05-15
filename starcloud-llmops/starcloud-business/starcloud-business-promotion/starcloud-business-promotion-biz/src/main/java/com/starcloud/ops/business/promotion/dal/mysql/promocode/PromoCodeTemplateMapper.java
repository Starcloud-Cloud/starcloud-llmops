package com.starcloud.ops.business.promotion.dal.mysql.promocode;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.promotion.controller.admin.promocode.vo.template.PromoCodeTemplatePageReqVO;
import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.enums.promocode.PromoCodeStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * 兑换码模板 Mapper
 *
 * @author Cusack Alan
 */
@Mapper
public interface PromoCodeTemplateMapper extends BaseMapperX<PromoCodeTemplateDO> {

    default PageResult<PromoCodeTemplateDO> selectPage(PromoCodeTemplatePageReqVO reqVO) {
        // 构建可领取的查询条件
        Consumer<LambdaQueryWrapper<PromoCodeTemplateDO>> canTakeConsumer = buildCanTakeQueryConsumer(reqVO.getCanTakeTypes());
        // 执行分页查询
        return selectPage(reqVO, new LambdaQueryWrapperX<PromoCodeTemplateDO>()
                .likeIfPresent(PromoCodeTemplateDO::getName, reqVO.getName())
                .eqIfPresent(PromoCodeTemplateDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PromoCodeTemplateDO::getCreateTime, reqVO.getCreateTime())
                .and(reqVO.getProductScopeValue() != null, w -> w.apply("FIND_IN_SET({0}, product_scope_values)",
                        reqVO.getProductScopeValue()))
                .and(canTakeConsumer != null, canTakeConsumer)
                .orderByDesc(PromoCodeTemplateDO::getId));
    }

    default void updateTakeCount(Long id, Integer incrCount) {
        update(null, new LambdaUpdateWrapper<PromoCodeTemplateDO>()
                .eq(PromoCodeTemplateDO::getId, id)
                .setSql("take_count = take_count + " + incrCount));
    }

    default List<PromoCodeTemplateDO> selectListByCodeType(Integer codeType) {
        return selectList(PromoCodeTemplateDO::getCodeType, codeType);
    }

    default PromoCodeTemplateDO selectTemplateByCode(String code, Integer codeType) {
        LambdaQueryWrapper<PromoCodeTemplateDO> wrapper = Wrappers.lambdaQuery(PromoCodeTemplateDO.class)
                .eq(PromoCodeTemplateDO::getCode, code)
                .eq(PromoCodeTemplateDO::getCodeType, codeType)
                .eq(PromoCodeTemplateDO::getStatus, PromoCodeStatusEnum.ENABLE);
        return selectOne(wrapper);
    }

    default List<PromoCodeTemplateDO> selectList(List<Integer> canTakeTypes, Integer productScope, Long productScopeValue, Integer count) {
        // 构建可领取的查询条件
        Consumer<LambdaQueryWrapper<PromoCodeTemplateDO>> canTakeConsumer = buildCanTakeQueryConsumer(canTakeTypes);
        return selectList(new LambdaQueryWrapperX<PromoCodeTemplateDO>()
                .and(productScopeValue != null, w -> w.apply("FIND_IN_SET({0}, product_scope_values)",
                        productScopeValue))
                .and(canTakeConsumer != null, canTakeConsumer)
                .last(" LIMIT " + count)
                .orderByDesc(PromoCodeTemplateDO::getId));
    }

    static Consumer<LambdaQueryWrapper<PromoCodeTemplateDO>> buildCanTakeQueryConsumer(List<Integer> canTakeTypes) {
        Consumer<LambdaQueryWrapper<PromoCodeTemplateDO>> canTakeConsumer = null;
        if (CollUtil.isNotEmpty(canTakeTypes)) {
            canTakeConsumer = w ->
                    w.eq(PromoCodeTemplateDO::getStatus, CommonStatusEnum.ENABLE.getStatus()) // 1. 状态为可用的
                            .and(ww -> ww.isNull(PromoCodeTemplateDO::getValidEndTime)  // 3. 未过期
                                    .or().gt(PromoCodeTemplateDO::getValidEndTime, LocalDateTime.now()))
                            .apply(" (take_count < total_count OR total_count = -1 )");
        }
        return canTakeConsumer;
    }

}
