package com.starcloud.ops.business.order.service.order;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.pay.config.PayProperties;
import cn.iocoder.yudao.framework.pay.core.client.PayClient;
import cn.iocoder.yudao.framework.pay.core.client.PayClientFactory;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PayNotifyReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.notify.PayOrderNotifyRespDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.framework.pay.core.client.dto.order.PayOrderUnifiedRespDTO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.core.config.notice.DingTalkNoticeProperties;
import com.starcloud.ops.business.limits.controller.admin.userbenefits.vo.UserDiscountCodeInfoVO;
import com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy.UserBenefitsStrategyDO;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.enums.ProductEnum;
import com.starcloud.ops.business.limits.enums.ProductTimeEnum;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.limits.service.userbenefitsstrategy.UserBenefitsStrategyService;
import com.starcloud.ops.business.order.api.order.dto.PayOrderCreateReqDTO;
import com.starcloud.ops.business.order.controller.admin.order.vo.*;
import com.starcloud.ops.business.order.convert.order.PayOrderConvert;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderDO;
import com.starcloud.ops.business.order.dal.dataobject.order.PayOrderExtensionDO;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderExtensionMapper;
import com.starcloud.ops.business.order.dal.mysql.order.PayOrderMapper;
import com.starcloud.ops.business.order.enums.ErrorCodeConstants;
import com.starcloud.ops.business.order.enums.notify.PayNotifyTypeEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderNotifyStatusEnum;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import com.starcloud.ops.business.order.enums.refund.PayRefundTypeEnum;
import com.starcloud.ops.business.order.service.merchant.PayAppService;
import com.starcloud.ops.business.order.service.merchant.PayChannelService;
import com.starcloud.ops.business.order.service.notify.PayNotifyService;
import com.starcloud.ops.business.order.service.notify.dto.PayNotifyTaskCreateReqDTO;
import com.starcloud.ops.business.order.util.PaySeqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.notEqual;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.json.JsonUtils.toJsonString;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUser;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId;
import static com.starcloud.ops.business.order.enums.ErrorCodeConstants.PAY_ORDER_ERROR_SUBMIT_DISCOUNT_ERROR;
import static com.starcloud.ops.business.order.enums.ErrorCodeConstants.PAY_ORDER_NOT_FOUND;

/**
 * 支付订单 Service 实现类
 *
 * @author aquan
 */
@Service
@Validated
@Slf4j
public class PayOrderServiceImpl implements PayOrderService {

    @Resource
    private PayProperties payProperties;

    @Resource
    private SmsSendApi smsSendApi;

    @Resource
    private PayClientFactory payClientFactory;

    @Resource
    private PayOrderMapper orderMapper;
    @Resource
    private PayOrderExtensionMapper orderExtensionMapper;

    @Resource
    private PayAppService appService;
    @Resource
    private PayChannelService channelService;

    @Resource
    private PayNotifyService notifyService;

    @Resource
    private UserBenefitsService userBenefitsService;

    @Resource
    private UserBenefitsStrategyService userBenefitsStrategyService;

    @Resource
    private AdminUserService userService;

    @Resource
    private DingTalkNoticeProperties dingTalkNoticeProperties;


    @Override
    public PayOrderDO getOrder(Long id) {
        return orderMapper.selectById(id);
    }

    /**
     * 获得支付订单
     *
     * @param merchantOrderId@return 支付订单
     */
    @Override
    public PayOrderDO getOrder(String merchantOrderId) {
        return this.validatePayOrderCanSubmit(merchantOrderId);
    }

    @Override
    public PageResult<PayOrderDO> getOrderPage(PayOrderPageReqVO pageReqVO) {
        return orderMapper.selectPage(pageReqVO);
    }

    @Override
    public List<PayOrderDO> getOrderList(PayOrderExportReqVO exportReqVO) {
        return orderMapper.selectList(exportReqVO);
    }

    // TODO @艿艿：需要优化。不确定这个方法的作用
    @Override
    public List<PayOrderDO> getOrderSubjectList(Collection<Long> idList) {
        return orderMapper.findByIdListQueryOrderSubject(idList);
    }

