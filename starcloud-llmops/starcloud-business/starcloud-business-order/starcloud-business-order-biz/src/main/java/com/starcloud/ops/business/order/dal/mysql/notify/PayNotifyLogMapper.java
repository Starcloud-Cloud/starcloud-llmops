package com.starcloud.ops.business.order.dal.mysql.notify;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.starcloud.ops.business.order.dal.dataobject.notify.PayNotifyLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayNotifyLogMapper extends BaseMapperX<PayNotifyLogDO> {
}
