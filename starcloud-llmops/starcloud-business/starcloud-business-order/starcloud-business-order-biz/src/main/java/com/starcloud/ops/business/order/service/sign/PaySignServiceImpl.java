package com.starcloud.ops.business.order.service.sign;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.enums.ProductEnum;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.convert.order.PayOrderConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderMapper;
import com.starcloud.ops.business.order.dal.mysql.sign.PaySignMapper;
import com.starcloud.ops.business.order.enums.order.PayOrderNotifyStatusEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.enums.refund.PayRefundTypeEnum;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;

@Service
@Validated
@Slf4j
public class PaySignServiceImpl implements PaySignService {


    @Resource
    private PaySignMapper signMapper;
    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;

    /**
     * 创建支付单
     *
     * @param reqDTO 创建请求
     * @return 支付单编号
     */
    @Override
    public String createPaySign(PayOrderCreateReqDTO reqDTO) {
        log.info("[createPaySign],用户[userId({})｜租户[({})｜开始创建订阅记录({})]", getLoginUserId(), getTenantId(), reqDTO.getMerchantOrderId());

        // 校验 App
        PayAppDO app = appService.validPayApp(reqDTO.getAppId());

        // 判断产品是否存在
        ProductEnum productEnum;
        try {
             productEnum = ProductEnum.valueOf(reqDTO.getProductCode());
        }catch (RuntimeException e){
            throw new RuntimeException("产品不存在，请重新核对后提交");
        }

        // 根据产品获取订阅参数

        // 创建订阅记录

        // 创建订单

        // 调用支付宝接口
        // 返回订阅ID
        //


        // if (order != null) {
        //     log.warn("[createPayOrder][appId({}) merchantOrderId({}) 已经存在对应的支付单({})]", order.getAppId(),
        //             order.getMerchantOrderId(), toJsonString(order)); // 理论来说，不会出现这个情况
        //     return order.getMerchantOrderId();
        // }
        //
        // // 创建支付交易单
        // order = PayOrderConvert.INSTANCE.convert(reqDTO)
        //         .setMerchantId(app.getMerchantId())
        //         .setAppId(app.getId())
        //         .setProductCode(reqDTO.getProductCode());
        // // 商户相关字段
        // order.setNotifyUrl(app.getPayNotifyUrl())
        //         .setNotifyStatus(PayOrderNotifyStatusEnum.NO.getStatus());
        // // 订单相关字段
        // order.setStatus(PayOrderStatusEnum.WAITING.getStatus());
        // // 退款相关字段
        // order.setRefundStatus(PayRefundTypeEnum.NO.getStatus())
        //         .setRefundTimes(0).setRefundAmount(0L);
        //
        // signMapper.insert(order);
        // log.info("[createPayOrder],用户[userId({}) 创建新的订单结束，订单编号为({})]", getLoginUserId(), order.getMerchantOrderId());
        // return order.getMerchantOrderId();
        return null;
    }

    /**
     * 更新示例订单为已支付
     *
     * @param id         编号
     * @param payOrderId 支付订单号
     * @param signId
     */
    @Override
    public void updatePaySign(Long id, Long payOrderId, String signId) {

    }
}
