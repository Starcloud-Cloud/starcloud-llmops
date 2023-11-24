package cn.iocoder.yudao.module.pay.api.sign;

import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.order.dto.PayOrderRespDTO;

import javax.validation.Valid;

/**
 * 签约单 API 接口
 *
 * @author LeeYan9
 * @since 2022-08-26
 */
public interface PaySignApi {

    /**
     * 创建签约
     *
     * @param reqDTO 创建请求
     * @return 签约编号
     */
    Long createSign(@Valid PayOrderCreateReqDTO reqDTO);

    /**
     * 获得签约记录
     *
     * @param id 签约编号
     * @return 获得签约记录
     */
    PayOrderRespDTO getSignRecord(Long id);


}
