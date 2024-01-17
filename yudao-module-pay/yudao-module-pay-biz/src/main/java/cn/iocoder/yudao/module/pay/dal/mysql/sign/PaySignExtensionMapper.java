package cn.iocoder.yudao.module.pay.dal.mysql.sign;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderExtensionDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.sign.PaySignExtensionDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 签约数据扩展 Mapper
 *
 * @author starcloudadmin
 */
@Mapper
public interface PaySignExtensionMapper extends BaseMapperX<PaySignExtensionDO> {

    default PaySignExtensionDO selectByNo(String no) {
        return selectOne(PaySignExtensionDO::getNo, no);
    }

    default int updateByIdAndStatus(Long id, Integer status, PaySignExtensionDO update) {
        return update(update, new LambdaQueryWrapper<PaySignExtensionDO>()
                .eq(PaySignExtensionDO::getId, id).eq(PaySignExtensionDO::getStatus, status));
    }

    default List<PaySignExtensionDO> selectListByOrderId(Long orderId) {
        return selectList(PaySignExtensionDO::getSignId, orderId);
    }

    default List<PaySignExtensionDO> selectListByStatusAndCreateTimeGe(Integer status, LocalDateTime minCreateTime) {
        return selectList(new LambdaQueryWrapper<PaySignExtensionDO>()
                .eq(PaySignExtensionDO::getStatus, status)
                .ge(PaySignExtensionDO::getCreateTime, minCreateTime));
    }
}