package com.starcloud.ops.business.trade.service.price.calculator;

import com.starcloud.ops.business.product.api.spu.dto.GiveRightsDTO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateReqBO;
import com.starcloud.ops.business.trade.service.price.bo.TradePriceCalculateRespBO;
import com.starcloud.ops.business.user.api.level.AdminUserLevelApi;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * 赠送积分的 {@link TradePriceCalculator} 实现类
 *
 * @author owen
 */
@Component
@Order(TradePriceCalculator.ORDER_RIGHTS_GIVE)
@Slf4j
public class TradeRightGiveCalculator implements TradePriceCalculator {

    @Resource
    private AdminUserRightsApi adminUserRightsApi;

    @Resource
    private AdminUserLevelApi adminUserLevelApi;

    @Override
    public void calculate(TradePriceCalculateReqBO param, TradePriceCalculateRespBO result) {
//        // 1.1 校验积分功能是否开启
//        GiveRightsDTO giveRightsDTO = Optional.ofNullable(result.getGiveRights())
//                .orElse(null);
//        if (Objects.isNull(giveRightsDTO)) {
//            return;
//        }
//        // 设置会员等级
//        adminUserLevelApi.addAdminUserLevel(param.getUserId(),giveRightsDTO.getLevelId());
//        // 设置会员权益
//        adminUserRightsApi.addRights(param.getUserId(),giveRightsDTO.getGiveMagicBean(),giveRightsDTO.getGiveImage(), AdminUserRightsBizTypeEnum.ORDER_GIVE.getType(), String.valueOf(111));

    }

}
