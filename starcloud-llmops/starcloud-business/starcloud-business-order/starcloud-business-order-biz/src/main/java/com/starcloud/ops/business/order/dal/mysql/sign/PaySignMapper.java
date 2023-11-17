package com.starcloud.ops.business.order.dal.mysql.sign;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaySignMapper extends BaseMapperX<PaySignDO> {

    default PaySignDO selectByAppIdAndMerchantSignId(Long appId, String merchantSignId) {
        return selectOne(new QueryWrapper<PaySignDO>().eq("app_id", appId)
                .eq("merchant_sign_id", merchantSignId));
    }

    default PaySignDO selectByMerchantSignId(String merchantSignId) {
        return selectOne(new QueryWrapper<PaySignDO>()
                .eq("merchant_sign_id", merchantSignId));
    }


}
