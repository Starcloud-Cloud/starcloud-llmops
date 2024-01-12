package cn.iocoder.yudao.module.pay.dal.mysql.sign;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderExportReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderPageReqVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import cn.iocoder.yudao.module.pay.enums.sign.PaySignStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.*;

/**
 * 支付签约
 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface PaySignMapper extends BaseMapperX<PaySignDO> {

    default PageResult<PaySignDO> selectPage(SignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PaySignDO>()
                .eqIfPresent(PaySignDO::getAppId, reqVO.getAppId())
                .eqIfPresent(PaySignDO::getChannelId, reqVO.getChannelId())
                .eqIfPresent(PaySignDO::getUserIp, reqVO.getUserIp())
                .eqIfPresent(PaySignDO::getExtensionId, reqVO.getExtensionId())
                .eqIfPresent(PaySignDO::getNo, reqVO.getNo())
                .betweenIfPresent(PaySignDO::getPayTime, reqVO.getPayTime())
                .eqIfPresent(PaySignDO::getChannelCode, reqVO.getChannelCode())
                .eqIfPresent(PaySignDO::getMerchantSignId, reqVO.getMerchantSignId())
                .eqIfPresent(PaySignDO::getSubject, reqVO.getSubject())
                .eqIfPresent(PaySignDO::getBody, reqVO.getBody())
                .eqIfPresent(PaySignDO::getReturnUrl, reqVO.getReturnUrl())
                .eqIfPresent(PaySignDO::getNotifyUrl, reqVO.getNotifyUrl())
                .eqIfPresent(PaySignDO::getFirstPrice, reqVO.getFirstPrice())
                .eqIfPresent(PaySignDO::getPrice, reqVO.getPrice())
                .eqIfPresent(PaySignDO::getPeriod, reqVO.getPeriod())
                .eqIfPresent(PaySignDO::getPeriodUnit, reqVO.getPeriodUnit())
                .eqIfPresent(PaySignDO::getChannelFeeRate, reqVO.getChannelFeeRate())
                .eqIfPresent(PaySignDO::getChannelFeePrice, reqVO.getChannelFeePrice())
                .eqIfPresent(PaySignDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PaySignDO::getContractTime, reqVO.getContractTime())
                .betweenIfPresent(PaySignDO::getExpireTime, reqVO.getExpireTime())
                .eqIfPresent(PaySignDO::getUserId, reqVO.getUserId())
                .betweenIfPresent(PaySignDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PaySignDO::getId));
    }




    default Long selectCountByAppId(Long appId) {
        return selectCount(PaySignDO::getAppId, appId);
    }

    default PaySignDO selectByAppIdAndMerchantSignId(Long appId, String merchantSignId) {
        return selectOne(PaySignDO::getAppId, appId,
                PaySignDO::getMerchantSignId, merchantSignId);
    }

    default int updateByIdAndStatus(Long id, Integer status, PaySignDO update) {
        return update(update, new LambdaQueryWrapper<PaySignDO>()
                .eq(PaySignDO::getId, id).eq(PaySignDO::getStatus, status));
    }

    default List<PaySignDO> selectListByStatusAndExpireTimeLt(Integer status, LocalDateTime expireTime) {
        return selectList(new LambdaQueryWrapper<PaySignDO>()
                .eq(PaySignDO::getStatus, status)
                .lt(PaySignDO::getExpireTime, expireTime));
    }


    default List<PaySignDO> selectIsSignSuccess() {
        return selectList(new LambdaQueryWrapper<PaySignDO>()
                .eq(PaySignDO::getStatus, PaySignStatusEnum.SUCCESS.getStatus()));
    }


    // default PageResult<PayOrderDO> selectPage(PayOrderPageReqVO reqVO) {
    //     return selectPage(reqVO, new LambdaQueryWrapperX<PayOrderDO>()
    //             .eqIfPresent(PayOrderDO::getAppId, reqVO.getAppId())
    //             .eqIfPresent(PayOrderDO::getChannelCode, reqVO.getChannelCode())
    //             .likeIfPresent(PayOrderDO::getMerchantOrderId, reqVO.getMerchantOrderId())
    //             .likeIfPresent(PayOrderDO::getChannelOrderNo, reqVO.getChannelOrderNo())
    //             .likeIfPresent(PayOrderDO::getNo, reqVO.getNo())
    //             .eqIfPresent(PayOrderDO::getStatus, reqVO.getStatus())
    //             .betweenIfPresent(PayOrderDO::getCreateTime, reqVO.getCreateTime())
    //             .orderByDesc(PayOrderDO::getId));
    // }
    //
    // default List<PayOrderDO> selectList(PayOrderExportReqVO reqVO) {
    //     return selectList(new LambdaQueryWrapperX<PayOrderDO>()
    //             .eqIfPresent(PayOrderDO::getAppId, reqVO.getAppId())
    //             .eqIfPresent(PayOrderDO::getChannelCode, reqVO.getChannelCode())
    //             .likeIfPresent(PayOrderDO::getMerchantOrderId, reqVO.getMerchantOrderId())
    //             .likeIfPresent(PayOrderDO::getChannelOrderNo, reqVO.getChannelOrderNo())
    //             .likeIfPresent(PayOrderDO::getNo, reqVO.getNo())
    //             .eqIfPresent(PayOrderDO::getStatus, reqVO.getStatus())
    //             .betweenIfPresent(PayOrderDO::getCreateTime, reqVO.getCreateTime())
    //             .orderByDesc(PayOrderDO::getId));
    // }

}