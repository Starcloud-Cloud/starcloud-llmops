package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import com.starcloud.ops.business.app.dal.databoject.xhs.XhsCreativeContentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-09
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "小红书应用创意执行响应", description = "小红书应用创意执行响应")
public class XhsImageCreativeExecuteResponse implements java.io.Serializable {

    private static final long serialVersionUID = 6728431346119515076L;

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
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String planUid;

    /**
     * 创作方案UID
     */
    @Schema(description = "创作方案UID")
    private String schemeUid;

    /**
     * 业务UID
     */
    @Schema(description = "业务UID")
    private String businessUid;

    /**
     * 创作任务UID
     */
    @Schema(description = "创作任务UID")
    private String contentUid;

    /**
     * 图片风格响应参数
     */
    @Schema(description = "图片风格响应参数")
    private XhsImageStyleExecuteResponse imageStyleResponse;

    /**
     * 失败响应
     *
     * @param request            请求
     * @param errorCode          错误码
     * @param errorMessage       错误信息
     * @param imageStyleResponse 图片风格响应参数
     * @return 失败响应
     */
    public static XhsImageCreativeExecuteResponse failure(XhsImageCreativeExecuteRequest request, Integer errorCode, String errorMessage, XhsImageStyleExecuteResponse imageStyleResponse) {
        XhsImageCreativeExecuteResponse response = new XhsImageCreativeExecuteResponse();
        response.setPlanUid(request.getPlanUid());
        response.setSchemeUid(request.getSchemeUid());
        response.setBusinessUid(request.getBusinessUid());
        response.setContentUid(request.getContentUid());
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setErrorCode(errorCode);
        response.setImageStyleResponse(imageStyleResponse);
        return response;
    }

    /**
     * 失败响应
     *
     * @param content            请求
     * @param errorCode          错误码
     * @param errorMessage       错误信息
     * @param imageStyleResponse 图片风格响应参数
     * @return 失败响应
     */
    public static XhsImageCreativeExecuteResponse failure(XhsCreativeContentDO content, Integer errorCode, String errorMessage, XhsImageStyleExecuteResponse imageStyleResponse) {
        XhsImageCreativeExecuteResponse response = new XhsImageCreativeExecuteResponse();
        if (Objects.nonNull(content)) {
            response.setPlanUid(content.getPlanUid());
            response.setSchemeUid(content.getSchemeUid());
            response.setBusinessUid(content.getBusinessUid());
            response.setContentUid(content.getUid());
        }
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setErrorCode(errorCode);
        response.setImageStyleResponse(imageStyleResponse);
        return response;
    }
}
