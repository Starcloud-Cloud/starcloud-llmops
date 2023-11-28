package cn.iocoder.yudao.module.pay.dal.mysql.sign;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderExportReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.order.vo.PayOrderPageReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.PaySignExportReqVO;
import cn.iocoder.yudao.module.pay.controller.admin.sign.vo.PaySignPageReqVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaySignMapper extends BaseMapperX<PaySignDO> {

    default PageResult<PaySignDO> selectPage(PaySignPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PaySignDO>()
                .eqIfPresent(PaySignDO::getAppId, reqVO.getAppId())
                .eqIfPresent(PaySignDO::getChannelCode, reqVO.getChannelCode())
                .likeIfPresent(PaySignDO::getMerchantSignId, reqVO.getMerchantSignId())
                .likeIfPresent(PaySignDO::getChannelOrderNo, reqVO.getChannelOrderNo())
                .likeIfPresent(PaySignDO::getNo, reqVO.getNo())
                .eqIfPresent(PaySignDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PaySignDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PaySignDO::getId));
    }

    default List<PaySignDO> selectList(PaySignExportReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<PaySignDO>()
                .eqIfPresent(PaySignDO::getAppId, reqVO.getAppId())
                .eqIfPresent(PaySignDO::getChannelCode, reqVO.getChannelCode())
                .likeIfPresent(PaySignDO::getMerchantSignId, reqVO.getMerchantSignId())
                .likeIfPresent(PaySignDO::getChannelOrderNo, reqVO.getChannelOrderNo())
                .likeIfPresent(PaySignDO::getNo, reqVO.getNo())
                .eqIfPresent(PaySignDO::getStatus, reqVO.getStatus())
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

}
