package com.starcloud.ops.business.app.api.xhs.execute;

import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CreativeAppExecuteResponse implements java.io.Serializable {

    private static final long serialVersionUID = 4051481805044366802L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private Integer errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创意内容id
     */
    private Long contentId;

    /**
     * 创意内容uid
     */
    private String contentUid;

    /**
     * 创意方案id
     */
    private String businessUid;

    /**
     * 创意方案id
     */
    private String schemeUid;

    /**
     * 创意计划id
     */
    private String planUid;

    /**
     * 文案数据
     */
    private CopyWritingContentDTO copyWritingContent;

    /**
     * 图片数据
     */
    private List<PosterImageDTO> posterList;

    /**
     * 失败
     *
     * @param code    错误码
     * @param message 错误信息
     * @return CreativeAppExecuteResponse
     */
    public static CreativeAppExecuteResponse failure(Integer code, String message) {
        CreativeAppExecuteResponse response = new CreativeAppExecuteResponse();
        response.setSuccess(Boolean.FALSE);
        response.setErrorCode(code);
        response.setErrorMessage(message);
        return response;
    }
}
