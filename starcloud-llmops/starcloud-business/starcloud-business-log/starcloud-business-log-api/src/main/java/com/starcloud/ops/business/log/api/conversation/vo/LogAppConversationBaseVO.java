package com.starcloud.ops.business.log.api.conversation.vo;

import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 应用执行日志会话 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 *
 * @author nacoyer
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class LogAppConversationBaseVO implements Serializable {

    private static final long serialVersionUID = 7104048190080172356L;

    /**
     * 会话 UID
     */
    @Schema(description = "会话UID")
    @NotBlank(message = "会话【uid】是必填项！")
    private String uid;

    /**
     * 应用唯一标识
     */
    @Schema(description = "应用唯一标识")
    @NotBlank(message = "应用唯一标识【appUid】是必填项！")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    @NotBlank(message = "应用名称【appName】是必填项！")
    private String appName;

    /**
     * 应用模式
     */
    @Schema(description = "应用模式")
    @NotBlank(message = "应用模式【appMode】是必填项！")
    private String appMode;

    /**
     * 执行场景
     */
    @Schema(description = "执行场景")
    @NotBlank(message = "执行场景【fromScene】是必填项！")
    private String fromScene;

    /**
     * 使用的 ai 模型
     */
    @Schema(description = "使用的 ai 模型")
    private String aiModel;

    /**
     * 应用配置
     */
    @Schema(description = "应用配置")
    @NotBlank(message = "应用配置【appConfig】是必填项！")
    private String appConfig = "{}";

    /**
     * 会话状态，error：失败，success：成功
     */
    @Schema(description = "会话状态，ERROR：失败，SUCCESS：成功")
    @InEnum(value = LogStatusEnum.class, field = InEnum.EnumField.NAME, message = "会话状态[{value}], 不在合法范围内, 有效值：{values}")
    @NotBlank(message = "会话状态【status】是必填项！")
    private String status;

    /**
     * 执行错误码
     */
    @Schema(description = "执行错误码")
    private String errorCode;

    /**
     * 执行错误信息
     */
    @Schema(description = "执行错误信息")
    private String errorMsg;

    /**
     * 终端用户ID
     */
    @Schema(description = "终端用户ID")
    private String endUser;

}