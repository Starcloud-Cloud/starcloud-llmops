
package cn.iocoder.yudao.framework.pay.core.client.dto.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付通知 Response DTO
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaySignNotifyRespDTO {

    /**
     * 标示用户的唯一签约协议号，商家自定义
     */
    private String externalAgreementNo;
    /**
     * 支付渠道用户签约记录的编号。
     */
    private String agreementNo;
    /**
     * 异步通知类型，枚举支持。
     * dut_user_sign：当 status = NORMAL 表示签约成功。
     * dut_user_unsign：当 status = UNSIGN 表示解约成功。
     */
    private String notifyType;
    /**
     * 签约协议场景
     */
    private String signScene;
    /**
     * 协议产品码
     */
    private String personalProductCode;
    /**
     * 用户的支付宝账号对应的支付宝唯一用户号。
     */
    private String alipayUserId;
    /**
     * 用户的支付宝登录账号
     */
    private String alipayLogonId;
    /**
     * 签约时间
     */
    private LocalDateTime signTime;

    /**
     * TODO @jason 结合其他的渠道定义成枚举,
     *
     * alipay
     * NORMAL,正常。
     * UNSIGN, 解约
     */
    private String status;

}
