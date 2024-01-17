package com.starcloud.ops.business.trade.service.order.handler;

import com.starcloud.ops.business.product.api.spu.dto.GiveRightsDTO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.user.api.level.AdminUserLevelApi;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
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
    private AdminUserRightsApi adminUserRightsApi;
    @Resource
    private AdminUserLevelApi adminUserLevelApi;

    @Override
    public void afterPayOrder(TradeOrderDO order, List<TradeOrderItemDO> orderItems) {

        if (order.getGiveRights().isEmpty()) {
            return;
        }

        for (GiveRightsDTO giveRight : order.getGiveRights()) {
            // 设置会员等级
            adminUserLevelApi.addAdminUserLevel(
                    order.getUserId(),
                    giveRight.getLevelId(),
                    giveRight.getLevelTimeNums(),
                    giveRight.getLevelTimeRange(),
                    AdminUserLevelBizTypeEnum.ORDER_GIVE.getType(),
                    String.valueOf(order.getId()));
            // 设置会员权益
            adminUserRightsApi.addRights(
                    order.getUserId(),
                    giveRight.getGiveMagicBean(),
                    giveRight.getGiveImage(),
                    giveRight.getRightsTimeNums(),
                    giveRight.getRightsTimeRange(),
                    AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(),
                    String.valueOf(order.getId()),
                    giveRight.getLevelId());
        }

    }

}
