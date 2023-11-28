package cn.iocoder.yudao.module.pay.dal.dataobject.sign;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.pay.core.enums.channel.PayChannelEnum;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.channel.PayChannelDO;
import cn.iocoder.yudao.module.pay.enums.sign.PaySignStatusEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@TableName("pay_sign")
@KeySequence("pay_sign_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
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
     *
     * 例如说，内部系统 A 的订单号，需要保证每个 PayAppDO 唯一
     */
    private String merchantSignId;
    /**
     * 商品标题
     */
    private String subject;
    /**
     * 商品描述信息
     */
    private String body;
    /**
     * 异步通知地址
     */
    private String notifyUrl;
    /**
     * 异步通知地址
     */
    private String returnUrl;

    // ========== 签约信息相关字段 ==========

    /**
     * 首次签约金额，单位：分
     */
    private Integer firstPrice;

    /**
     * 后续签约金额，单位：分
     */
    private Integer price;

    /**
     * 签约周期数
     */
    private Integer period;

    /**
     * 签约周期类型 ，枚举值为 DAY 和 MONTH。
     */
    private Integer period_type;

    /**
     * 渠道手续费，单位：百分比
     *
     * 冗余 {@link PayChannelDO#getFeeRate()}
     */
    private Double channelFeeRate;
    /**
     * 渠道手续金额，单位：分
     */
    private Integer channelFeePrice;


    /**
     * 签约状态
     * WAITING(0, "待签约"),
     * SUCCESS(10, "签约成功"),
     * CANCEL(20, "取消签约"),
     * CLOSED(30, "签约关闭"),
     *
     * 枚举 {@link PaySignStatusEnum}
     */
    private Integer status;

    /**
     * 签约成功时间
     */
    private LocalDateTime contractTime;

    /**
     * 签约失效时间
     */
    private LocalDateTime expireTime;


    /**
     * 下次扣款时间
     */
    private Date nextPay;
    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 签约成功的订单拓展单编号
     *
     * 关联 {@link PaySignExtensionDO#getId()}
     */
    private Long extensionId;
    /**
     * 签约成功的外部签约号
     *
     * 关联 {@link PaySignExtensionDO#getNo()}
     */
    private String no;

    // ========== 渠道相关字段 ==========
    /**
     * 渠道用户编号
     *
     * 例如说，微信 openid、支付宝账号
     */
    private String channelUserId;
    /**
     * 渠道订单号
     */
    private String channelOrderNo;

}
