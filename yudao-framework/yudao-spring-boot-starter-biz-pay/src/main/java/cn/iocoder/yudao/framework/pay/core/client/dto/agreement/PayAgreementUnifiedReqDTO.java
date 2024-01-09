package cn.iocoder.yudao.framework.pay.core.client.dto.agreement;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderDisplayModeEnum;
import com.alipay.api.domain.TimeRange;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一下单 Request DTO
 *
 * @author 芋道源码
 */
@Data
public class PayAgreementUnifiedReqDTO {

    /**
     * 用户 IP
     */
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;

    // ========== 支付订单相关字段 ==========

    /**
     * 外部订单号
     *
     * 对应 PayOrderExtensionDO 的 no 字段
     */
    @NotEmpty(message = "外部订单编号不能为空")
    private String outTradeNo;

    /**
     * 商品标题
     */
    @NotNull(message = "首次签约价格不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "首次签约价格必须大于零")
    private Integer totalAmount;

    /**
     * 商品标题
     */
    @NotEmpty(message = "商品标题不能为空")
    @Length(max = 32, message = "商品标题不能超过 32")
    private String subject;
    /**
     * 商品描述信息
     */
    @Length(max = 128, message = "商品描述信息长度不能超过128")
    private String body;

    /**
     * 支付过期时间
     */
    @NotNull(message = "支付过期时间不能为空")
    private LocalDateTime expireTime;

    // ========== 签约相关字段 ==========

    @NotEmpty(message = "外部签约编号不能为空")
    private String externalAgreementNo;


    /**
     * 支付金额，单位：分
     */
    @NotEmpty(message = "周期类型不能为空")
    private String periodType;

    @NotNull(message = "周期数不能为空")
    @Min(value = 1, message = "周期数不能小于 1")
    private Long period;

    @NotNull(message = "固定扣款时间不能为空")
    private LocalDateTime executeTime;

    @NotNull(message = "签约价格不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "签约价格必须大于零")
    private Integer singleAmount;


    @NotEmpty(message = "支付并签约结果的回调地址不能为空")
    @URL(message = "支付并签约结果的 notify 回调地址必须是 URL 格式")
    private String signNotifyUrl;

    // ========== 拓展参数 ==========
    /**
     * 支付渠道的额外参数
     *
     * 例如说，微信公众号需要传递 openid 参数
     */
    private Map<String, String> channelExtras;

    /**
     * 展示模式
     *
     * 如果不传递，则每个支付渠道使用默认的方式
     *
     * 枚举 {@link PayOrderDisplayModeEnum}
     */
    private String displayMode;
}
