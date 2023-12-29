package com.starcloud.ops.business.promotion.dal.dataobject.promocode;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.enums.common.PromotionDiscountTypeEnum;
import com.starcloud.ops.business.promotion.enums.common.PromotionProductScopeEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponStatusEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTakeTypeEnum;
import com.starcloud.ops.business.promotion.enums.promocode.PromoCodeStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠劵 DO
 *
 * @author 芋道源码
 */
@TableName(value = "promotion_coupon", autoResultMap = true)
@KeySequence("promotion_coupon_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class PromoCodeDO extends BaseDO {

    // ========== 基本信息 BEGIN ==========
    /**
     * 兑换码记录 ID
     */
    private Long id;
    /**
     * 兑换码模板编号
     *
     * 关联 {@link PromoCodeTemplateDO#getId()}
     */
    private Long templateId;
    /**
     * 兑换码名
     *
     * 冗余 {@link PromoCodeTemplateDO#getName()}
     */
    private String name;

    // ========== 基本信息 END ==========

    // ========== 领取情况 BEGIN ==========
    /**
     * 用户编号
     *
     * 关联 AdminUserDO 的 id 字段
     */
    private Long userId;
    // ========== 领取情况 END ==========

    // ========== 使用规则 BEGIN ==========
    /**
     * 生效开始时间
     */
    private LocalDateTime validStartTime;
    /**
     * 生效结束时间
     */
    private LocalDateTime validEndTime;
    // ========== 使用情况 BEGIN ==========
    /**
     * 使用订单号
     */
    private Long useOrderId;
    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    // ========== 使用情况 END ==========

    /**
     * 优惠券模板编号
     *
     * 关联 {@link CouponTemplateDO#getId()}
     */
    private Long couponTemplateId;
    // ========== 权益相关字段 =========
    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = PromoCodeTemplateDO.GiveRightsTypeHandler.class)
    private PromoCodeTemplateDO.GiveRights giveRights;


    /**
     * 魔法 AI 专属
     * 商品附属赠送权益
     *
     * @author Alan Cusack
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GiveRights {
        /**
         * 赠送魔法豆
         */
        private Integer giveMagicBean;

        /**
         * 赠送图片权益
         */
        private Integer giveImage;

        /**
         * 权益生效时间
         */
        @Schema(description = "权益生效时间", example = "100")
        private Integer rightsTimeNums;

        /**
         * 权益生效时间单位
         */
        @Schema(description = "权益生效时间单位", example = "100")
        @InEnum(value = TimeRangeTypeEnum.class,message = "权益生效时间单位，必须是 {value}")
        private Integer rightsTimeRange;

        /**
         * 用户等级
         */
        @Schema(description = "用户等级", example = "1")
        private Long levelId;

        @Schema(description = "用户等级生效时间", example = "100")
        private Integer levelTimeNums;

        @Schema(description = "用户等级生效时间单位", example = "100")
        @InEnum(value = TimeRangeTypeEnum.class,message = "用户等级生效时间单位，必须是 {value}")
        private Integer LevelTimeRange;
    }

    public static class GiveRightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, PromoCodeTemplateDO.GiveRights.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

}
