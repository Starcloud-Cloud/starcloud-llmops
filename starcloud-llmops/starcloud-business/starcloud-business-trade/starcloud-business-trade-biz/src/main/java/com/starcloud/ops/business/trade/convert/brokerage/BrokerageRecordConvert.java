package com.starcloud.ops.business.trade.convert.brokerage;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.trade.controller.admin.brokerage.vo.record.BrokerageRecordPageReqVO;
import com.starcloud.ops.business.trade.controller.admin.brokerage.vo.record.BrokerageRecordRespVO;
import com.starcloud.ops.business.trade.controller.app.brokerage.vo.record.AppBrokerageRecordPageReqVO;
import com.starcloud.ops.business.trade.controller.app.brokerage.vo.record.AppBrokerageRecordRespVO;
import com.starcloud.ops.business.trade.controller.app.brokerage.vo.user.AppBrokerageUserRankByPriceRespVO;
import com.starcloud.ops.business.trade.dal.dataobject.brokerage.BrokerageRecordDO;
import com.starcloud.ops.business.trade.dal.dataobject.brokerage.BrokerageUserDO;
import com.starcloud.ops.business.trade.enums.brokerage.BrokerageRecordBizTypeEnum;
import com.starcloud.ops.business.trade.enums.brokerage.BrokerageRecordStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 佣金记录 Convert
 *
 * @author owen
 */
@Mapper
public interface BrokerageRecordConvert {

    BrokerageRecordConvert INSTANCE = Mappers.getMapper(BrokerageRecordConvert.class);

    BrokerageRecordRespVO convert(BrokerageRecordDO bean);

    List<BrokerageRecordRespVO> convertList(List<BrokerageRecordDO> list);

    PageResult<BrokerageRecordRespVO> convertPage(PageResult<BrokerageRecordDO> page);

    default BrokerageRecordDO convert(BrokerageUserDO user, BrokerageRecordBizTypeEnum bizType, String bizId,
                                      Integer brokerageFrozenDays, int brokeragePrice, LocalDateTime unfreezeTime,
                                      String title, Long sourceUserId, Integer sourceUserLevel) {
        brokerageFrozenDays = ObjectUtil.defaultIfNull(brokerageFrozenDays, 0);
        // 不冻结时，佣金直接就是结算状态
        Integer status = brokerageFrozenDays > 0
                ? BrokerageRecordStatusEnum.WAIT_SETTLEMENT.getStatus()
                : BrokerageRecordStatusEnum.SETTLEMENT.getStatus();
        return new BrokerageRecordDO().setUserId(user.getId())
                .setBizType(bizType.getType()).setBizId(bizId)
                .setPrice(brokeragePrice).setTotalPrice(user.getBrokeragePrice())
                .setTitle(title)
                .setDescription(StrUtil.format(bizType.getDescription(), MoneyUtils.fenToYuanStr(Math.abs(brokeragePrice))))
                .setStatus(status).setFrozenDays(brokerageFrozenDays).setUnfreezeTime(unfreezeTime)
                .setSourceUserLevel(sourceUserLevel).setSourceUserId(sourceUserId);
    }

    default PageResult<BrokerageRecordRespVO> convertPage(PageResult<BrokerageRecordDO> pageResult, Map<Long, AdminUserDO> userMap) {
        PageResult<BrokerageRecordRespVO> result = convertPage(pageResult);
        for (BrokerageRecordRespVO respVO : result.getList()) {
            Optional.ofNullable(userMap.get(respVO.getUserId())).ifPresent(user ->
                    respVO.setUserNickname(user.getNickname()).setUserAvatar(user.getAvatar()));
            Optional.ofNullable(userMap.get(respVO.getSourceUserId())).ifPresent(user ->
                    respVO.setSourceUserNickname(user.getNickname()).setSourceUserAvatar(user.getAvatar()));
        }
        return result;
    }

    BrokerageRecordPageReqVO convert(AppBrokerageRecordPageReqVO pageReqVO, Long userId);

    PageResult<AppBrokerageRecordRespVO> convertPage02(PageResult<BrokerageRecordDO> pageResult);

    default PageResult<AppBrokerageUserRankByPriceRespVO> convertPage03(PageResult<AppBrokerageUserRankByPriceRespVO> pageResult, Map<Long, AdminUserDO> userMap) {
        for (AppBrokerageUserRankByPriceRespVO vo : pageResult.getList()) {
            copyTo(userMap.get(vo.getId()), vo);
        }
        return pageResult;
    }

    void copyTo(AdminUserDO from, @MappingTarget AppBrokerageUserRankByPriceRespVO to);
}
