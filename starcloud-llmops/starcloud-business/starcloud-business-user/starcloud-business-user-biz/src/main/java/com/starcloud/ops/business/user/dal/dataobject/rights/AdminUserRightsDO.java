package com.starcloud.ops.business.user.dal.dataobject.rights;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.app.feign.dto.video.v2.VideoGeneratorConfigV2;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsStatusEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户积分记录 DO
 *
 * @author QingX
 */
@TableName("system_user_rights")
@KeySequence("system_user_rights_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRightsDO extends BaseDO {

    /**
     * 自增主键
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     * <p>
     * 对应 MemberUserDO 的 id 属性
     */
    private Long userId;

    /**
     * 业务编码
     */
    private String bizId;
    /**
     * 业务类型
     * <p>
     * 枚举 {@link AdminUserRightsBizTypeEnum}
     */
    private Integer bizType;


    /**
     * 权益标题
     */
    private String title;
    /**
     * 权益描述
     */
    private String description;
    /**
     * 魔法豆
     */
    private Integer magicBean;
    /**
     * 图片值
     */
    private Integer magicImage;
    /**
     * 矩阵点
     */
    private Integer matrixBean;


    /**
     * 魔法豆初始值
     */
    private Integer magicBeanInit;
    /**
     * 图片初始值
     */
    private Integer magicImageInit;
    /**
     * 矩阵点初始值
     */
    private Integer matrixBeanInit;


    /**
     * '关联用户等级ID'
     */
    private Long userLevelId;

    /**
     * '生效开始时间'
     */
    private LocalDateTime validStartTime;
    /**
     * '生效结束时间'
     */
    private LocalDateTime validEndTime;


    /**
     * 权益状态
     * {@link AdminUserRightsStatusEnum}
     * <p>
     */
    private Integer status;


    @TableField(typeHandler = OriginalFixedRightsTypeHandler.class)
    private  OriginalFixedRights  originalFixedRights;

    @TableField(typeHandler = DynamicRightsTypeHandler.class)
    private DynamicRights dynamicRights;

    @TableField(typeHandler = InvariantRightsTypeHandler.class)
    private InvariantRights invariantRights;



    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class OriginalFixedRights {
        /**
         * 模板数量
         */
        private int templateNums;
        /**
         * 魔法豆
         */
        private Integer magicBean;
        /**
         * 图片值
         */
        private Integer magicImage;
        /**
         * 矩阵点
         */
        private Integer matrixBean;

    }

    public static class OriginalFixedRightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, OriginalFixedRights.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class DynamicRights {
        /**
         * 模板数量
         */
        private int templateNums;
        /**
         * 魔法豆
         */
        private Integer magicBean;
        /**
         * 图片值
         */
        private Integer magicImage;
        /**
         * 矩阵点
         */
        private Integer matrixBean;

    }

    public static class DynamicRightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, DynamicRights.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


    @Data
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class InvariantRights {
        /**
         * 模板数量
         */
        private int templateNums;
        /**
         * 魔法豆
         */
        private Integer magicBean;
        /**
         * 图片值
         */
        private Integer magicImage;
        /**
         * 矩阵点
         */
        private Integer matrixBean;

    }

    public static class InvariantRightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, InvariantRights.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


}
