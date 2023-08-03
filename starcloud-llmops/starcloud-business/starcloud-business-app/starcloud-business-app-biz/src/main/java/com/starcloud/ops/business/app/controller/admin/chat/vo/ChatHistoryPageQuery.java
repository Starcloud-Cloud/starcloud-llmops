package com.starcloud.ops.business.app.controller.admin.chat.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "聊天历史查询")
public class ChatHistoryPageQuery extends PageQuery {

    @Schema(description = "会话id")
    @NotBlank(message = "会话uid不能为空")
    private String conversationUid;

}