    @Override
    public String createPayOrder(PayOrderCreateReqDTO reqDTO) {

        log.info("[createPayOrder],用户[userId({})｜租户[({})｜开始创建订单({})]", getLoginUserId(), getTenantId(), reqDTO.getMerchantOrderId());

        Long discountId = null;
        if (StrUtil.isNotBlank(reqDTO.getDiscountCode())) {
            UserBenefitsStrategyDO userBenefitsStrategy = userBenefitsStrategyService.getUserBenefitsStrategy(reqDTO.getDiscountCode());
            userBenefitsService.addUserBenefitsByCode(reqDTO.getDiscountCode(), getLoginUserId());
            reqDTO.setDiscountId(userBenefitsStrategy.getId());
            discountId = userBenefitsStrategy.getId();
        }

        // 检验是否有历史未支付订单
        PayOrderDO noPayOrder = orderMapper.selectNoCloseByProductCode(
                reqDTO.getProductCode(), discountId, getLoginUserId(), getTenantId());

        if (noPayOrder != null) {
            log.info("[createPayOrder],用户[userId({}) 已经存在未支付的支付单({})]", getLoginUserId(), noPayOrder.getMerchantOrderId());
            // 重新设置订单过期时间
            orderMapper.updateByIdAndStatus(noPayOrder.getId(), PayOrderStatusEnum.WAITING.getStatus(), noPayOrder.setExpireTime(reqDTO.getExpireTime()));

            return noPayOrder.getMerchantOrderId();
        }
        log.info("[createPayOrder],用户[userId({}) 不存在未支付的支付单，开始创建新的订单]", getLoginUserId());
        // 校验 App
        PayAppDO app = appService.validPayApp(reqDTO.getAppId());

        // 查询对应的支付交易单是否已经存在。如果是，则直接返回
        PayOrderDO order = orderMapper.selectByAppIdAndMerchantOrderId(
                reqDTO.getAppId(), reqDTO.getMerchantOrderId());

        if (order != null) {
            log.warn("[createPayOrder][appId({}) merchantOrderId({}) 已经存在对应的支付单({})]", order.getAppId(),
                    order.getMerchantOrderId(), toJsonString(order)); // 理论来说，不会出现这个情况
            return order.getMerchantOrderId();
        }

        // 创建支付交易单
        order = PayOrderConvert.INSTANCE.convert(reqDTO)
                .setMerchantId(app.getMerchantId())
                .setAppId(app.getId())
                .setProductCode(reqDTO.getProductCode());
        // 商户相关字段
        order.setNotifyUrl(app.getPayNotifyUrl())
                .setNotifyStatus(PayOrderNotifyStatusEnum.NO.getStatus());
        // 订单相关字段
        order.setStatus(PayOrderStatusEnum.WAITING.getStatus());
        // 退款相关字段
        order.setRefundStatus(PayRefundTypeEnum.NO.getStatus())
                .setRefundTimes(0).setRefundAmount(0L);
        order.setSignId(reqDTO.getSignId());

        if (null == getLoginUserId()) {
            order.setCreator(reqDTO.getUserId());
            order.setUpdater(reqDTO.getUserId());
        }
        orderMapper.insert(order);
        log.info("[createPayOrder],用户[userId({}) 创建新的订单结束，订单编号为({})]", getLoginUserId(), order.getMerchantOrderId());
        return order.getMerchantOrderId();

    }

