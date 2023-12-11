package com.starcloud.ops.business.app.api.xhs.execute;

import com.starcloud.ops.business.app.api.xhs.scheme.dto.CopyWritingContentDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@Schema(name = "XhsAppExecuteResponse", description = "小红书应用请求")
public class XhsAppExecuteResponse implements java.io.Serializable {

    private static final long serialVersionUID = 3336342789222957644L;

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    private String uid;

    /**
     * 文案数据
     */
    @Schema(description = "文案数据")
    private CopyWritingContentDTO copyWriting;

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
     * 成功
     *
     * @param uid         appUid
     * @param copyWriting 文案对象
     * @return 结果
     */
    public static XhsAppExecuteResponse success(String uid, CopyWritingContentDTO copyWriting) {
        XhsAppExecuteResponse response = new XhsAppExecuteResponse();
        response.setSuccess(Boolean.TRUE);
        response.setUid(uid);
        response.setCopyWriting(copyWriting);
        return response;
    }

    /**
     * 成功
     *
     * @param uid         appUid
     * @param copyWriting 文案对象
     * @return 结果
     */
    public static List<XhsAppExecuteResponse> success(String uid, CopyWritingContentDTO copyWriting, Integer n) {
        List<XhsAppExecuteResponse> responses = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            responses.add(success(uid, copyWriting));
        }
        return responses;
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 结果
     */
    public static XhsAppExecuteResponse failure(String errorCode, String errorMsg) {
        return failure(null, errorCode, errorMsg);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param n         生成数量
     * @return 结果
     */
    public static List<XhsAppExecuteResponse> failure(String errorCode, String errorMsg, Integer n) {
        List<XhsAppExecuteResponse> responses = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            responses.add(failure(errorCode, errorMsg));
        }
        return responses;
    }

    /**
     * 失败
     *
     * @param uid       AppUid
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @return 结果
     */
    public static XhsAppExecuteResponse failure(String uid, String errorCode, String errorMsg) {
        XhsAppExecuteResponse response = new XhsAppExecuteResponse();
        response.setSuccess(Boolean.FALSE);
        response.setUid(uid);
        response.setErrorCode(errorCode);
        response.setErrorMsg(errorMsg);
        return response;
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param n         生成数量
     * @return 结果
     */
    public static List<XhsAppExecuteResponse> failure(String uid, String errorCode, String errorMsg, Integer n) {
        List<XhsAppExecuteResponse> responses = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            responses.add(failure(uid, errorCode, errorMsg));
        }
        return responses;
    }
}
