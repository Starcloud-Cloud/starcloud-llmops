package com.starcloud.ops.business.promotion.dal.dataobject.promocode;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.promotion.enums.common.PromotionCodeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 兑换码模板 DO
 *
 * 当用户领取时，会生成 {@link PromoCodeDO} 兑换码记录
 *
 * @author  Cusack Alan
 */
@TableName(value = "promotion_code_template", autoResultMap = true)
@KeySequence("promotion_coupon_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
public class PromoCodeTemplateDO extends BaseDO {

    // ========== 基本信息 BEGIN ==========
    /**
     * 模板编号，自增唯一
     */
    @TableId
    private Long id;
    /**
     * 兑换码名
     */
    private String name;

    /**
     * 兑换码编号
     */
    private String code;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    // ========== 兑换规则 ==========
    /**
     * 发放数量
     *
     * -1 - 则表示不限制发放数量
     */
    private Integer totalCount;
    /**
     * 每人限领个数
     *
     * -1 - 则表示不限制
     */
    private Integer takeLimitCount;
    // ========== 领取规则 ==========

    // ========== 使用规则 BEGIN ==========
    /**
     * 兑换码类型
     *
     * 枚举 {@link PromotionCodeTypeEnum}
     */
    private Integer codeType;

    /**
     * 固定日期 - 生效开始时间
     *
     *
     */
    private LocalDateTime validStartTime;
    /**
     * 固定日期 - 生效结束时间
     *
     */
    private LocalDateTime validEndTime;

    // ========== 统计信息 BEGIN ==========
    /**
     * 已经被兑换数量
     */
    private Integer takeCount;
    // ========== 统计信息 END ==========

    // ========== 权益相关字段 =========
    /**
     * 兑换码
     */
    private Long couponTemplateId;

    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = GiveRightsTypeHandler.class)
    private GiveRights giveRights;


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
            return JsonUtils.parseObject(json, GiveRights.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


}
