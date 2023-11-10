package com.starcloud.ops.business.order.dal.dataobject.sign;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.pay.core.enums.PayChannelEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayAppDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayChannelDO;
import com.starcloud.ops.business.order.dal.dataobject.merchant.PayMerchantDO;
import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 支付订单 DO
 *
 * @author 芋道源码
 */
@TableName("llm_pay_sign")
@KeySequence("llm_pay_sign_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaySignDO extends BaseDO {

    /**
     * 订单编号，数据库自增
     */
    private Long id;

    /**
     * 商户编号
     *
     * 关联 {@link PayMerchantDO#getId()}
     */
    private Long merchantId;
    /**
     * 应用编号
     *
     * 关联 {@link PayAppDO#getId()}
     */
    private Long appId;
    /**
     * 渠道编号
     *
     * 关联 {@link PayChannelDO#getId()}
     */
    private Long channelId;
    /**
     * 渠道编码
     *
     * 枚举 {@link PayChannelEnum}
     */
    private String channelCode;


    // ========== 商户相关字段 ==========

    /**
     * 商户订单编号
     * 例如说，内部系统 A 的订单号。需要保证每个 PayMerchantDO 唯一
     */
    private String merchantOrderId;

    /**
     * 商品code
     */
    private String productCode;

    /**
     * 支付金额，单位：分
     */
    private Integer amount;

    /**
     * 支付宝签约号
     */
    private String agreementNo;


    /**
     * 签约状态 - 0 未订阅 1签约中 2已订阅 -1已退订
     *
     * 枚举 {@link PayOrderStatusEnum}
     */
    private Integer status;

    /**
     * 签约失效时间
     */
    private LocalDateTime expireTime;
    /**
     * 签约成功时间
     */
    private LocalDateTime contractTime;

    /**
     * 下次扣款时间
     */
    private LocalDateTime nextPay;

    /**
     * 扩展数据
     */
    private String extensionData;

    /**
     * 需要履约次数
     */
    private int performanceTimes;

    /**
     * 已经履约次数
     */
    private int performanceCompletedTimes;

    /**
     * 用户 ID
     */
    private String user_id;

    /**
     * 多租户编号
     */
    private Long tenantId;

}
