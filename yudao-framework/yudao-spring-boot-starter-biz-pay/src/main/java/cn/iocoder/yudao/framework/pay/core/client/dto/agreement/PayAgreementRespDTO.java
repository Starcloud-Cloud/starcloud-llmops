package cn.iocoder.yudao.framework.pay.core.client.dto.agreement;

import cn.iocoder.yudao.framework.pay.core.client.exception.PayException;
import cn.iocoder.yudao.framework.pay.core.enums.agreement.PayAgreementStatusRespEnum;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderDisplayModeEnum;
import cn.iocoder.yudao.framework.pay.core.enums.order.PayOrderStatusRespEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 渠道支付订单 Response DTO
 *
 * @author 芋道源码
 */
@Data
public class PayAgreementRespDTO {

    /**
     * 签约状态
     * <p>
     * 枚举：{@link PayAgreementStatusRespEnum}
     */
    private Integer status;

    /**
     * 外部签约号
     * <p>
     * 对应 PaySignExtensionDO 的 no 字段
     */
    private String agreementNo;

    /**
     * 外部订单号
     * <p>
     * 对应 PaySignExtensionDO 的 no 字段
     */
    private String outTradeNo;

    /**
     * 支付渠道编号
     */
    private String channelAgreementNo;
    /**
     * 支付渠道用户编号
     */
    private String channelUserId;

    /**
     * 协议签约时间
     */
    private LocalDateTime signTime;

    /**
     * 协议失效时间
     */
    private LocalDateTime invalidTime;

    // ========== 主动发起支付时，会返回的字段 ==========

    /**
     * 展示内容
     */
    private String displayContent;


    /**
     * 原始的同步/异步通知结果
     */
    private Object rawData;

    /**
     * 调用渠道的错误码
     * <p>
     * 注意：这里返回的是业务异常，而是不系统异常。
     * 如果是系统异常，则会抛出 {@link PayException}
     */
    private String channelErrorCode;
    /**
     * 调用渠道报错时，错误信息
     */
    private String channelErrorMsg;

    public PayAgreementRespDTO() {
    }

    /**
     * 创建【WAITING】状态的订单返回
     */
    public static PayAgreementRespDTO waitingSignOf(String channelAgreementNo, String displayContent, Object rawData) {
        PayAgreementRespDTO respDTO = new PayAgreementRespDTO();
        respDTO.status = PayAgreementStatusRespEnum.WAITING.getStatus();

        respDTO.displayContent = displayContent;
        // 相对通用的字段
        respDTO.channelAgreementNo = channelAgreementNo;
        respDTO.rawData = rawData;
        return respDTO;
    }

    /**
     * 创建【SUCCESS】状态的订单返回
     */
    public static PayAgreementRespDTO successOf(String channelAgreementNo, String channelUserId, LocalDateTime signTime,
                                                String outAgreementNo, Object rawData) {
        PayAgreementRespDTO respDTO = new PayAgreementRespDTO();
        respDTO.status = PayAgreementStatusRespEnum.SUCCESS.getStatus();
        respDTO.channelAgreementNo = channelAgreementNo;
        respDTO.channelUserId = channelUserId;
        respDTO.signTime = signTime;
        // 相对通用的字段
        respDTO.agreementNo = outAgreementNo;
        respDTO.rawData = rawData;
        return respDTO;
    }

    /**
     * 创建指定状态的订单返回，适合支付渠道回调时
     */
    public static PayAgreementRespDTO of(Integer status, String channelAgreementNo, String channelUserId,
                                         LocalDateTime signTime, LocalDateTime invalidTime,
                                         String outAgreementNo, Object rawData) {
        PayAgreementRespDTO respDTO = new PayAgreementRespDTO();
        respDTO.status = status;
        respDTO.channelAgreementNo = channelAgreementNo;
        respDTO.channelUserId = channelUserId;
        respDTO.signTime = signTime;
        respDTO.invalidTime = invalidTime;
        // 相对通用的字段
        respDTO.agreementNo = outAgreementNo;
        respDTO.rawData = rawData;
        return respDTO;
    }

    /**
     * 创建【CLOSED】状态的订单返回，适合调用支付渠道失败时
     */
    public static PayAgreementRespDTO closedOf(String channelAgreementNo, String channelUserId, LocalDateTime invalidTime,
                                               String outAgreementNo, Object rawData) {
        PayAgreementRespDTO respDTO = new PayAgreementRespDTO();
        respDTO.status = PayAgreementStatusRespEnum.CLOSED.getStatus();
        respDTO.channelAgreementNo = channelAgreementNo;
        respDTO.channelUserId = channelUserId;
        respDTO.invalidTime = invalidTime;
        // 相对通用的字段
        respDTO.agreementNo = outAgreementNo;
        respDTO.rawData = rawData;
        return respDTO;
    }

    public static PayAgreementRespDTO failOf(String channelErrorCode, String channelErrorMsg,
                                             String outTradeNo,String externalAgreementNo, Object rawData) {
        PayAgreementRespDTO respDTO = new PayAgreementRespDTO();
        respDTO.status = PayOrderStatusRespEnum.CLOSED.getStatus();
        respDTO.channelErrorCode = channelErrorCode;
        respDTO.channelErrorMsg = channelErrorMsg;
        // 相对通用的字段
        respDTO.outTradeNo = outTradeNo;
        respDTO.agreementNo = externalAgreementNo;
        respDTO.rawData = rawData;
        return respDTO;
    }

}
