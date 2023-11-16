package com.starcloud.ops.business.order.controller.admin.sign;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.starcloud.ops.business.limits.enums.ProductEnum;
import com.starcloud.ops.business.limits.enums.ProductSignEnum;
import com.starcloud.ops.business.order.api.sign.dto.PaySignCreateReqDTO;
import com.starcloud.ops.business.order.api.sign.dto.PaySignSubmitReqDTO;
import com.starcloud.ops.business.order.controller.admin.order.vo.PayOrderDetailsRespVO;
import com.starcloud.ops.business.order.controller.admin.sign.vo.SignPayResultReqVO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.sign.PaySignDO;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.sign.PaySignService;
import com.starcloud.ops.business.order.util.PaySeqUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;

@Tag(name = "星河云海 - 支付签约")
@RestController
@RequestMapping("/llm/pay/sign")
@Validated
@Slf4j
public class PaySignController {

    @Resource
    private PaySignService paySignService;

    @Resource
    private PayAppService appService;

    @PostMapping("/createSign")
    @Operation(summary = "创建签约")
    public CommonResult<String> createSign(@RequestBody PaySignCreateReqDTO reqDTO) {
        // 获取当前唯一 APPID
        PayAppDO appInfo = appService.getAppInfo();
        reqDTO.setAppId(appInfo.getId());
        // 判断产品是否存在
        ProductEnum productEnum;
        try {
            productEnum = ProductEnum.getByCode(reqDTO.getProductCode());
        } catch (RuntimeException e) {
            throw new RuntimeException("产品不存在，请重新核对后提交");
        }

        // 根据产品获取订阅参数
        ProductSignEnum productSignEnum = productEnum.getProductSignEnum();
        if (ObjectUtil.isNull(productSignEnum)) {
            throw new RuntimeException("该产品不支持订阅");
        }
        reqDTO.setMerchantSignId(PaySeqUtils.genMerchantSignOrderNo());
        return success(paySignService.createSign(reqDTO));
    }

    @PostMapping("/submitSign")
    @Operation(summary = "提交签约")
    public CommonResult<String> submitSign(@RequestBody PaySignSubmitReqDTO reqDTO) {

        Assert.notBlank(reqDTO.getMerchantSignId(),"签约编号不可以为空");
        reqDTO.setChannelCode(PayChannelEnum.ALIPAY_SIGN.getCode());
        // 根据签约编号 获取签约记录
        PaySignDO paySign = paySignService.getPaySign(reqDTO.getMerchantSignId());
        ProductEnum productEnum = ProductEnum.getByCode(paySign.getProductCode());

        reqDTO.setPeriod(productEnum.getProductSignEnum().getPeriod().toString());
        reqDTO.setPeriodType(productEnum.getProductSignEnum().getPeriodType());
        reqDTO.setExecuteTime(DateUtil.today());
        reqDTO.setSingleAmount(String.valueOf(paySign.getAmount()/100.0));
        reqDTO.setTotalAmount("");
        reqDTO.setTotalPayments("");
        reqDTO.setAppId(paySign.getAppId());
        return success(paySignService.submitSign(reqDTO));
    }

    @PostMapping("/IsSuccess")
    @Operation(summary = "是否成功签约")
    public CommonResult<Boolean> IsSuccess(@RequestBody PaySignCreateReqDTO reqDTO) {
        Assert.notBlank(reqDTO.getMerchantSignId(),"签约编号不可以为空");
        return success(paySignService.validateSignStatusIsSuccess(reqDTO));
    }

    @Deprecated
    @PostMapping("/createSignPay")
    @Operation(summary = "创建签约订单")
    public CommonResult<String> createSignPay(@RequestParam("merchantSignId") String merchantSignId) {

        return success(paySignService.createSignPay(merchantSignId));
    }

    @Deprecated
    @PostMapping("/submitSignPay")
    @Operation(summary = "提交签约订单支付")
    public CommonResult<SignPayResultReqVO> submitSignPay(@RequestParam("merchantOrderId") String merchantOrderId) {

        return success(paySignService.submitSignPay(merchantOrderId));
    }

}
