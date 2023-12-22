package com.starcloud.ops.business.user.dal.dataobject.level;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import lombok.*;

import java.util.List;

/**
 * 会员等级 DO
 * <p>
 * 配置每个等级需要的积分
 *
 * @author owen
 */
@TableName("system_user_level_config")
@KeySequence("system_user_level_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserLevelConfigDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 等级
     */
    private Integer level;

    /**
     * 所关联的角色 Id
     * {@link RoleDO#getId()} ()} 字段
     */
    private Long roleId;
    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级图标
     */
    private String icon;

    // ========== 等级配置 =========
    /**
     * 等级配置
     */
    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = LevelConfigTypeHandler.class)
    private List<LevelConfig> levelConfig;
    /**
     * 等级背景图
     */
    private String backgroundUrl;
    /**
     * 状态
     * <p>
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    /**
     * 魔法 AI 专属
     * 商品订阅配置
     *
     * @author Alan Cusack
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelConfig {
        /**
         *  可使用的应用数
         */
        private Boolean usableApp;

        /**
         *  可使用的基础版机器人数
         */
        private Integer usableBasicBot;

        /**
         *  可使用的微信机器人数
         */
        private Integer usableWechatBot;

        /**
         *  可使用的机器人文档数
         */
        private Integer usableBotDocument;

        /**
         *  技能插件数
         */
        private Integer usableSkillPlugin;

    }

    public static class LevelConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, LevelConfig.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


}
