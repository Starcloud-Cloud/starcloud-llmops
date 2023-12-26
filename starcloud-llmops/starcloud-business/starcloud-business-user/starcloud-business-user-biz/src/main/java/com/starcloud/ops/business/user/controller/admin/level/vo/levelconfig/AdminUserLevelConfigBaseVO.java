package com.starcloud.ops.business.user.controller.admin.level.vo.levelconfig;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelConfigDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 会员等级 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class AdminUserLevelConfigBaseVO {

    @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "等级不能为空")
    @Positive(message = "等级必须大于 0")
    private Integer level;

    @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotBlank(message = "等级名称不能为空")
    private String name;

    @Schema(description = "所关联的角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "所关联的角色不能为空")
    private Long roleId;

    @Schema(description = "显示顺序不能为空", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    /**
     * 等级配置
     */
    @Schema(description = "等级配置", example = "xxx")
    private LevelConfig levelConfig;

    @Schema(description = "等级图标", example = "https://www.iocoder.cn/yudao.jpg")
    @URL(message = "等级图标必须是 URL 格式")
    private String icon;

    @Schema(description = "等级背景图", example = "https://www.iocoder.cn/yudao.jpg")
    @URL(message = "等级背景图必须是 URL 格式")
    private String backgroundUrl;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;


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

    }


}
