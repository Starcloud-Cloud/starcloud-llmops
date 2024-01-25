package com.starcloud.ops.business.user.dal.dataobject.invitation;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 邀请新人配置 DO
 *
 * @author QingX
 */
@TableName("system_user_invite_rule")
@KeySequence("system_user_invite_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInvitationConfigDO extends BaseDO {

    /**
     * 规则自增主键
     */
    @TableId
    private Long id;

    /**
     *  达标人数
     */
    private Long ruleType;

    /**
     *  达标人数
     */
    private Long count;
    /**
     *  限制时间数
     */
    private Integer times;
    /**
     * 限制时间单位
     */
    private Integer timeRange;

    /**
     * 赠送的优惠劵编号的数组
     * <p>
     * 对应 CouponTemplateDO 的 id 属性
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> giveCouponTemplateIds;

    // ========== 权益相关字段 =========
    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = GiveRightsTypeHandler.class)
    private GiveRights giveRights;

    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;


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
