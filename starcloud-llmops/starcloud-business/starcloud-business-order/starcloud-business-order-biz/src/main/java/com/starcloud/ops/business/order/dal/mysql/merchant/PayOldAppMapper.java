package com.starcloud.ops.business.order.dal.mysql.merchant;


import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.QueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppExportReqVO;
import com.starcloud.ops.business.order.controller.admin.merchant.vo.app.PayAppPageReqVO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface PayOldAppMapper extends BaseMapperX<PayAppDO> {

    default PageResult<PayAppDO> selectPage(PayAppPageReqVO reqVO, Collection<Long> merchantIds) {
        return selectPage(reqVO, new QueryWrapperX<PayAppDO>()
                .likeIfPresent("name", reqVO.getName())
                .eqIfPresent("status", reqVO.getStatus())
                .eqIfPresent("remark", reqVO.getRemark())
                .eqIfPresent("pay_notify_url", reqVO.getPayNotifyUrl())
                .eqIfPresent("refund_notify_url", reqVO.getRefundNotifyUrl())
                .inIfPresent("merchant_id", merchantIds)
                .betweenIfPresent("create_time", reqVO.getCreateTime())
                .orderByDesc("id"));
    }

    default List<PayAppDO> selectList(PayAppExportReqVO reqVO, Collection<Long> merchantIds) {
        return selectList(new QueryWrapperX<PayAppDO>()
                .likeIfPresent("name", reqVO.getName())
                .eqIfPresent("status", reqVO.getStatus())
                .eqIfPresent("remark", reqVO.getRemark())
                .eqIfPresent("pay_notify_url", reqVO.getPayNotifyUrl())
                .eqIfPresent("refund_notify_url", reqVO.getRefundNotifyUrl())
                .inIfPresent("merchant_id", merchantIds)
                .betweenIfPresent("create_time", reqVO.getCreateTime())
                .orderByDesc("id"));
    }

    default List<PayAppDO> getListByMerchantId(Long merchantId) {
        return selectList(new LambdaQueryWrapper<PayAppDO>()
                .select(PayAppDO::getId, PayAppDO::getName)
                .eq(PayAppDO::getMerchantId, merchantId));
    }



    // TODO @aquan：方法名补充 ByMerchantId
    default Long selectCount(Long merchantId) {
        return selectCount(new LambdaQueryWrapper<PayAppDO>().eq(PayAppDO::getMerchantId, merchantId));
    }

}
