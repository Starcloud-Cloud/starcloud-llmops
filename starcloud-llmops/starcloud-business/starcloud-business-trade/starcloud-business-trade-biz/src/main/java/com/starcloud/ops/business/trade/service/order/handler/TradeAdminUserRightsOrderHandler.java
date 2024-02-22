package com.starcloud.ops.business.trade.service.order.handler;

import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.user.api.level.AdminUserLevelApi;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsCommonDTO;
import com.starcloud.ops.business.user.api.rights.dto.UserRightsBasicDTO;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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


        for (AdminUserRightsCommonDTO giveRight : order.getGiveRights()) {
            if (Objects.nonNull(giveRight.getLevelBasicDTO())){
                // 设置会员等级
                adminUserLevelApi.addAdminUserLevel(
                        order.getUserId(),
                        giveRight.getLevelBasicDTO().getLevelId(),
                        giveRight.getLevelBasicDTO().getTimesRange().getTimeNums(),
                        giveRight.getLevelBasicDTO().getTimesRange().getTimeRange(),
                        AdminUserLevelBizTypeEnum.ORDER_GIVE.getType(),
                        String.valueOf(order.getId()));
            }
            if (Objects.nonNull(giveRight.getRightsBasicDTO())){
                UserRightsBasicDTO rightsBasicDTO = giveRight.getRightsBasicDTO();
                // 设置会员权益
                AddRightsDTO addRightsDTO = new AddRightsDTO();
                addRightsDTO.setUserId(order.getUserId())
                        .setMagicBean(rightsBasicDTO.getMagicBean())
                        .setMagicImage(rightsBasicDTO.getMagicImage())
                        .setMatrixBean(rightsBasicDTO.getMatrixBean())
                        .setTimeNums(rightsBasicDTO.getTimesRange().getTimeNums())
                        .setTimeRange(rightsBasicDTO.getTimesRange().getTimeRange())
                        .setBizType(AdminUserRightsBizTypeEnum.REDEEM_CODE.getType())
                        .setBizId(String.valueOf(order.getId()))
                        .setLevelId(giveRight.getLevelBasicDTO() != null ? giveRight.getLevelBasicDTO().getLevelId() : null);
                adminUserRightsApi.addRights(addRightsDTO);
            }


        }

    }

}