    @Override
    public PayOrderSubmitRespVO submitPayOrder(PayOrderSubmitReqVO reqVO, String userIp) {
        log.info("[submitPayOrder][0.支付宝统一下单接收到请求：用户ID({})|订单 ID({})｜用户 IP({})]", getLoginUser(), reqVO.getOrderId(), userIp);
        // 1. 获得 PayOrderDO ，并校验其是否存在
        PayOrderDO order = validatePayOrderCanSubmit(reqVO.getOrderId());

        // TODO 临时增加过期时间订单过期时间过滤
        LocalDateTime now = LocalDateTimeUtil.of(System.currentTimeMillis(), TimeZone.getTimeZone("Asia/Shanghai"));
        if (now.isAfter(order.getExpireTime())) {
            orderMapper.updateByIdAndStatus(order.getId(), PayOrderStatusEnum.CLOSED.getStatus(), order);
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }

        // 1.2 校验支付渠道是否有效
        PayChannelDO channel = validatePayChannelCanSubmit(order.getAppId(), reqVO.getChannelCode());
        PayClient client = payClientFactory.getPayClient(channel.getId());
        log.info("[submitPayOrder][1.支付渠道有效：用户ID({})|渠道 ID({})｜用户 IP({})]", getLoginUser(), channel.getId(), userIp);
        // 2. 插入 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = PayOrderConvert.INSTANCE.convert(reqVO, userIp, order.getId())
                .setOrderId(order.getId())
                .setNo(generateOrderExtensionNo())
                .setChannelId(channel.getId())
                .setChannelCode(channel.getCode())
                .setStatus(PayOrderStatusEnum.WAITING.getStatus());
        orderExtensionMapper.insert(orderExtension);

        log.info("[submitPayOrder][2.创建扩展订单数据成功：用户ID({})|订单 ID({})｜用户 IP({})]", getLoginUser(), JSONObject.toJSONString(orderExtension), userIp);
        // 3. 调用三方接口
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = PayOrderConvert.INSTANCE.convert2(reqVO)
                // 商户相关的字段
                .setMerchantOrderId(orderExtension.getNo()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                .setSubject(order.getSubject()).setBody(order.getBody())
                .setNotifyUrl(genChannelPayNotifyUrl(channel))
                // .setReturnUrl(genChannelReturnUrl(channel))
                // 订单相关字段
                .setAmount(order.getAmount()).setExpireTime(order.getExpireTime());
        PayOrderUnifiedRespDTO unifiedOrderRespDTO = client.unifiedOrder(unifiedOrderReqDTO);

        // TODO 轮询三方接口，是否已经支付的任务
        log.info("[submitPayOrder][3.创建支付成功，返回支付链接：用户ID({})|订单 ID({})｜用户 IP({})]", getLoginUser(), order.getMerchantOrderId(), userIp);
        // 返回成功
        return PayOrderConvert.INSTANCE.convert(unifiedOrderRespDTO).setCreateTime(LocalDateTime.now()).setExpireTime(order.getExpireTime());
    }

    /**
     * 重新提交支付
     * 此时，会发起支付渠道的调用
     *
     * @param reqVO  提交请求
     * @param userIp 提交 IP
     * @return 提交结果
     */
    @Override
    public PayOrderSubmitRespVO submitOrderRepay(PayOrderRepaySubmitReqVO reqVO, String userIp) {
        // 获取订单
        log.info("[submitPayOrder][收到重新下单请求，支付宝统一下单接收到请求：用户ID({})|订单 ID({})｜用户 IP({})]", getLoginUser(), reqVO.getOrderId(), userIp);

        //  获得 PayOrderDO ，并校验其是否存在
        PayOrderDO order = validatePayOrderCanSubmit(reqVO.getOrderId());

        // 校验支付渠道是否有效
        PayChannelDO channel = validatePayChannelCanSubmit(order.getAppId(), reqVO.getChannelCode());
        PayClient client = payClientFactory.getPayClient(channel.getId());
        log.info("[submitPayOrder][支付渠道有效：用户ID({})|渠道 ID({})｜用户 IP({})]", getLoginUser(), channel.getId(), userIp);

        // 插入 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = PayOrderConvert.INSTANCE.convert(reqVO, userIp, order.getId())
                .setOrderId(order.getId())
                .setNo(generateOrderExtensionNo())
                .setChannelId(channel.getId())
                .setChannelCode(channel.getCode())
                .setStatus(PayOrderStatusEnum.WAITING.getStatus());
        orderExtensionMapper.insert(orderExtension);

        PayOrderSubmitReqVO payOrderSubmitReqVO = BeanUtil.copyProperties(reqVO, PayOrderSubmitReqVO.class);
        // 提交支付
        PayOrderUnifiedReqDTO unifiedOrderReqDTO = PayOrderConvert.INSTANCE.convert2(payOrderSubmitReqVO)
                // 商户相关的字段
                .setMerchantOrderId(orderExtension.getNo()) // 注意，此处使用的是 PayOrderExtensionDO.no 属性！
                .setSubject(order.getSubject())
                .setBody(order.getBody())
                .setNotifyUrl(genChannelPayNotifyUrl(channel))
                // 订单相关字段
                .setAmount(order.getAmount())
                .setExpireTime(LocalDateTimeUtil.of(reqVO.getTimestamp(), TimeZone.getTimeZone("Asia/Shanghai")).plusMinutes(5));
        PayOrderUnifiedRespDTO unifiedOrderRespDTO = client.unifiedOrder(unifiedOrderReqDTO);

        // TODO 轮询三方接口，是否已经支付的任务
        log.info("[submitPayOrder][3.创建支付成功，返回支付链接：用户ID({})|订单 ID({})｜用户 IP({})]", getLoginUser(), order.getMerchantOrderId(), userIp);

        // 返回成功
        return PayOrderConvert.INSTANCE.convert(unifiedOrderRespDTO).setCreateTime(LocalDateTime.now()).setExpireTime(order.getExpireTime());
    }

    @Deprecated
    public Long createPayOrder(ProductEnum product, String userIP) {

        PayOrderDO order = new PayOrderDO();
        // ========订单相关字段=========
        // 生成订单编号
        String sMerchantOrderId = PaySeqUtils.genMerchantOrderNo();
        // 设置订单编号
        order.setMerchantOrderId(sMerchantOrderId);
        // 订单相关状态
        order.setAmount(product.getPrice());
        // 通知商户支付结果的回调状态
        order.setNotifyStatus(PayOrderNotifyStatusEnum.NO.getStatus());
        // 订单相关状态
        order.setStatus(PayOrderStatusEnum.WAITING.getStatus());

        // ========商品相关字段=========
        // 设置商品名称
        order.setSubject(product.getName());
        // 设置商品描述
        order.setBody(product.getDescription());

        // ========退款相关字段=========
        // 创建支付的订单的退款状态枚举
        order.setRefundStatus(PayRefundTypeEnum.NO.getStatus());
        order.setRefundTimes(0).setRefundAmount(0L);
        order.setRefundAmount(0L);
        orderMapper.insert(order);
        // 最终返回
        return order.getId();
    }


    private PayOrderDO validatePayOrderCanSubmit(String id) {
        PayOrderDO order = orderMapper.selectByMerchantOrderId(id);
        if (order == null) { // 是否存在
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        return order;
    }

    private PayChannelDO validatePayChannelCanSubmit(Long appId, String channelCode) {
        // 校验 App
        appService.validPayApp(appId);

        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(appId, channelCode);
        // 校验支付客户端是否正确初始化
        PayClient client = payClientFactory.getPayClient(channel.getId());
        if (client == null) {
            log.error("[validatePayChannelCanSubmit][渠道编号({}) 找不到对应的支付客户端]", channel.getId());
            throw exception(ErrorCodeConstants.PAY_CHANNEL_CLIENT_NOT_FOUND);
        }
        return channel;
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的返回地址
     *
     * @param channel 支付渠道
     * @return 支付成功返回的地址。 配置地址 + "/" + channel id
     */
    private String genChannelReturnUrl(PayChannelDO channel) {
        return payProperties.getReturnUrl() + "/" + channel.getId();
    }

    /**
     * 根据支付渠道的编码，生成支付渠道的回调地址
     *
     * @param channel 支付渠道
     * @return 支付渠道的回调地址  配置地址 + "/" + channel id
     */
    private String genChannelPayNotifyUrl(PayChannelDO channel) {
        return payProperties.getCallbackUrl() + "/" + channel.getId();
    }

    private String generateOrderExtensionNo() {
//    wx
//    2014
//    10
//    27
//    20
//    09
//    39
//    5522657
//    a690389285100
        // 目前的算法
        // 时间序列，年月日时分秒 14 位
        // 纯随机，6 位 TODO 芋艿：此处估计是会有问题的，后续在调整
        return DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + // 时间序列
                RandomUtil.randomInt(100000, 999999) // 随机。为什么是这个范围，因为偷懒
                ;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyPayOrder(Long channelId, PayOrderNotifyRespDTO notify, PayNotifyReqDTO rawNotify) {
        // 校验支付渠道是否有效
        PayChannelDO channel = channelService.validPayChannel(channelId);
        TenantUtils.execute(channel.getTenantId(), () -> {
            // 1. 更新 PayOrderExtensionDO 支付成功
            PayOrderExtensionDO orderExtension = updatePayOrderExtensionSuccess(notify.getOrderExtensionNo(),
                    rawNotify);
            // 2. 更新 PayOrderDO 支付成功
            PayOrderDO order = updatePayOrderSuccess(channel, orderExtension, notify);

            // 3. 插入支付通知记录
            notifyService.createPayNotifyTask(PayNotifyTaskCreateReqDTO.builder()
                    .type(PayNotifyTypeEnum.ORDER.getType()).dataId(order.getId()).build());
            // 发送钉钉通知消息
            sendMessage(order.getCreator(), order.getSubject(), order.getAmount());
            // 根据商品 code 获取商品预设用户等级
            String roleCode = ProductEnum.getRoleCodeByCode(order.getProductCode());
            // 根据商品 code 获取权益类型
            String benefitsType = ProductEnum.getBenefitsTypeByCode(order.getProductCode());
            // 设置上下文租户
            TenantContextHolder.setTenantId(order.getTenantId());
            // TODO 设置用户角色 异常处理 日志
            userBenefitsService.addBenefitsAndRole(benefitsType, Long.valueOf(order.getCreator()), roleCode);
        });
    }


    /**
     * 订单钉钉消息通知
     *
     * @param userId      用户 ID
     * @param productName 商品名称
     * @param amount      商品金额
     */
    @TenantIgnore
    private void sendMessage(String userId, String productName, Integer amount) {

        try {
            AdminUserDO user = userService.getUser(Long.valueOf(userId));

            Map<String, Object> templateParams = new HashMap<>();
            String environmentName = dingTalkNoticeProperties.getName().equals("Test") ? "测试环境" : "正式环境";
            templateParams.put("environmentName", environmentName);
            templateParams.put("userName", user.getNickname());
            templateParams.put("productName", productName);

            templateParams.put("amount", String.format("%.2f", amount / 100d));
            smsSendApi.sendSingleSmsToAdmin(
                    new SmsSendSingleToUserReqDTO()
                            .setUserId(1L).setMobile("17835411844")
                            // .setTemplateCode("SMS_2023_PAY")
                            .setTemplateCode(dingTalkNoticeProperties.getSmsCode())
                            .setTemplateParams(templateParams));
        } catch (RuntimeException e) {
            log.error("系统支付通知信息发送失败", e);
        }

    }

    /**
     * 用户获得订单记录
     * 分页
     *
     * @param userId   分页查询
     * @param tenantId 分页查询
     * @return 支付订单
     * 分页
     */
    @Override
    public PageResult<AppPayOrderDetailsRespVO> getAppOrderPage(PayOrderAppPageReqVO pageReqVO, Long userId, Long tenantId) {

        PageResult<PayOrderDO> payOrderDOPageResult = orderMapper.selectAppPage(pageReqVO, userId, tenantId);

        List<PayOrderDO> list = payOrderDOPageResult.getList();

        LocalDateTime now = LocalDateTimeUtil.of(System.currentTimeMillis(), TimeZone.getTimeZone("Asia/Shanghai"));
        List<PayOrderDO> updatedList = list.stream()
                .peek(order -> {
                    order.setCreateTime(order.getUpdateTime());
                    if (now.isAfter(order.getExpireTime()) && !PayOrderStatusEnum.SUCCESS.getStatus().equals(order.getStatus())) {
                        order.setStatus(PayOrderStatusEnum.CLOSED.getStatus());
                    }
                    if (!PayOrderStatusEnum.SUCCESS.getStatus().equals(order.getStatus())) {
                        order.setCreateTime(null);
                    }
                })
                .collect(Collectors.toList());
        payOrderDOPageResult.setList(updatedList);
        return PayOrderConvert.INSTANCE.convertAppPage(payOrderDOPageResult);

    }

    /**
     * 获取商品列表
     * 分页
     *
     * @return 支付订单
     * 分页
     */
    @Override
    public Map<String, List<AppPayProductDetailsRespVO>> getAppProductList() {
        Map<String, List<AppPayProductDetailsRespVO>> productListMap = new HashMap<>();

        for (ProductTimeEnum timeType : ProductTimeEnum.values()) {
            List<ProductEnum> productList = ProductEnum.getBySetMealTimeType(timeType);

            List<AppPayProductDetailsRespVO> collect = productList.stream()
                    .map(setMealInfo -> {
                        AppPayProductDetailsRespVO product = new AppPayProductDetailsRespVO();
                        product.setCode(setMealInfo.getCode());
                        product.setName(setMealInfo.getName());
                        product.setImage(setMealInfo.getPicture());
                        product.setDescribe(setMealInfo.getDescription());
                        product.setAmount(Long.valueOf(setMealInfo.getPrice()));
                        return product;
                    })
                    .collect(Collectors.toList());
            productListMap.put(timeType.getType(), collect);
        }

        return productListMap;
    }

    /**
     * 获取商品优惠信息
     * 分页
     *
     * @param productCode  产品代码
     * @param discountCode 折扣代码
     * @return 支付订单
     * 分页
     */
    @Override
    public AppPayProductDiscountRespVO getOrderProductDiscount(String productCode, String noNeedProductCode, String discountCode) {
        AppPayProductDiscountRespVO appPayProductDiscountRespVO = new AppPayProductDiscountRespVO();
        // 判断商品是否存在 存在则获取商品信息
        ProductEnum product;
        try {
            product = ProductEnum.getByCode(productCode);
        } catch (RuntimeException e) {
            throw new RuntimeException("未获取到该产品信息");
        }
        appPayProductDiscountRespVO.setCode(product.getCode());
        appPayProductDiscountRespVO.setName(product.getName());
        if (ProductEnum.AI_TRIAL.equals(product)) {
            appPayProductDiscountRespVO.setOriginalAmount(5900L);
            appPayProductDiscountRespVO.setDiscountAmount(4910L);
            appPayProductDiscountRespVO.setDiscountedAmount(Long.valueOf(product.getPrice()));
            if (StrUtil.isNotBlank(discountCode)) {
                throw new RuntimeException("当前产品不可以使用优惠券");
            }
            appPayProductDiscountRespVO.setDiscountCouponStatus(false);
            return appPayProductDiscountRespVO;
        }
        //  商品为年付商品
        if (product.getTimeType().equals(ProductTimeEnum.YEAR)) {
            // 获取月付产品
            ProductEnum monthProduct = ProductEnum.getByCode(noNeedProductCode);
            Integer monthUnitPrice = monthProduct.getPrice();

            appPayProductDiscountRespVO.setOriginalAmount((long) (monthUnitPrice * 12));
            appPayProductDiscountRespVO.setDiscountAmount((long) (monthUnitPrice * 12 - product.getPrice()));
            appPayProductDiscountRespVO.setDiscountedAmount(Long.valueOf(product.getPrice()));
        } else {
            appPayProductDiscountRespVO.setOriginalAmount(Long.valueOf(product.getPrice()));
            appPayProductDiscountRespVO.setDiscountAmount(0L);
            appPayProductDiscountRespVO.setDiscountedAmount(Long.valueOf(product.getPrice()));
        }

        appPayProductDiscountRespVO.setDiscountCouponStatus(false);
        // 如果有折扣码 则判断折扣码的有效性
        if (StrUtil.isNotBlank(discountCode) && userBenefitsService.validateUserBenefitsByCode(discountCode, getLoginUserId())) {

            // 折扣码有效  则根据折扣码计算对应的价格
            UserBenefitsStrategyDO userBenefitsStrategyDO = userBenefitsService.validateDiscount(productCode, discountCode, getLoginUserId());
            if (ObjectUtil.isNotNull(userBenefitsStrategyDO)) {
                Long discountPrice = userBenefitsService.calculateDiscountPrice(productCode, discountCode);

                appPayProductDiscountRespVO.setDiscountAmount(appPayProductDiscountRespVO.getOriginalAmount() - discountPrice);
                appPayProductDiscountRespVO.setDiscountedAmount(discountPrice);
                appPayProductDiscountRespVO.setDiscountCouponName(userBenefitsStrategyDO.getStrategyName());

                appPayProductDiscountRespVO.setDiscountCouponStatus(true);

            } else {
                appPayProductDiscountRespVO.setDiscountCouponStatus(false);
            }
        } else {
            appPayProductDiscountRespVO.setDiscountCouponStatus(false);
        }
        return appPayProductDiscountRespVO;
    }

    /**
     * 创建订单的时候价格校验
     *
     * @param productCode
     * @param discountCode
     * @return
     */
    @Override
    public Long getDiscountOrderPrice(String productCode, String discountCode) {
        UserBenefitsStrategyDO userBenefitsStrategyDO = userBenefitsService.validateDiscount(productCode, discountCode, getLoginUserId());
        if (ObjectUtil.isNotNull(userBenefitsStrategyDO)) {
            return userBenefitsService.calculateDiscountPrice(productCode, discountCode);
        }
        throw exception(PAY_ORDER_ERROR_SUBMIT_DISCOUNT_ERROR);
    }

    /**
     * 获取新用户优惠券
     */
    @Override
    public UserDiscountCodeInfoVO getNewUserDiscountCode(Long userId) {


        UserDiscountCodeInfoVO newUserDiscount = new UserDiscountCodeInfoVO();
        // 获取新用户优惠券
        if (isUserRegisteredWithinSpecifiedTime(userId, 3) && hasOrdersWithSuccessPayment(userId, null)) {

            try {
                UserBenefitsStrategyDO masterConfigStrategyByType = userBenefitsStrategyService.getMasterConfigStrategyByType(BenefitsStrategyTypeEnums.DIRECT_DISCOUNT_NEW_USER.getName());
                if (ObjectUtil.isNull(masterConfigStrategyByType)) {
                    log.error("后台缺失新用户优惠券配置");
                }
                newUserDiscount.setCode(masterConfigStrategyByType.getCode());
                newUserDiscount.setName(masterConfigStrategyByType.getStrategyName());

                AdminUserDO user = userService.getUser(userId);
                // 获取用户注册时间
                LocalDateTime registeredTime = user.getCreateTime();

                newUserDiscount.setStartTime(registeredTime);
                newUserDiscount.setEndTime(registeredTime.plusDays(3));

            } catch (RuntimeException e) {
                newUserDiscount.setCode("00001");
                newUserDiscount.setName("新人专享体验套餐");

                AdminUserDO user = userService.getUser(userId);
                // 获取用户注册时间
                LocalDateTime registeredTime = user.getCreateTime();
                newUserDiscount.setStartTime(registeredTime);
                newUserDiscount.setEndTime(registeredTime.plusDays(3));
                log.warn("新用户权益配置已经过期");
            }

        }
        return newUserDiscount;
    }


    public List<UserDiscountCodeInfoVO> getDiscountList(Long userId) {

        List<UserDiscountCodeInfoVO> UserDiscountCodeInfos = new ArrayList<>();
        // 获取新用户优惠券
        if (isUserRegisteredWithinSpecifiedTime(userId, 3) && hasOrdersWithSuccessPayment(userId, null)) {

            UserDiscountCodeInfoVO newUserDiscount = new UserDiscountCodeInfoVO();
            try {
                UserBenefitsStrategyDO masterConfigStrategyByType = userBenefitsStrategyService.getMasterConfigStrategyByType(BenefitsStrategyTypeEnums.DIRECT_DISCOUNT_NEW_USER.getName());
                if (ObjectUtil.isNull(masterConfigStrategyByType)) {
                    log.error("后台缺失新用户优惠券配置");
                }
                newUserDiscount.setCode(masterConfigStrategyByType.getCode());
                newUserDiscount.setName(masterConfigStrategyByType.getStrategyName());

                AdminUserDO user = userService.getUser(userId);
                // 获取用户注册时间
                LocalDateTime registeredTime = user.getCreateTime();

                newUserDiscount.setStartTime(registeredTime);
                newUserDiscount.setEndTime(registeredTime.plusDays(3));

                UserDiscountCodeInfos.add(newUserDiscount);
            } catch (RuntimeException e) {
                log.warn("新用户权益配置已经过期");
            }

        }
        // 获取用户八折优惠券
        if (hasOrdersWithSuccessPayment(userId, CollUtil.list(true, "ai_trial"))) {

            UserDiscountCodeInfoVO userDiscountCodeInfoVO = new UserDiscountCodeInfoVO();
            try {
                UserBenefitsStrategyDO masterConfigStrategyByType = userBenefitsStrategyService.getMasterConfigStrategyByType(BenefitsStrategyTypeEnums.PERCENTAGE_DISCOUNT_80.getName());
                if (userBenefitsService.exitBenefitsStrategy(masterConfigStrategyByType.getId())) {
                    if (ObjectUtil.isNotNull(masterConfigStrategyByType)) {
                        userDiscountCodeInfoVO.setCode(masterConfigStrategyByType.getCode());
                        userDiscountCodeInfoVO.setName(masterConfigStrategyByType.getStrategyName());

                        AdminUserDO user = userService.getUser(userId);
                        // 获取用户注册时间
                        LocalDateTime registeredTime = user.getCreateTime();

                        userDiscountCodeInfoVO.setStartTime(registeredTime);
                        userDiscountCodeInfoVO.setEndTime(registeredTime.plusDays(3));
                        UserDiscountCodeInfos.add(userDiscountCodeInfoVO);
                    }
                }

            } catch (RuntimeException e) {
                log.warn("新用户权益配置已经过期");
            }

        }

        return UserDiscountCodeInfos;
    }

    /**
     * 更新订单为已支付
     *
     * @param id         编号
     * @param payOrderId 支付订单号
     */
    @Override
    public void updateDemoOrderPaid(Long id, Long payOrderId) {
        // // 校验并获得支付订单（可支付）
        // PayOrderRespDTO payOrder = validateDemoOrderCanPaid(id, payOrderId);
        //
        // // 更新 PayDemoOrderDO 状态为已支付
        // int updateCount = orderMapper.updateByIdAndStatus(id, PayOrderStatusEnum.SUCCESS.getStatus(),
        //         new PayOrderDO().setStatus(PayOrderStatusEnum.SUCCESS.getStatus()).setSuccessTime(LocalDateTime.now())
        //                 .setChannelCode(payOrder.getChannelCode()));
        // if (updateCount == 0) {
        //     throw exception(PAY_DEMO_ORDER_UPDATE_PAID_STATUS_NOT_UNPAID);
        // }

    }

    /**
     * 用户端检测订单是否支付成功
     *
     * @param payOrderId 支付订单号
     */
    @Override
    public Boolean notifyUSerOrderPaid(String payOrderId) {
        // 1. 获得 PayOrderDO ，并校验其是否存在
        PayOrderDO order = orderMapper.selectByMerchantOrderId(payOrderId);
        if (order == null) { // 是否存在
            throw exception(PAY_ORDER_NOT_FOUND);
        }

        if (PayOrderStatusEnum.SUCCESS.getStatus().equals(order.getStatus())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /*
      校验交易订单满足被支付的条件
      1. 交易订单未支付
      2. 支付单已支付

      @param id 交易订单编号
     * @param payOrderId 支付订单编号
     * @return 交易订单
     */
    // private PayOrderRespDTO validateDemoOrderCanPaid(Long id, Long payOrderId) {
    //     // 1.1 校验订单是否存在
    //     PayOrderDO order = orderMapper.selectById(id);
    //     if (order == null) {
    //         throw exception(PAY_DEMO_ORDER_NOT_FOUND);
    //     }
    //     // 1.2 校验订单未支付
    //     if (PayOrderStatusEnum.SUCCESS.getStatus().equals(order.getStatus())) {
    //         log.error("[validateDemoOrderCanPaid][order({}) 不处于待支付状态，请进行处理！order 数据是：{}]",
    //                 id, toJsonString(order));
    //         throw exception(PAY_DEMO_ORDER_UPDATE_PAID_STATUS_NOT_UNPAID);
    //     }
    //     // 1.3 校验支付订单匹配
    //     if (notEqual(order.getId(), payOrderId)) { // 支付单号
    //         log.error("[validateDemoOrderCanPaid][order({}) 支付单不匹配({})，请进行处理！order 数据是：{}]",
    //                 id, payOrderId, toJsonString(order));
    //         throw exception(PAY_DEMO_ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
    //     }
    //
    //     // 2.1 校验支付单是否存在
    //     PayOrderRespDTO payOrder = payOrderApi.getOrder(payOrderId);
    //     if (payOrder == null) {
    //         log.error("[validateDemoOrderCanPaid][order({}) payOrder({}) 不存在，请进行处理！]", id, payOrderId);
    //         throw exception(PAY_ORDER_NOT_FOUND);
    //     }
    //     // 2.2 校验支付单已支付
    //     if (!PayOrderStatusEnum.isSuccess(payOrder.getStatus())) {
    //         log.error("[validateDemoOrderCanPaid][order({}) payOrder({}) 未支付，请进行处理！payOrder 数据是：{}]",
    //                 id, payOrderId, toJsonString(payOrder));
    //         throw exception(PAY_DEMO_ORDER_UPDATE_PAID_FAIL_PAY_ORDER_STATUS_NOT_SUCCESS);
    //     }
    //     // 2.3 校验支付金额一致
    //     if (notEqual(payOrder.getAmount(), order.getAmount())) {
    //         log.error("[validateDemoOrderCanPaid][order({}) payOrder({}) 支付金额不匹配，请进行处理！order 数据是：{}，payOrder 数据是：{}]",
    //                 id, payOrderId, toJsonString(order), toJsonString(payOrder));
    //         throw exception(PAY_DEMO_ORDER_UPDATE_PAID_FAIL_PAY_PRICE_NOT_MATCH);
    //     }
    //     // 2.4 校验支付订单匹配（二次）
    //     if (notEqual(payOrder.getMerchantOrderId(), id.toString())) {
    //         log.error("[validateDemoOrderCanPaid][order({}) 支付单不匹配({})，请进行处理！payOrder 数据是：{}]",
    //                 id, payOrderId, toJsonString(payOrder));
    //         throw exception(PAY_DEMO_ORDER_UPDATE_PAID_FAIL_PAY_ORDER_ID_ERROR);
    //     }
    //     return payOrder;
    // }

    /**
     * 更新 PayOrderExtensionDO 支付成功
     *
     * @param no                  支付订单号（支付模块）
     * @param rawNotifyJsonString 通知数据
     * @return PayOrderExtensionDO 对象
     */
    public PayOrderExtensionDO updatePayOrderExtensionSuccess(String no, String rawNotifyJsonString) {
        // 1.1 查询 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = orderExtensionMapper.selectByNo(no);
        if (orderExtension == null) {
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_NOT_FOUND);
        }
        if (notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        // 1.2 更新 PayOrderExtensionDO
        int updateCounts = orderExtensionMapper.updateByIdAndStatus(orderExtension.getId(),
                PayOrderStatusEnum.WAITING.getStatus(), PayOrderExtensionDO.builder().id(orderExtension.getId())
                        .status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelNotifyData(rawNotifyJsonString).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        log.info("[updatePayOrderSuccess][支付拓展单({}) 更新为已支付]", orderExtension.getId());
        return orderExtension;
    }

    /**
     * 更新 PayOrderExtensionDO 支付成功
     *
     * @param no        支付订单号（支付模块）
     * @param rawNotify 通知数据
     * @return PayOrderExtensionDO 对象
     */
    private PayOrderExtensionDO updatePayOrderExtensionSuccess(String no, PayNotifyReqDTO rawNotify) {
        // 1.1 查询 PayOrderExtensionDO
        PayOrderExtensionDO orderExtension = orderExtensionMapper.selectByNo(no);
        if (orderExtension == null) {
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_NOT_FOUND);
        }
        if (notEqual(orderExtension.getStatus(), PayOrderStatusEnum.WAITING.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        // 1.2 更新 PayOrderExtensionDO
        int updateCounts = orderExtensionMapper.updateByIdAndStatus(orderExtension.getId(),
                PayOrderStatusEnum.WAITING.getStatus(), PayOrderExtensionDO.builder().id(orderExtension.getId())
                        .status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelNotifyData(toJsonString(rawNotify)).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_EXTENSION_STATUS_IS_NOT_WAITING);
        }
        log.info("[updatePayOrderSuccess][支付拓展单({}) 更新为已支付]", orderExtension.getId());
        return orderExtension;
    }


    /**
     * 更新 PayOrderDO 支付成功
     *
     * @param channelId        支付渠道
     * @param channelCode      支付渠道
     * @param orderId          订单 ID
     * @param orderExtensionId 支付拓展ID
     * @return PayOrderDO 对象
     */
    public PayOrderDO updatePayOrderSuccess(Long channelId, String channelCode, Long orderId, Long orderExtensionId) {
        // 2.1 判断 PayOrderDO 是否处于待支付
        PayOrderDO order = orderMapper.selectById(orderId);
        if (order == null) {
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        // 2.2 更新 PayOrderDO
        int updateCounts = orderMapper.updateByIdAndStatus(order.getId(), PayOrderStatusEnum.WAITING.getStatus(),
                PayOrderDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelId(channelId)
                        .channelCode(channelCode)
                        .successTime(LocalDateTime.now())
                        .successExtensionId(orderExtensionId)
                        .notifyTime(LocalDateTime.now()).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        log.info("[updatePayOrderSuccess][支付订单({}) 更新为已支付]", order.getId());
        return order;
    }

    /**
     * @param payOrderDO
     */
    @Override
    public void updatePayOrder(PayOrderDO payOrderDO) {
        orderMapper.updateById(payOrderDO);

    }

    /**
     * 获得支付订单
     *
     * @param signId
     * @return 支付订单
     */
    @Override
    public List<PayOrderDO> getOrderBySign(Long signId) {
        return orderMapper.selectList(Wrappers.lambdaQuery(PayOrderDO.class).eq(PayOrderDO::getSignId, signId));
    }

    /**
     * 更新 PayOrderDO 支付成功
     *
     * @param channel        支付渠道
     * @param orderExtension 支付拓展单
     * @param notify         通知回调
     * @return PayOrderDO 对象
     */
    private PayOrderDO updatePayOrderSuccess(PayChannelDO channel, PayOrderExtensionDO orderExtension,
                                             PayOrderNotifyRespDTO notify) {
        // 2.1 判断 PayOrderDO 是否处于待支付
        PayOrderDO order = orderMapper.selectById(orderExtension.getOrderId());
        if (order == null) {
            throw exception(PAY_ORDER_NOT_FOUND);
        }
        if (!PayOrderStatusEnum.WAITING.getStatus().equals(order.getStatus())) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        // 2.2 更新 PayOrderDO
        int updateCounts = orderMapper.updateByIdAndStatus(order.getId(), PayOrderStatusEnum.WAITING.getStatus(),
                PayOrderDO.builder().status(PayOrderStatusEnum.SUCCESS.getStatus())
                        .channelId(channel.getId()).channelCode(channel.getCode())
                        .successTime(notify.getSuccessTime()).successExtensionId(orderExtension.getId())
                        .channelOrderNo(notify.getChannelOrderNo()).channelUserId(notify.getChannelUserId())
                        .notifyTime(LocalDateTime.now()).build());
        if (updateCounts == 0) { // 校验状态，必须是待支付
            throw exception(ErrorCodeConstants.PAY_ORDER_STATUS_IS_NOT_WAITING);
        }
        log.info("[updatePayOrderSuccess][支付订单({}) 更新为已支付]", order.getId());
        return order;
    }


    private LocalDateTime getNowGmtTime() {
        long timestamp = System.currentTimeMillis();
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }


    /**
     * 用户是否有成功下单的下单记录
     *
     * @param userId       用户 ID
     * @param productCodes 产品 code （可以为空）
     * @return
     */
    private Boolean hasOrdersWithSuccessPayment(Long userId, List<String> productCodes) {

        LambdaQueryWrapper<PayOrderDO> wrapper = Wrappers.lambdaQuery(PayOrderDO.class);
        wrapper.eq(PayOrderDO::getStatus, PayOrderStatusEnum.SUCCESS.getStatus());
        wrapper.eq(PayOrderDO::getCreator, userId);
        wrapper.in(CollUtil.isNotEmpty(productCodes), PayOrderDO::getProductCode, productCodes);
        Long aLong = orderMapper.selectCount(wrapper);

        // 判断是否存在成功订单
        return aLong <= 0;

    }

    /**
     * 用户是否在指定时间内注册
     *
     * @param userId 用户 ID
     * @param days   时间（天）
     * @return
     */
    private Boolean isUserRegisteredWithinSpecifiedTime(Long userId, Integer days) {
        AdminUserDO user = userService.getUser(userId);
        // 获取用户注册时间
        LocalDateTime registeredTime = user.getCreateTime();

        // 获取当前时间
        LocalDateTime nowTime = LocalDateTimeUtil.now();

        // 计算14天后的时间
        LocalDateTime fourteenDaysLater = nowTime.plusDays(days);

        // 判断创建时间是否在days天内
        return registeredTime.isBefore(fourteenDaysLater);

    }
}
