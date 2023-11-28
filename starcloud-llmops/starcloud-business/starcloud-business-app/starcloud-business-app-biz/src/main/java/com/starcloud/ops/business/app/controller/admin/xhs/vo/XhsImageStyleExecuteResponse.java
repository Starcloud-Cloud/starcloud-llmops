package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsImageStyleExecuteResponse", description = "小红书风格图片执行响应")
public class XhsImageStyleExecuteResponse implements java.io.Serializable {

    private static final long serialVersionUID = 7906252823450071384L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMessage;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private Integer errorCode;

    /**
     * 风格ID
     */
    @Schema(description = "风格ID")
    private String id;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    private String name;

    /**
     * 图片请求
     */
    @Valid
    @Schema(description = "图片响应")
    private List<XhsImageExecuteResponse> imageResponses;

    /**
     * 失败响应
     *
     * @param id           风格ID
     * @param name         风格名称
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     * @return 失败响应
     */
    public static XhsImageStyleExecuteResponse failure(String id, String name, Integer errorCode, String errorMessage, List<XhsImageExecuteResponse> imageResponses) {
        XhsImageStyleExecuteResponse response = new XhsImageStyleExecuteResponse();
        response.setId(id);
        response.setName(name);
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setErrorCode(errorCode);
        response.setImageResponses(CollectionUtils.isEmpty(imageResponses) ? Collections.emptyList() : imageResponses);
        return response;
    }

}

