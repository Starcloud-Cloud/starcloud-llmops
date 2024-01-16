package com.starcloud.ops.business.user.controller.admin.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 APP - 用户个人信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInfoRespVO {


    @Schema(description = "用户编号", required = true, example = "1")
    private Long id;

    @Schema(description = "状态,参见 CommonStatusEnum 枚举类", required = true, example = "1")
    private Integer status;

    @Schema(description = "用户账号", required = true, example = "yudao")
    private String username;

    @Schema(description = "用户昵称", required = true, example = "芋艿")
    private String nickname;

    @Schema(description = "备注", example = "我是一个用户")
    private String remark;

    @Schema(description = "用户邮箱", example = "yudao@iocoder.cn")
    private String email;

    @Schema(description = "手机号码", example = "15601691300")
    @Mobile
    private String mobile;

    @Schema(description = "用户性别,参见 SexEnum 枚举类", example = "1")
    private Integer sex;

    @Schema(description = "用户头像", example = "https://www.iocoder.cn/xxx.png")
    private String avatar;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "邀请链接")
    private String inviteUrl;

    @Schema(description = "是否新用户")
    private Boolean isNewUser;

    @Schema(description = "注册时间")
    private LocalDateTime registerTime;

    @Schema(description = "优惠结束时间")
    private LocalDateTime endTime;

    @Schema(description = "用户等级")
    private List<Level> levels;

    @Schema(description = "用户权益")
    private List<Rights> rights;

    @Schema(description = "部门 ID")
    private Long deptId;

    @Schema(description = "系统用户会员 - 会员等级")
    @Data
    public static class Level {

        @Schema(description = "等级编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
        private String levelName;

        @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer levelId;

        @Schema(description = "会员等级配置信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private LevelConfig levelConfig;


    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelConfig {

        /**
         *  可使用的应用数
         */
        @Schema(description = "可使用的应用数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer usableApp;

        /**
         *  可使用的基础版机器人数
         */
        @Schema(description = "可使用的基础版机器人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer usableBasicBot;

        /**
         *  可使用的微信机器人数
         */
        @Schema(description = "可使用的微信机器人数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer usableWechatBot;

        /**
         *  可使用的机器人文档数
         */
        @Schema(description = "可使用的机器人文档数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer usableBotDocument;

        /**
         *  技能插件数
         */
        @Schema(description = "技能插件数", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer usableSkillPlugin;



    }

    @Schema(description = "系统用户会员- 会员权益")
    @Data
    public static class Rights {

        @Schema(description = "名称")
        private String name;

        @Schema(description = "类型")
        private String type;

        @Schema(description = "总量")
        private Long totalNum;

        @Schema(description = "使用量")
        private Long usedNum;

        @Schema(description = "剩余量")
        private Long remaining;

        @Schema(description = "权益百分比")
        private Integer percentage;

    }

}
