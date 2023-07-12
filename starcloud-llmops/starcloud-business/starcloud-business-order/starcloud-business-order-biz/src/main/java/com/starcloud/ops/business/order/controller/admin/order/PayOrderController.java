package com.starcloud.ops.business.order.controller.admin.order;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.limits.enums.ProductEnum;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReq2DTO;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.controller.admin.order.vo.*;
import com.starcloud.ops.business.order.convert.order.PayOrderConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderExtensionDO;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayMerchantService;
import com.starcloud.ops.business.order.service.order.PayOrderExtensionService;
import com.starcloud.ops.business.order.service.order.PayOrderService;
import com.starcloud.ops.business.order.util.PaySeqUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;

@Tag(name = "星河云海 - 支付订单")
@RestController
@RequestMapping("/llm/pay/order")
@Validated
@Slf4j
public class PayOrderController {


    @Resource
    private PayOrderService orderService;

    @Resource
    private PayOrderService payOrderService;
    @Resource
    private PayOrderExtensionService orderExtensionService;
    @Resource
    private PayMerchantService merchantService;
    @Resource
    private PayAppService appService;

    @GetMapping("/get")
    @Operation(summary = "获得支付订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:order:query')")
    public CommonResult<PayOrderRespVO> getOrder(@RequestParam("id") Long id) {
        return success(PayOrderConvert.INSTANCE.convert(payOrderService.getOrder(id)));
    }

    // TODO 芋艿：看看怎么优化下；
    @GetMapping("/get-detail")
    @Operation(summary = "获得支付订单详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('pay:order:query')")
    public CommonResult<PayOrderDetailsRespVO> getOrderDetail(@RequestParam("id") Long id) {
        PayOrderDO order = payOrderService.getOrder(id);
        if (ObjectUtil.isNull(order)) {
            return success(new PayOrderDetailsRespVO());
        }

        PayMerchantDO merchantDO = merchantService.getMerchant(order.getMerchantId());
        PayAppDO appDO = appService.getApp(order.getAppId());
        PayChannelEnum channelEnum = PayChannelEnum.getByCode(order.getChannelCode());

        // TODO @aquan：文案，都是前端 format；
        PayOrderDetailsRespVO respVO = PayOrderConvert.INSTANCE.orderDetailConvert(order);
        respVO.setMerchantName(ObjectUtil.isNotNull(merchantDO) ? merchantDO.getName() : "未知商户");
        respVO.setAppName(ObjectUtil.isNotNull(appDO) ? appDO.getName() : "未知应用");
        respVO.setChannelCodeName(ObjectUtil.isNotNull(channelEnum) ? channelEnum.getName() : "未知渠道");

        PayOrderExtensionDO extensionDO = orderExtensionService.getOrderExtension(order.getSuccessExtensionId());
        if (ObjectUtil.isNotNull(extensionDO)) {
            respVO.setPayOrderExtension(PayOrderConvert.INSTANCE.orderDetailExtensionConvert(extensionDO));
        }

        return success(respVO);
    }


    @GetMapping("/page")
    @Operation(summary = "获得支付订单分页")
    @PreAuthorize("@ss.hasPermission('pay:order:query')")
    public CommonResult<PageResult<PayOrderPageItemRespVO>> getOrderPage(@Validated PayOrderPageReqVO pageVO) {
        PageResult<PayOrderDO> pageResult = payOrderService.getOrderPage(pageVO);
        if (CollectionUtil.isEmpty(pageResult.getList())) {
            return success(new PageResult<>(pageResult.getTotal()));
        }

        // 处理商户ID数据
        Map<Long, PayMerchantDO> merchantMap = merchantService.getMerchantMap(
                CollectionUtils.convertList(pageResult.getList(), PayOrderDO::getMerchantId));
        // 处理应用ID数据
        Map<Long, PayAppDO> appMap = appService.getAppMap(
                CollectionUtils.convertList(pageResult.getList(), PayOrderDO::getAppId));

        List<PayOrderPageItemRespVO> pageList = new ArrayList<>(pageResult.getList().size());
        pageResult.getList().forEach(c -> {
            PayMerchantDO merchantDO = merchantMap.get(c.getMerchantId());
            PayAppDO appDO = appMap.get(c.getAppId());
            PayChannelEnum channelEnum = PayChannelEnum.getByCode(c.getChannelCode());

            PayOrderPageItemRespVO orderItem = PayOrderConvert.INSTANCE.pageConvertItemPage(c);
            orderItem.setMerchantName(ObjectUtil.isNotNull(merchantDO) ? merchantDO.getName() : "未知商户");
            orderItem.setAppName(ObjectUtil.isNotNull(appDO) ? appDO.getName() : "未知应用");
            orderItem.setChannelCodeName(ObjectUtil.isNotNull(channelEnum) ? channelEnum.getName() : "未知渠道");
            pageList.add(orderItem);
        });
        return success(new PageResult<>(pageList, pageResult.getTotal()));
    }


    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public CommonResult<Long> submitPayOrder(@RequestBody PayOrderCreateReq2DTO req2DTO) {

        log.info("1.开始创建订单，准备封装订单参数，订单入参为:{}", JSONObject.toJSONString(req2DTO));
        PayOrderCreateReqDTO payOrderCreateReqDTO = new PayOrderCreateReqDTO();

        // 获取当前唯一 APPID
        PayAppDO appInfo = appService.getAppInfo();
        payOrderCreateReqDTO.setAppId(appInfo.getId());
        // 生成 merchantOrderId 商户订单编号
        String merchantOrderNo = PaySeqUtils.genMerchantOrderNo();
        payOrderCreateReqDTO.setMerchantOrderId(merchantOrderNo);
        ProductEnum productEnum = ProductEnum.getByCode(req2DTO.getProductCode());
        // 设置商品标题
        payOrderCreateReqDTO.setBody(productEnum.getName());
        // 设置商品描述
        payOrderCreateReqDTO.setSubject(productEnum.getDescription());
        // 设置商品价格
        payOrderCreateReqDTO.setAmount(productEnum.getPrice());
        // 设置过期时间
        payOrderCreateReqDTO.setExpireTime(LocalDateTimeUtil.ofUTC(req2DTO.getTimestamp()).plusMinutes(10));
        // 设置当前用户 IP
        payOrderCreateReqDTO.setUserIp(getClientIP());

        payOrderCreateReqDTO.setProductCode(req2DTO.getProductCode());
        log.info("2.订单参数封装完成，订单参数为:{}", JSONObject.toJSONString(payOrderCreateReqDTO));
        return success(payOrderService.createPayOrder(payOrderCreateReqDTO));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交支付订单")
    public CommonResult<PayOrderSubmitRespVO> submitPayOrder(@RequestBody PayOrderSubmitReqVO reqVO) {
        PayOrderSubmitRespVO respVO = payOrderService.submitPayOrder(reqVO, getClientIP());
        return success(respVO);
    }

    @PostMapping("/user/page")
    @Operation(summary = "用户获取订单分页")
    public CommonResult<PageResult<AppPayOrderDetailsRespVO>> submitPayOrder(@RequestBody PayOrderAppPageReqVO pageReqVO) {
        PageResult<AppPayOrderDetailsRespVO>  respVO = payOrderService.getAppOrderPage(pageReqVO, getLoginUser().getId(),getTenantId());
        return success(respVO);
    }

    @PostMapping("/product/list")
    @Operation(summary = "获取商品列表")
    public CommonResult<Map<String, List<AppPayProductDetailsRespVO>>> getProductList() {
        return success( payOrderService.getAppProductList());
    }



}
