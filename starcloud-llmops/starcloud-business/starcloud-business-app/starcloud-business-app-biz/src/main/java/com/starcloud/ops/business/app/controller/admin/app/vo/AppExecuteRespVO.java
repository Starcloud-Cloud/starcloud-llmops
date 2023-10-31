package com.starcloud.ops.business.app.controller.admin.app.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用执行返回结果
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用类别对象")
public class AppExecuteRespVO implements Serializable {

    private static final long serialVersionUID = 5973120108905794563L;

    @Schema(description = "返回状态")
    private Boolean success;

    @Schema(description = "返回code")
    private String resultCode;

    @Schema(description = "返回描述")
    private String resultDesc;

    @Schema(description = "返回值")
    private Object result;

    /**
     * 会话UID
     */
    @Schema(description = "会话id")
    private String conversationUid;

    /**
     * 成功返回
     *
     * @param resultCode 返回code
     * @param resultDesc 返回描述
     * @param result     返回值
     * @return AppExecuteRespVO
     */
    public static AppExecuteRespVO success(String resultCode, String resultDesc, Object result, String conversationUid) {
        AppExecuteRespVO respVO = new AppExecuteRespVO();
        respVO.setSuccess(Boolean.TRUE);
        respVO.setResultCode(resultCode);
        respVO.setResultDesc(resultDesc);
        respVO.setResult(result);
        respVO.setConversationUid(conversationUid);
        return respVO;
    }

}
