package cn.iocoder.yudao.module.pay.api.sign.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 签约单创建 Request DTO
 */
@Data
public class PaySignCreateReqDTO implements Serializable {

    public static final int SUBJECT_MAX_LENGTH = 32;

    /**
     * 应用编号
     */
    @NotNull(message = "应用编号不能为空")
    private Long appId;
    /**
     * 用户 IP
     */
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;

    // ========== 商户相关字段 ==========

    /**
     * 商户订单编号
     */
    @NotEmpty(message = "商户订单编号不能为空")
    private String merchantSignId;
    /**
     * 商品标题
     */
    @NotEmpty(message = "商品标题不能为空")
    @Length(max = SUBJECT_MAX_LENGTH, message = "商品标题不能超过 32")
    private String subject;
    /**
     * 商品描述
     */
    @Length(max = 128, message = "商品描述信息长度不能超过128")
    private String body;

    // ========== 订单相关字段 ==========

    /**
     * 支付金额，单位：分
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "签约金额必须大于 0 元")
    private Integer price;

    /**
     * 支付金额，单位：分
     */
    @NotNull(message = " 首次支付金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "首次支付金额必须大于 0 元")
    private Integer firstPrice;


    // ========== 签约相关字段 ==========
    @NotEmpty(message = "周期类型不可以为空，枚举值为 DAY 和 MONTH\"")
    private String periodUnit;

    @NotEmpty(message = "周期数 不能为空")
    private String period;

    @NotEmpty(message = "扣款时间 不能为空")
    private Date payTime;

//    @NotEmpty(message = "单次扣款最大金额")
//    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
    private String singleAmount;

    // @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
    private String totalAmount;

    // @Schema(description = "总扣款次数")
    private String totalPayments;


}
