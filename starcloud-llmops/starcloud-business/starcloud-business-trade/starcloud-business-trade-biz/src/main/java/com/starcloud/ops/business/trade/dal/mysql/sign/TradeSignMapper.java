package com.starcloud.ops.business.trade.dal.mysql.sign;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.MPJLambdaWrapperX;
import cn.iocoder.yudao.module.pay.enums.sign.PaySignStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.starcloud.ops.business.trade.controller.admin.order.vo.TradeOrderPageReqVO;
import com.starcloud.ops.business.trade.controller.app.order.vo.AppTradeOrderPageReqVO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.sign.TradeSignDO;
import com.starcloud.ops.business.trade.enums.sign.TradeSignStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface TradeSignMapper extends BaseMapperX<TradeSignDO> {

    default int updateByIdAndStatus(Long id, Integer status, TradeSignDO update) {
        return update(update, new LambdaUpdateWrapper<TradeSignDO>()
                .eq(TradeSignDO::getId, id).eq(TradeSignDO::getStatus, status));
    }

    default TradeSignDO selectByIdAndUserId(Long id, Long userId) {
        return selectOne(TradeSignDO::getId, id, TradeSignDO::getUserId, userId);
    }

    default List<TradeSignDO> selectIsSignSuccess() {
        return selectList(new LambdaQueryWrapper<TradeSignDO>()
                .eq(TradeSignDO::getStatus, TradeSignStatusEnum.SIGNING.getStatus()));
    }


    /**
     * 获取可以扣款列表 【签约中-扣款时间在 5 天内的数据】
     * @return
     */
    default List<TradeSignDO> selectDeductibleData() {
        return selectList(new LambdaQueryWrapper<TradeSignDO>()
                .eq(TradeSignDO::getStatus, TradeSignStatusEnum.SIGNING.getStatus())
                .between(TradeSignDO::getPayTime, LocalDateTime.now().minusDays(5), LocalDateTime.now()));
    }



}
