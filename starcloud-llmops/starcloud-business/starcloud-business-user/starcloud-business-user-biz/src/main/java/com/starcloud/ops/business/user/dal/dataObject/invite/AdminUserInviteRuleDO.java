package com.starcloud.ops.business.user.dal.dataobject.invite;

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
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.enums.invite.InviteRuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 邀请新人配置 DO
 *
 * @author QingX
 */
@TableName(value = "system_user_invite_rule", autoResultMap = true)
@KeySequence("system_user_invite_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInviteRuleDO extends BaseDO {

    /**
     * 规则自增主键
     */
    @TableId
    private Long id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 规则类型{@link InviteRuleTypeEnum}
     */
    private Integer type;


    @Schema(description = " 邀请有效时间", example = "1")
    private Integer timeNums;

    @Schema(description = "邀请有效时间单位", example = "30")
    @InEnum(value = TimeRangeTypeEnum.class, message = "邀请有效时间单位，必须是 {value}")
    private Integer timeRange;


    @TableField(typeHandler = RuleTypeHandler.class)
    List<Rule> inviteRule;

    /**
     * 状态
     * <p>
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
    public static class Rule {

        /**
         * 达标人数
         */
        private Long count;

        /**
         * 排序【自动根据】count字段进行排序
         */
        private Integer sort;
        /**
         * 赠送的优惠劵编号的数组
         * <p>
         * 对应 CouponTemplateDO 的 id 属性
         */
        @TableField(typeHandler = JacksonTypeHandler.class)
        private List<Long> giveCouponTemplateIds;

        // ========== 权益相关字段 =========
        @TableField(typeHandler = GiveRightsTypeHandler.class)
        private AdminUserRightsAndLevelCommonDTO giveRights;

    }

    public static class RuleTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseArray(json, Rule.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


    public static class GiveRightsTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, AdminUserRightsAndLevelCommonDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


}
