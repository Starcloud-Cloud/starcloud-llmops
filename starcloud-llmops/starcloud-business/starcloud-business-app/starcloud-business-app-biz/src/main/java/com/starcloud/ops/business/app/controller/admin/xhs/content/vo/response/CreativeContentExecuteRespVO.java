package com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response;

import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "创作内容执行结果")
public class CreativeContentExecuteRespVO implements java.io.Serializable {

    private static final long serialVersionUID = 5467176420713541199L;

    /**
     * 是否执行成功
     */
    @Schema(description = "是否执行成功")
    private Boolean success;

    /**
     * 执行错误信息
     */
    @Schema(description = "执行错误信息")
    private String errorMessage;

    /**
     * 创作内容UID
     */
    @Schema(description = "创作内容UID")
    private String uid;

    /**
     * 创作批次UID
     */
    @Schema(description = "创作批次UID")
    private String batchUid;

    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String planUid;

    /**
     * 图片
     */
    @Schema(description = "图片")
    private CreativeContentExecuteResult result;

    /**
     * 生成失败返回结果
     *
     * @param uid          创作内容UID
     * @param errorMessage 错误信息
     * @return 返回结果
     */
    public static CreativeContentExecuteRespVO failure(String uid, String planUid, String batchUid, String errorMessage) {
        CreativeContentExecuteRespVO response = new CreativeContentExecuteRespVO();
        response.setSuccess(Boolean.FALSE);
        response.setUid(uid);
        response.setPlanUid(planUid);
        response.setBatchUid(batchUid);
        response.setErrorMessage(errorMessage);
        return response;
    }

}
