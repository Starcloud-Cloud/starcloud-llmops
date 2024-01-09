package cn.iocoder.yudao.module.pay.dal.dataobject.sign;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 签约数据扩展 DO
 *
 * @author starcloudadmin
 */
@TableName(value = "pay_sign_extension",autoResultMap = true)
@KeySequence("pay_sign_extension_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaySignExtensionDO extends BaseDO {

    /**
     * 支付订单编号
     */
    @TableId
    private Long id;
    /**
     * 支付订单号
     */
    private String no;
    /**
     * 签约编号
     */
    private Long signId;
    /**
     * 渠道编号
     */
    private Long channelId;
    /**
     * 渠道编码
     */
    private String channelCode;
    /**
     * 用户 IP
     */
    private String userIp;
    /**
     * 签约状态
     */
    private Integer status;
    /**
     * 支付渠道的额外参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String channelExtras;
    /**
     * 渠道调用报错时，错误码
     */
    private String channelErrorCode;
    /**
     * 渠道调用报错时，错误信息
     */
    private String channelErrorMsg;
    /**
     * 支付渠道异步通知的内容
     */
    private String channelNotifyData;

}