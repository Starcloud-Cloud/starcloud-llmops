package com.starcloud.ops.business.order.dal.mysql.order;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starcloud.ops.business.order.controller.admin.order.vo.PayOrderAppPageReqVO;
import com.starcloud.ops.business.order.controller.admin.order.vo.PayOrderExportReqVO;
import com.starcloud.ops.business.order.controller.admin.order.vo.PayOrderPageReqVO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PayOrderMapper extends BaseMapperX<PayOrderDO> {

    default PageResult<PayOrderDO> selectPage(PayOrderPageReqVO reqVO) {
        return selectPage(reqVO, new QueryWrapperX<PayOrderDO>()
                .eqIfPresent("merchant_id", reqVO.getMerchantId())
                .eqIfPresent("app_id", reqVO.getAppId())
                .eqIfPresent("channel_id", reqVO.getChannelId())
                .eqIfPresent("channel_code", reqVO.getChannelCode())
                .likeIfPresent("merchant_order_id", reqVO.getMerchantOrderId())
                .eqIfPresent("notify_status", reqVO.getNotifyStatus())
                .eqIfPresent("status", reqVO.getStatus())
                .eqIfPresent("refund_status", reqVO.getRefundStatus())
                .likeIfPresent("channel_order_no", reqVO.getChannelOrderNo())
                .betweenIfPresent("create_time", reqVO.getCreateTime())
                .orderByDesc("id"));
    }
    default PageResult<PayOrderDO> selectAppPage(PayOrderAppPageReqVO reqVO, Long userId, Long tenantId) {
        return selectPage(reqVO, new QueryWrapperX<PayOrderDO>()
                .likeIfPresent("merchant_order_id", reqVO.getMerchantOrderId())
                .eqIfPresent("status", reqVO.getStatus())
                .eqIfPresent("refund_status", reqVO.getRefundStatus())
                .eqIfPresent("creator", userId)
                .eqIfPresent("tenant_id", tenantId)
                .orderByDesc("id"));
    }

    default List<PayOrderDO> selectList(PayOrderExportReqVO reqVO) {
        return selectList(new QueryWrapperX<PayOrderDO>()
                .eqIfPresent("merchant_id", reqVO.getMerchantId())
                .eqIfPresent("app_id", reqVO.getAppId())
                .eqIfPresent("channel_id", reqVO.getChannelId())
                .eqIfPresent("channel_code", reqVO.getChannelCode())
                .likeIfPresent("merchant_order_id", reqVO.getMerchantOrderId())
                .eqIfPresent("notify_status", reqVO.getNotifyStatus())
                .eqIfPresent("status", reqVO.getStatus())
                .eqIfPresent("refund_status", reqVO.getRefundStatus())
                .likeIfPresent("channel_order_no", reqVO.getChannelOrderNo())
                .betweenIfPresent("create_time", reqVO.getCreateTime())
                .orderByDesc("id"));
    }

    default List<PayOrderDO> findByIdListQueryOrderSubject(Collection<Long> idList) {
        return selectList(new LambdaQueryWrapper<PayOrderDO>()
                .select(PayOrderDO::getId, PayOrderDO::getSubject)
                .in(PayOrderDO::getId, idList));
    }

    /**
     * 查询符合的订单数量
     *
     * @param appId 应用编号
     * @param status 订单状态
     * @return 条数
     */
    default Long selectCount(Long appId, Integer status) {
        return selectCount(new LambdaQueryWrapper<PayOrderDO>()
                .eq(PayOrderDO::getAppId, appId)
                .in(PayOrderDO::getStatus, status));
    }

    default PayOrderDO selectByAppIdAndMerchantOrderId(Long appId, String merchantOrderId) {
        return selectOne(new QueryWrapper<PayOrderDO>().eq("app_id", appId)
                .eq("merchant_order_id", merchantOrderId));
    }
    default PayOrderDO selectByMerchantOrderId(String merchantOrderId) {
        return selectOne(new QueryWrapper<PayOrderDO>()
                .eq("merchant_order_id", merchantOrderId));
    }

    default int updateByIdAndStatus(Long id, Integer status, PayOrderDO update) {
        return update(update, new QueryWrapper<PayOrderDO>()
                .eq("id", id).eq("status", status));
    }

    default PayOrderDO selectNoPayByProductCode(String productCode,Integer orderStatus,Long userId,Long tenantId) {
        return selectOne(new QueryWrapper<PayOrderDO>()
                .eq("product_code", productCode)
                .eq("status",orderStatus)
                .eq("creator", userId)
                .eq("tenant_id", tenantId)
        );
    }

    default PayOrderDO selectNoCloseByProductCode(String productCode,Long userId,Long tenantId) {
        return selectOne(new QueryWrapper<PayOrderDO>()
                .eq("product_code", productCode)
                .gt("expire_time", LocalDateTimeUtil.now())
                .eq("status", 0)
                .eq("creator", userId)
                .eq("tenant_id", tenantId)
        );
    }


}
