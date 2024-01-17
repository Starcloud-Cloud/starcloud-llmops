package com.starcloud.ops.business.trade.dal.mysql.sign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface TradeSignItemMapper extends BaseMapperX<TradeSignItemDO> {

    default int updateAfterSaleStatus(Long id, Integer oldAfterSaleStatus, Integer newAfterSaleStatus,
                                      Long afterSaleId) {
        return update(new TradeSignItemDO().setAfterSaleStatus(newAfterSaleStatus).setAfterSaleId(afterSaleId),
                new LambdaUpdateWrapper<>(new TradeSignItemDO().setId(id).setAfterSaleStatus(oldAfterSaleStatus)));
    }

    default List<TradeSignItemDO> selectListBySignId(Long signId) {
        return selectList(TradeSignItemDO::getSignId, signId);
    }

    default List<TradeSignItemDO> selectListBySignId(Collection<Long> signIds) {
        return selectList(TradeSignItemDO::getSignId, signIds);
    }

    default TradeSignItemDO selectByIdAndUserId(Long signItemId, Long loginUserId) {
        return selectOne(new LambdaQueryWrapperX<TradeSignItemDO>()
                .eq(TradeSignItemDO::getId, signItemId)
                .eq(TradeSignItemDO::getUserId, loginUserId));
    }

    default List<TradeSignItemDO> selectListBySignIdAndCommentStatus(Long signId, Boolean commentStatus) {
        return selectList(new LambdaQueryWrapperX<TradeSignItemDO>()
                .eq(TradeSignItemDO::getSignId, signId)
                .eq(TradeSignItemDO::getCommentStatus, commentStatus));
    }

    default int selectProductSumBySignId(@Param("signIds") Set<Long> signIds) {
        // SQL sum 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<TradeSignItemDO>()
                .select("SUM(count) AS sumCount")
                .in("sign_id", signIds)); // 只计算选中的
        // 获得数量
        return CollUtil.getFirst(result) != null ? MapUtil.getInt(result.get(0), "sumCount") : 0;
    }

}
