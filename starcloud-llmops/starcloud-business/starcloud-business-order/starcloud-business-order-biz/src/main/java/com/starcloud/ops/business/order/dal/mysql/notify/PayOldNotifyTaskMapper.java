package com.starcloud.ops.business.order.dal.mysql.notify;


import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.starcloud.ops.business.order.dal.dataobject.notify.PayNotifyTaskDO;
import com.starcloud.ops.business.order.enums.notify.PayNotifyStatusEnum;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PayOldNotifyTaskMapper extends BaseMapperX<PayNotifyTaskDO> {

    /**
     * 获得需要通知的 PayNotifyTaskDO 记录。需要满足如下条件：
     *
     * 1. status 非成功
     * 2. nextNotifyTime 小于当前时间
     *
     * @return PayTransactionNotifyTaskDO 数组
     */
    default List<PayNotifyTaskDO> selectListByNotify() {
        return selectList(new QueryWrapper<PayNotifyTaskDO>()
                .in("status", PayNotifyStatusEnum.WAITING.getStatus(), PayNotifyStatusEnum.REQUEST_SUCCESS.getStatus(),
                        PayNotifyStatusEnum.REQUEST_FAILURE.getStatus())
                .le("next_notify_time", LocalDateTime.now()));
    }

}
