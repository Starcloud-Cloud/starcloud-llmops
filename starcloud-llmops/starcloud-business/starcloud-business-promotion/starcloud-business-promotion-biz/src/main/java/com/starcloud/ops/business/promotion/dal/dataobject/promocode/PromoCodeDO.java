package com.starcloud.ops.business.promotion.dal.dataobject.promocode;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 优惠劵 DO
 *
 * @author 芋道源码
 */
@TableName(value = "promotion_code", autoResultMap = true)
@KeySequence("promotion_code_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
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
    /**
     * 使用时间
     */
    private LocalDateTime useTime;

}
