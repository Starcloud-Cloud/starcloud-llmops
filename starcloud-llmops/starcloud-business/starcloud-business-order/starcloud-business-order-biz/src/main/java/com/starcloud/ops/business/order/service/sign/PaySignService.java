package com.starcloud.ops.business.order.service.sign;

import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PayNotifyReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PaySignNotifyRespDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignCreateReqDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignSubmitReqDTO;
import com.starcloud.ops.business.order.controller.admin.sign.vo.SignPayResultReqVO;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 支付订单 Service 接口
 *
 * @author aquan
 */
public interface PaySignService {


    /**
     * 获取订阅信息
     *
     * @param id id
     * @return 支付单编号
     */
    PaySignDO getPaySign(Long id);

    /**
     * 获取订阅信息
     *
     * @param merchantSignId id
     * @return 支付单编号
     */
    PaySignDO getPaySign(String merchantSignId);

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    String createSign(PaySignCreateReqDTO reqDTO);


    /**
     * 提交签约
     *
     * @param reqDTO 创建请求
     * @return 签约地址
     */
    String submitSign(@Validated PaySignSubmitReqDTO reqDTO);


    /**
     * 验证签约状态是否成功
     *
     * @param reqDTO 创建请求
     * @return Boolean
     */
    Boolean validateSignStatusIsSuccess(@Validated PaySignCreateReqDTO reqDTO);


    /**
     * 创建支付单
     *
     * @param merchantSignId 创建请求
     * @return 支付单编号
     */
    String createSignPay(String merchantSignId);


    /**
     * 提交签约
     *
     * @param reqDTO 创建请求
     * @return 签约地址
     */
    SignPayResultReqVO submitSignPay(String merchantOrderId);

    /**
     * 通知签约成功
     *
     * @param channelId 渠道编号
     * @param notify    通知
     * @param rawNotify 通知数据
     */
    void notifySign(Long channelId, PaySignNotifyRespDTO notify, PayNotifyReqDTO rawNotify);

    /**
     * 更新签约订单
     *
     * @param paySignDO         编号
     */
    void updatePaySign(PaySignDO paySignDO);


    /**
     * 获取可以支付的签约记录
     *
     */
    List<PaySignDO> getAbleToPayRecords();

    Boolean validatePaySignResult(String code);


    /**
     * 处理 签约支付
     *
     * @param paySignDO 创建请求
     * @return 支付单编号
     */
    void processSigningPayment(PaySignDO paySignDO);


}
