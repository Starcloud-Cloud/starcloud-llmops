package com.starcloud.ops.business.order.dal.mysql.sign;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaySignMapper extends BaseMapperX<PaySignDO> {

    default PaySignDO selectByAppIdAndMerchantOrderId(Long appId, String merchantOrderId) {
        return selectOne(new QueryWrapper<PaySignDO>().eq("app_id", appId)
                .eq("merchant_order_id", merchantOrderId));
    }


}
