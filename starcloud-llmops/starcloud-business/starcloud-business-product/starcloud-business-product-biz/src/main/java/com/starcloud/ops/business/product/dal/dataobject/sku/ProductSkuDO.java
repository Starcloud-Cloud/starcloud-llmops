package com.starcloud.ops.business.product.dal.dataobject.sku;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.mybatis.core.type.LongListTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.product.api.sku.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.product.dal.dataobject.property.ProductPropertyDO;
import com.starcloud.ops.business.product.dal.dataobject.property.ProductPropertyValueDO;
import com.starcloud.ops.business.product.dal.dataobject.spu.ProductSpuDO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import lombok.*;

import java.util.List;

/**
 * 商品 SKU DO
 *
 * @author 芋道源码
 */
@TableName(value = "product_sku", autoResultMap = true)
@KeySequence("product_sku_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuDO extends BaseDO {

    /**
     * 商品 SKU 编号，自增
     */
    @TableId
    private Long id;
    /**
     * SPU 编号
     * <p>
     * 关联 {@link ProductSpuDO#getId()}
     */
    private Long spuId;
    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = PropertyTypeHandler.class)
    private List<Property> properties;
    /**
     * 商品价格，单位：分
     */
    private Integer price;
    /**
     * 市场价，单位：分
     */
    private Integer marketPrice;
    /**
     * 成本价，单位：分
     */
    private Integer costPrice;

    /**
     * 商品编码
     */
    private String productCode;
    /**
     * 商品条码
     */
    private String barCode;
    /**
     * 图片地址
     */
    private String picUrl;
    /**
     * 库存
     */
    private Integer stock;
    /**
     * 商品重量，单位：kg 千克
     */
    private Double weight;
    /**
     * 商品体积，单位：m^3 平米
     */
    private Double volume;


    /**
     * 赠送的优惠劵编号的数组
     * <p>
     * 对应 CouponTemplateDO 的 id 属性
     */
    @TableField(typeHandler = LongListTypeHandler.class)
    private List<Long> giveCouponTemplateIds;

    /**
     * 商品附属权益
     */
    @TableField(typeHandler = RightsTypeHandler.class)
    private AdminUserRightsAndLevelCommonDTO rightsConfig;
    /**
     * 商品签约配置
     */
    @TableField(typeHandler = SubscribeConfigTypeHandler.class)
    private SubscribeConfigDTO subscribeConfig;

    @TableField(typeHandler = OrderLimitConfigTypeHandler.class)
    private OrderLimitConfig orderLimitConfig;


    /**
     * 活动详情
     */
    private String activeData;


    /**
     * 一级分销的佣金，单位：分
     */
    private Integer firstBrokeragePrice;
    /**
     * 二级分销的佣金，单位：分
     */
    private Integer secondBrokeragePrice;

    // ========== 营销相关字段 =========

    // ========== 统计相关字段 =========
    /**
     * 商品销量
     */
    private Integer salesCount;

    /**
     * 商品属性
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {

        /**
         * 属性编号
         * 关联 {@link ProductPropertyDO#getId()}
         */
        private Long propertyId;
        /**
         * 属性名字
         * 冗余 {@link ProductPropertyDO#getName()}
         * <p>
         * 注意：每次属性名字发生变化时，需要更新该冗余
         */
        private String propertyName;

        /**
         * 属性值编号
         * 关联 {@link ProductPropertyValueDO#getId()}
         */
        private Long valueId;
        /**
         * 属性值名字
         * 冗余 {@link ProductPropertyValueDO#getName()}
         * <p>
         * 注意：每次属性值名字发生变化时，需要更新该冗余
         */
        private String valueName;
        /**
         * 属性值备注 冗余 ProductPropertyValueDO.getRemark()
         * 注意：每次属性值备注发生变化时，需要更新该冗余
         */
        private String remark;

    }

    /**
     * 下单限制配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderLimitConfig {

        /**
         * 是否限制仅新用户下单
         * 关联 {@link ProductPropertyDO#getId()}
         */
        private Boolean isNewUser;
        /**
         * 限制的优惠券模板编号
         */
        private List<Long> limitCouponTemplateId;

    }





    // TODO @芋艿：可以找一些新的思路
    public static class OrderLimitConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, OrderLimitConfig.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

    // TODO @芋艿：可以找一些新的思路
    public static class PropertyTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseArray(json, Property.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


    public static class RightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, AdminUserRightsAndLevelCommonDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

    public static class SubscribeConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, SubscribeConfigDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


    // TODO @芋艿：可以找一些新的思路

    // TODO 芋艿：integral from y
    // TODO 芋艿：pinkPrice from y
    // TODO 芋艿：seckillPrice from y
    // TODO 芋艿：pinkStock from y
    // TODO 芋艿：seckillStock from y

}

