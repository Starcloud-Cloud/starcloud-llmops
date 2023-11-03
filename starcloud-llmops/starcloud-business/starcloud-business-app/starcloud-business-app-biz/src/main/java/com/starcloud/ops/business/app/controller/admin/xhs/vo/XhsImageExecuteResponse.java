package com.starcloud.ops.business.app.controller.admin.xhs.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsImageExecuteResponse", description = "小红书图片响应！")
public class XhsImageExecuteResponse implements java.io.Serializable {

    private static final long serialVersionUID = 3336342789222957644L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 应用UID
     */
    @Schema(description = "图片UID")
    private String imageTemplate;

    /**
     * 应用UID
     */
    @Schema(description = "图片序号")
    private Integer index;

    /**
     * 是否主图
     */
    @Schema(description = "是否主图")
    private Boolean isMain;

    /**
     * 应用生成参数
     */
    @Schema(description = "返回数据")
    private String url;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private String errorCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 基础响应
     *
     * @return XhsImageExecuteResponse
     */
    public static XhsImageExecuteResponse ofBase() {
        XhsImageExecuteResponse response = new XhsImageExecuteResponse();
        response.setSuccess(Boolean.FALSE);
        return response;
    }
}
