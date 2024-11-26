package com.starcloud.ops.business.user.dal.dataobject.level;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员等级记录 DO
 * <p>
 * 用户每次等级发生变更时，记录一条日志
 *
 * @author owen
 */
@TableName(value ="system_user_level", autoResultMap = true)
@KeySequence("system_user_level_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserLevelDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     * <p>
     * 关联 {@link AdminUserDO#getId()} 字段
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
     * 等级编号
     * <p>
     * 关联 {@link AdminUserLevelConfigDO#getId()} 字段
     */
    private Long levelId;
    /**
     * 等级名称
     * <p>
     * 冗余 {@link AdminUserLevelConfigDO#getName()} 字段
     */
    private String levelName;

    /**
     * 属性数组，JSON 格式
     */
    @TableField(typeHandler = LevelConfigTypeHandler.class)
    private LevelConfigDTO levelConfig;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 生效开始时间
     */
    private LocalDateTime validStartTime;
    /**
     * 生效结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 备注
     */
    private String remark;
    /**
     * 描述
     */
    private String description;



    public static class LevelConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, LevelConfigDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

}
