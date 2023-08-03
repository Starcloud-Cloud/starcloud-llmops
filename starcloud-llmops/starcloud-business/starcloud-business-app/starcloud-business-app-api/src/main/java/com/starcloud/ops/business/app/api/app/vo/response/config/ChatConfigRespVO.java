package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.action.LLMFunctionRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 聊天应用配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用聊天配置响应对象")
public class ChatConfigRespVO extends BaseConfigRespVO {

    private static final long serialVersionUID = 7849659423813192733L;

    /**
     * code
     */
    @Schema(description = "code")
    private String code;

    @Schema(description = "对话提示词")
    private String prePrompt;

    @Schema(description = "模版变量")
    private VariableRespVO variable;

    @Schema(description = "聊天模型参数配置")
    private ModelConfigRespVO modelConfig;

    @Schema(description = "绑定数据集")
    private List<DatesetRespVO> datesetEntities;

    @Schema(description = "聊天建议")
    private SuggestedQuestionRespVO suggestedQuestion;

    @Schema(description = "聊天欢迎语")
    private OpeningStatementRespVO openingStatement;

    @Schema(description = "常用问题")
    private List<CommonQuestionRespVO> commonQuestion;

    @Schema(description = "语音配置")
    private AudioTransciptRespVO audioTransciptEntity;

    @Schema(description = "描述配置")
    private DescriptionRespVo description;

    /**
     * 挂载的 functions 列表
     */
    @Schema(description = "挂载的 functions 列表")
    private List<LLMFunctionRespVO> functions;

}
