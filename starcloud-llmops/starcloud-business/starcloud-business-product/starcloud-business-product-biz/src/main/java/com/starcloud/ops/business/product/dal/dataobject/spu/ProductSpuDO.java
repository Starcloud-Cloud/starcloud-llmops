package com.starcloud.ops.business.product.dal.dataobject.spu;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.starcloud.ops.business.product.dal.dataobject.brand.ProductBrandDO;
import com.starcloud.ops.business.product.dal.dataobject.category.ProductCategoryDO;
import com.starcloud.ops.business.product.dal.dataobject.sku.ProductSkuDO;
import com.starcloud.ops.business.product.enums.spu.ProductSpuStatusEnum;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsCommonDTO;
import lombok.*;

import java.util.List;

/**
 * 商品 SPU DO
 *
 * @author 芋道源码
 */
@TableName(value = "product_spu", autoResultMap = true)
@KeySequence("product_spu_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpuDO extends BaseDO {

    /**
     * 商品 SPU 编号，自增
     */
    @TableId
    private Long id;

    // ========== 基本信息 =========

    /**
     * 商品名称
     */
    private String name;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 商品简介
     */
    private String introduction;
    /**
     * 商品详情
     */
    private String description;
    // TODO @芋艿：是不是要删除
    /**
     * 商品条码（一维码）
     */
    private String barCode;

    /**
     * 商品分类编号
     * <p>
     * 关联 {@link ProductCategoryDO#getId()}
     */
    private Long categoryId;
    /**
     * 商品品牌编号
     * <p>
     * 关联 {@link ProductBrandDO#getId()}
     */
    private Long brandId;
    /**
     * 商品封面图
     */
    private String picUrl;
    /**
     * 商品轮播图
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> sliderPicUrls;
    /**
     * 商品视频
     */
    private String videoUrl;

    /**
     * 单位
     * <p>
     * 对应 product_unit 数据字典
     */
    private Integer unit;
    /**
     * 排序字段
     */
    private Integer sort;
    /**
     * 商品状态
     * <p>
     * 枚举 {@link ProductSpuStatusEnum}
     */
    private Integer status;

    // ========== SKU 相关字段 =========

    /**
     * 规格类型
     * <p>
     * false - 单规格
     * true - 多规格
     */
    private Boolean specType;
    /**
     * 商品价格，单位使用：分
     * <p>
     * 基于其对应的 {@link ProductSkuDO#getPrice()} sku单价最低的商品的
     */
    private Integer price;
    /**
     * 市场价，单位使用：分
     * <p>
     * 基于其对应的 {@link ProductSkuDO#getMarketPrice()} sku单价最低的商品的
     */
    private Integer marketPrice;
    /**
     * 成本价，单位使用：分
     * <p>
     * 基于其对应的 {@link ProductSkuDO#getCostPrice()} sku单价最低的商品的
     */
    private Integer costPrice;
    /**
     * 库存
     * <p>
     * 基于其对应的 {@link ProductSkuDO#getStock()} 求和
     */
    private Integer stock;

    // ========== 物流相关字段 =========

    /**
     * 物流配置模板编号
     * <p>
     * 对应 TradeDeliveryExpressTemplateDO 的 id 编号
     */
    private Long deliveryTemplateId;

    // ========== 营销相关字段 =========
    /**
     * 是否热卖推荐
     */
    private Boolean recommendHot;
    /**
     * 是否优惠推荐
     */
    private Boolean recommendBenefit;
    /**
     * 是否精品推荐
     */
    private Boolean recommendBest;
    /**
     * 是否新品推荐
     */
    private Boolean recommendNew;
    /**
     * 是否优品推荐
     */
    private Boolean recommendGood;

    /**
     * 是否限制注册天数 -1 不受限制
     */
    private Integer registerDays;

    /**
     * 赠送积分
     */
    private Integer giveIntegral;

    /**
     * 必须存在该优惠券才可以下单 如果为空 不做限制
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> limitCouponTemplateIds;

    /**
     * 赠送的优惠劵编号的数组
     * <p>
     * 对应 CouponTemplateDO 的 id 属性
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> giveCouponTemplateIds;

    // TODO @puhui999：字段估计要改成 brokerageType
    /**
     * 分销类型
     * <p>
     * false - 默认
     * true - 自行设置
     */
    private Boolean subCommissionType;

    /**
     * 活动展示顺序
     * <p>
     * 对应 PromotionTypeEnum 枚举
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Integer> activityOrders; // TODO @芋艿： 活动顺序字段长度需要增加

    // ========== 统计相关字段 =========

    /**
     * 商品销量
     */
    private Integer salesCount;
    /**
     * 虚拟销量
     */
    private Integer virtualSalesCount;
    /**
     * 浏览量
     */
    private Integer browseCount;

    // ========== 权益相关字段 =========
    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = GiveRightsTypeHandler.class)
    private AdminUserRightsCommonDTO giveRights;

    // ========== 签约相关配置 =========
    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = SubscribeConfigTypeHandler.class)
    private SubscribeConfig subscribeConfig;


    /**
     * 魔法 AI 专属
     * 商品订阅配置
     *
     * @author Alan Cusack
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscribeConfig {
        /**
         * 是否支持订阅
         */
        private Boolean isSubscribe;

        /**
         * 订阅首次支付价格
         */
        private Integer firstPrice;

        /**
         * 订阅价格
         */
        private Integer price;

        /**
         * 订阅周期
         */
        private Integer period;

        /**
         * 订阅周期类型
         * 枚举值为 DAY 和 MONTH。
         * 周期类型使用MONTH的时候，
         * 计划扣款时间 execute_time不允许传 28 日之后的日期（可以传 28 日），以此避免有些月份可能不存在对应日期的情况。
         */
        private Integer periodType;

    }

    public static class GiveRightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, AdminUserRightsCommonDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }



    public static class SubscribeConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, SubscribeConfig.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

}
