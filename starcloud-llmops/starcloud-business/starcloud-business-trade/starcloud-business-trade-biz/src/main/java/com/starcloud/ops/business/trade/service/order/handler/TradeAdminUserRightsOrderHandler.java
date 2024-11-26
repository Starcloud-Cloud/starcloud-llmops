package com.starcloud.ops.business.trade.service.order.handler;

import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.api.user.AdminUsersApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 会员积分、等级的 {@link TradeOrderHandler} 实现类
 *
 * @author owen
 */
@Component
public class TradeAdminUserRightsOrderHandler implements TradeOrderHandler {


    @Resource
    private AdminUsersApi adminUsersApi;

    @Override
    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {

        if (order.getGiveRights().isEmpty()) {
            return;
        }

        for (AdminUserRightsAndLevelCommonDTO giveRight : order.getGiveRights()) {
            // 增加权益
            adminUsersApi.insertUserRightsAndLevel(giveRight, order.getUserId(), AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), String.valueOf(order.getId()), order.getProductCount());
        }

    }

}
