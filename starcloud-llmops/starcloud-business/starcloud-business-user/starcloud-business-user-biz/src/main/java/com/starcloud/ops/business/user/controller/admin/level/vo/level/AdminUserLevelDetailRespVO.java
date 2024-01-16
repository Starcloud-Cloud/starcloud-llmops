package com.starcloud.ops.business.user.controller.admin.level.vo.level;

import cn.hutool.core.bean.BeanUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig.AdminUserLevelConfigBaseVO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import com.starcloud.ops.business.user.enums.level.AdminUserLevelBizTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 会员等级创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AdminUserLevelDetailRespVO extends AdminUserLevelBaseVO {

    /**
     * 等级配置
     */
    @Schema(description = "等级配置", example = "xxx")
    private LevelConfig levelConfig;

    /**
     * 等级配置
     */
    @Schema(description = " 显示排序", example = "xxx")
    private Integer sort;

    /**
     * 魔法 AI 专属
     * 商品订阅配置
     *
     * @author Alan Cusack
     */
    @Schema(description = "等级配置")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelConfig {
        /**
         *  可使用的应用数
         */
        private Integer usableApp;

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

        /**
         *  可创建的团队数
         */
        private Integer usableTeams;


        /**
         *  团队可以添加的人数
         */
        private Integer usableTeamUsers;


    }



}
