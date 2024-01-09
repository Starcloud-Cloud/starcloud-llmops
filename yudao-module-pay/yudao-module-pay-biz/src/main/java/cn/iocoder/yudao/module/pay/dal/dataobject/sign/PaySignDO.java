package cn.iocoder.yudao.module.pay.dal.dataobject.sign;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支付签约
 DO
 *
 * @author starcloudadmin
 */
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
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 应用编号
     */
    private Long appId;
    /**
     * 渠道编号
     */
    private Long channelId;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 扩展 Id
     */
    private Long extensionId;
    /**
     * 签约号
     */
    private String no;
    /**
     * 支付时间
     */
    private LocalDate payTime;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 商户订单编号
     */
    private String merchantSignId;
    /**
     * 商品名称
     */
    private String subject;
    /**
     * 支付系统-签约编号
     */
    private String body;
    /**
     * 返回的地址
     */
    private String returnUrl;
    /**
     * 签约通知时间
     */
    private String notifyUrl;
    /**
     * 首次签约价格
     */
    private Integer firstPrice;
    /**
     * 签约价格
     */
    private Integer price;
    /**
     * 通知商户签约结果的回调状态
     */
    private Integer period;
    /**
     * 签约 有效周期
     */
    private String periodUnit;
    /**
     * 费率
     */
    private Double channelFeeRate;
    /**
     * 费率价格
     */
    private Double channelFeePrice;
    /**
     * 签约状态
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
     * 用户ID
     */
    private String userId;

}