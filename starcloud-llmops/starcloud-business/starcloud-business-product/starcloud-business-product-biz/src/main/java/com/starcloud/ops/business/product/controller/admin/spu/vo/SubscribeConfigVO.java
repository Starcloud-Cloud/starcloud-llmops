package com.starcloud.ops.business.product.controller.admin.spu.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订阅参数
 */
@Data
public class SubscribeConfigVO {

    /**
     * 是否支持订阅
     */
    @Schema(description = "是否支持订阅", example = "true")
    private Boolean isSubscribe;

    /**
     * 订阅首次支付价格
     */
    @Schema(description = "订阅首次支付价格-分", example = "100")
    private Integer firstPrice;

    /**
     * 订阅价格
     */
    @Schema(description = "订阅价格-分", example = " 200")
    private Integer price;

    /**
     * 订阅周期
     */
    @Schema(description = "订阅周期", example = "1")
    private Integer period;

    /**
     * 订阅周期类型
     * 枚举值为 DAY 和 MONTH。
     * 周期类型使用MONTH的时候，
     * 计划扣款时间 execute_time不允许传 28 日之后的日期（可以传 28 日），以此避免有些月份可能不存在对应日期的情况。
     */
    @Schema(description = "订阅周期类型", example = "DAY")
    @InEnum(value = TimeRangeTypeEnum.class,message = "订阅周期类型所设置值，必须是 {value}")
    private Integer periodType;

}